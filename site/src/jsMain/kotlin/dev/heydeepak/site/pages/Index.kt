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
// A thin band across the middle of the viewport decides which section is "current".
private const val SCROLL_SPY_MARGIN = "-45% 0px -50% 0px"

private class Toast(val message: String, val id: Int)

@Page
@Composable
fun HomePage() {
    val motion = remember { motionAllowed() }
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
        TopBar(activeSection, onOpenPalette = { paletteOpen = true })
        Main {
            Hero()
            WorkSection()
            CareerSection()
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

/** Reports the section crossing the middle of the viewport (null over the hero). */
@Composable
private fun ScrollSpy(onActive: (String?) -> Unit) {
    DisposableEffect(Unit) {
        if (isExporting()) return@DisposableEffect onDispose { }
        val options: dynamic = js("({})")
        options.rootMargin = SCROLL_SPY_MARGIN
        val observer = IntersectionObserver({ entries, _ ->
            entries.filter { it.isIntersecting }.forEach { entry ->
                onActive(entry.target.id.takeIf { it != HERO_ID })
            }
        }, options)
        (listOf(HERO_ID) + SiteSection.entries.map { it.id })
            .mapNotNull { document.getElementById(it) }
            .forEach(observer::observe)
        onDispose { observer.disconnect() }
    }
}
