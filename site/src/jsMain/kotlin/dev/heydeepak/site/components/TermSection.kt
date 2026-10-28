package dev.heydeepak.site.components

import androidx.compose.runtime.*
import dev.heydeepak.site.LocalMotion
import dev.heydeepak.site.observeOnce
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.dom.*

private const val SECTION_KEYSTROKE_MS = 24L

/**
 * A page section framed as a shell command and its output. On entering the viewport the content
 * fades in at once; the command types alongside it as decoration and never gates the content.
 */
@Composable
fun TermSection(id: String, command: String, title: String, content: @Composable () -> Unit) {
    val motion = LocalMotion.current
    var inView by remember { mutableStateOf(!motion) }
    var typed by remember { mutableStateOf(if (motion) 0 else command.length) }

    LaunchedEffect(inView) {
        if (!motion || !inView) return@LaunchedEffect
        for (count in 1..command.length) {
            delay(SECTION_KEYSTROKE_MS)
            typed = count
        }
    }
    val done = typed == command.length

    Section(attrs = {
        id(id)
        classes("section")
        attr("tabindex", "-1")
        attr("aria-labelledby", "$id-title")
        if (motion) {
            ref { element ->
                val stop = observeOnce(element, rootMargin = "0px") { inView = true }
                onDispose { stop() }
            }
        }
    }) {
        Div(attrs = { classes("wrap") }) {
            Div(attrs = { classes("prompt"); attr("aria-hidden", "true") }) {
                Span(attrs = { classes("prompt-sign") }) { Text("$") }
                Span(attrs = { classes("prompt-cmd") }) { Text(command.take(typed)) }
                if (!done) Cursor(solid = typed > 0)
            }
            Div(attrs = {
                classes("section-body")
                if (!inView) classes("is-pending")
            }) {
                H2(attrs = { id("$id-title"); classes("h2") }) {
                    Span(attrs = { classes("h2-mark"); attr("aria-hidden", "true") }) { Text("▸") }
                    Text(title)
                }
                content()
            }
        }
    }
}

@Composable
fun Cursor(solid: Boolean = false) {
    Span(attrs = {
        classes("cursor")
        if (solid) classes("is-solid")
        attr("aria-hidden", "true")
    })
}

@Composable
fun Chips(chips: List<String>) {
    if (chips.isEmpty()) return
    Ul(attrs = { classes("chips"); attr("aria-label", "Tech") }) {
        chips.forEach { chip -> Li(attrs = { classes("chip") }) { Text(chip) } }
    }
}

@Composable
fun ExternalLink(href: String, vararg classNames: String, content: @Composable () -> Unit) {
    A(href = href, attrs = {
        classes(*classNames)
        attr("target", "_blank")
        attr("rel", "noopener noreferrer")
    }) { content() }
}
