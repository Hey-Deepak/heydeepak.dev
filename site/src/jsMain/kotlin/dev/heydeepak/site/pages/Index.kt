package dev.heydeepak.site.pages

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import dev.heydeepak.site.*
import dev.heydeepak.site.components.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

private const val TOAST_MS = 1800L
// A section is current once its top passes this fraction of the viewport height (below the sticky bar).
private const val SCROLL_SPY_LINE = 0.35
private const val PAGE_BOTTOM_SLACK_PX = 2
private const val MAIN_ID = "content"

private class Toast(val message: String, val id: Int)

@Page
@Composable
fun HomePage() {
    val motion = remember { motionAllowed() }
    // Read before markAppReady() below, in the same composition, so the grace timer can't fire in between.
    val heroMotion = remember { motion && bakedHeroHidden() }
    DisposableEffect(Unit) {
        markAppReady()
        onDispose { }
    }
    var paletteOpen by remember { mutableStateOf(false) }
    var activeSection by remember { mutableStateOf<String?>(null) }
    var toast by remember { mutableStateOf<Toast?>(null) }

    LaunchedEffect(toast?.id) {
        if (toast != null) {
            delay(TOAST_MS)
            toast = null
        }
    }

    val actions = remember {
        fun showToast(message: String) {
            toast = Toast(message, (toast?.id ?: 0) + 1)
        }
        SiteSection.entries.map { section ->
            PaletteAction("go", "Go to ${section.label}", "#${section.id}") { scrollToSection(section.id, motion) }
        } + listOf(
            PaletteAction("copy", "Copy email", Profile.EMAIL) {
                copyToClipboard(
                    Profile.EMAIL,
                    onCopied = { showToast("Email copied") },
                    onFailed = { showToast("Couldn't copy. Email: ${Profile.EMAIL}") },
                )
            },
            PaletteAction("open", "Download resume", "resume.pdf") { openInNewTab(Profile.RESUME) },
            PaletteAction("open", "Open GitHub", "github.com/Hey-Deepak") { openInNewTab(Profile.GITHUB) },
            PaletteAction("open", "Open LinkedIn", "in/hey-deepak") { openInNewTab(Profile.LINKEDIN) },
        )
    }

    GlobalShortcuts(
        isPaletteOpen = { paletteOpen },
        setPaletteOpen = { paletteOpen = it },
    )
    ScrollSpy { activeSection = it }

    CompositionLocalProvider(LocalMotion provides motion) {
        A(href = "#$MAIN_ID", attrs = { classes("skip-link") }) { Text("Skip to content") }
        TopBar(activeSection, onOpenPalette = { paletteOpen = true })
        Main(attrs = { id(MAIN_ID); attr("tabindex", "-1") }) {
            CompositionLocalProvider(LocalMotion provides heroMotion) { Hero() }
            AboutSection()
            WorkSection()
            CareerSection()
            StackSection()
            BuildingSection()
            ContactSection()
        }
        SiteFooter()
        if (paletteOpen) CommandPalette(actions, onClose = { paletteOpen = false })
        Div(attrs = { classes("toast-region"); attr("role", "status"); attr("aria-live", "polite") }) {
            toast?.let { current ->
                key(current.id) { Div(attrs = { classes("toast") }) { Text(current.message) } }
            }
        }
    }
}

/** ⌘K / Ctrl+K toggles the palette, `/` opens it, Esc closes it. */
@Composable
private fun GlobalShortcuts(isPaletteOpen: () -> Boolean, setPaletteOpen: (Boolean) -> Unit) {
    DisposableEffect(Unit) {
        val listener: (Event) -> Unit = listener@{ event ->
            val key = event as? KeyboardEvent ?: return@listener
            when {
                (key.metaKey || key.ctrlKey) && key.key.equals("k", ignoreCase = true) -> {
                    key.preventDefault()
                    setPaletteOpen(!isPaletteOpen())
                }
                key.key == "/" && !isPaletteOpen() && !isTyping() -> {
                    key.preventDefault()
                    setPaletteOpen(true)
                }
                key.key == "Escape" && isPaletteOpen() -> setPaletteOpen(false)
            }
        }
        window.addEventListener("keydown", listener)
        onDispose { window.removeEventListener("keydown", listener) }
    }
}

private fun isTyping(): Boolean {
    val focused = document.activeElement as? HTMLElement ?: return false
    return focused.tagName == "INPUT" || focused.tagName == "TEXTAREA" || focused.isContentEditable
}

/** Reports the nav item for the scroll position; sections outside the nav count as the preceding nav item. */
@Composable
private fun ScrollSpy(onActive: (String?) -> Unit) {
    DisposableEffect(Unit) {
        if (isExporting()) return@DisposableEffect onDispose { }
        var frame = 0
        fun update() {
            frame = 0
            val atBottom = window.scrollY + window.innerHeight >= document.documentElement!!.scrollHeight - PAGE_BOTTOM_SLACK_PX
            if (atBottom) return onActive(SiteSection.entries.last { it.inNav }.id)
            val line = window.innerHeight * SCROLL_SPY_LINE
            var active: String? = null
            for (section in SiteSection.entries) {
                val element = document.getElementById(section.id) ?: continue
                if (element.getBoundingClientRect().top > line) break
                if (section.inNav) active = section.id
            }
            onActive(active)
        }
        val listener: (Event) -> Unit = { if (frame == 0) frame = window.requestAnimationFrame { update() } }
        window.addEventListener("scroll", listener)
        window.addEventListener("resize", listener)
        update()
        onDispose {
            window.removeEventListener("scroll", listener)
            window.removeEventListener("resize", listener)
            if (frame != 0) window.cancelAnimationFrame(frame)
        }
    }
}
