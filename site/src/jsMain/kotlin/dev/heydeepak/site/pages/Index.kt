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
private const val MAIN_ID = "content"

private class Toast(val message: String, val id: Int)

@Page
@Composable
fun HomePage() {
    val motion = remember { motionAllowed() }
    // Read before markAppReady() below, in the same composition, so the grace timers can't fire in between.
    val promptMotion = remember { motion && bakedHeroHidden() }
    val textMotion = remember { promptMotion && bakedTextHidden() }
    // The export prints every section; so does the app when the grace timer already showed the baked transcript.
    val fullTranscript = remember { isExporting() || !bakedHeroHidden() }
    val swarm = remember { swarmFor(motion, textMotion) }
    DisposableEffect(Unit) {
        markAppReady()
        onDispose { }
    }
    // After the first render, so the agents find the blocks they build.
    DisposableEffect(swarm) {
        swarm?.start()
        onDispose { swarm?.dispose() }
    }
    val scope = rememberCoroutineScope()
    val shell = remember { ShellState(scope, promptMotion, fullTranscript, swarm) }
    var paletteOpen by remember { mutableStateOf(false) }
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
        sectionCommands.map { command ->
            PaletteAction("run", command.section.id, command.section.title) { shell.runTyped(command.section.id) }
        } + listOf(
            PaletteAction("run", "help", "every command") { shell.runTyped("help") },
            PaletteAction("run", "clear", "clear the screen") { shell.clear() },
            PaletteAction("copy", "Copy email", Profile.EMAIL) {
                copyToClipboard(
                    Profile.EMAIL,
                    onCopied = { showToast("Email copied") },
                    onFailed = { showToast("Couldn’t copy. Email: ${Profile.EMAIL}") },
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
        onClear = { shell.clear() },
        onEscape = { swarm?.skip() },
    )

    A(href = "#$SHELL_INPUT_ID", attrs = { classes("skip-link") }) { Text("Skip to the command prompt") }
    Div(attrs = { classes("term-page") }) {
        Main(attrs = { id(MAIN_ID); classes("term"); attr("tabindex", "-1") }) {
            TitleBar(swarm)
            CompositionLocalProvider(LocalMotion provides promptMotion, LocalTextMotion provides textMotion) {
                Shell(shell)
            }
        }
    }
    if (paletteOpen) CommandPalette(actions, onClose = { paletteOpen = false })
    Div(attrs = { classes("toast-region"); attr("role", "status"); attr("aria-live", "polite") }) {
        toast?.let { current ->
            key(current.id) { Div(attrs = { classes("toast") }) { Text(current.message) } }
        }
    }
}

/**
 * ⌘K / Ctrl+K toggles the palette, `/` opens it, Esc closes it (or skips the agents' build), Ctrl+L clears
 * the screen. Typing a printable key while nothing has focus goes to the prompt, like a terminal that's always listening.
 */
@Composable
private fun GlobalShortcuts(isPaletteOpen: () -> Boolean, setPaletteOpen: (Boolean) -> Unit, onClear: () -> Unit, onEscape: () -> Unit) {
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
                key.key == "Escape" -> onEscape()
                key.ctrlKey && key.key.equals("l", ignoreCase = true) && !isPaletteOpen() && !isTyping() -> {
                    key.preventDefault()
                    onClear()
                }
                // No preventDefault: focusing the prompt now lets this very key land in it.
                isPrintable(key) && !isPaletteOpen() && nothingFocused() -> focusById(SHELL_INPUT_ID)
            }
        }
        window.addEventListener("keydown", listener)
        onDispose { window.removeEventListener("keydown", listener) }
    }
}

private fun isPrintable(key: KeyboardEvent) = key.key.length == 1 && !key.metaKey && !key.ctrlKey && !key.altKey

private fun isTyping(): Boolean {
    val focused = document.activeElement as? HTMLElement ?: return false
    return focused.tagName == "INPUT" || focused.tagName == "TEXTAREA" || focused.isContentEditable
}

/** Nothing interactive has focus (the page, the window or the transcript), so a keystroke is meant for the prompt. */
private fun nothingFocused(): Boolean {
    val focused = document.activeElement as? HTMLElement ?: return true
    return focused == document.body || focused.id == MAIN_ID || focused.id == TRANSCRIPT_ID
}
