package dev.heydeepak.site.components

import androidx.compose.runtime.*
import dev.heydeepak.site.focusWithoutScroll
import dev.heydeepak.site.isTouchOnly
import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement

class PaletteAction(val group: String, val label: String, val hint: String, val run: () -> Unit)

private const val OPTION_ID_PREFIX = "palette-option-"

@Composable
fun CommandPalette(actions: List<PaletteAction>, onClose: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf(0) }
    // Navigation actions move focus to their target, so only restore focus when nothing ran.
    var ranAction by remember { mutableStateOf(false) }
    // Functions, not values: key handlers must see the latest query even before the next recomposition.
    fun matching() = actions.filter { action ->
        query.isBlank() || "${action.group} ${action.label} ${action.hint}".contains(query.trim(), ignoreCase = true)
    }
    fun activeIn(count: Int) = selected.coerceIn(0, (count - 1).coerceAtLeast(0))
    val matches = matching()
    val active = activeIn(matches.size)

    fun run(action: PaletteAction) {
        ranAction = true
        onClose()
        action.run()
    }

    DisposableEffect(Unit) {
        val previous = document.activeElement as? HTMLElement
        onDispose { if (!ranAction) previous?.let(::focusWithoutScroll) }
    }

    Div(attrs = {
        classes("palette-backdrop")
        onClick { onClose() }
    }) {
        Div(attrs = {
            classes("palette")
            attr("role", "dialog")
            attr("aria-modal", "true")
            attr("aria-label", "Command menu")
            onClick { it.stopPropagation() }
        }) {
            Div(attrs = { classes("palette-search") }) {
                Span(attrs = { classes("palette-sign"); attr("aria-hidden", "true") }) { Text("›") }
                Input(type = InputType.Text) {
                    classes("palette-input")
                    placeholder("Type a command")
                    value(query)
                    attr("role", "combobox")
                    attr("aria-expanded", "true")
                    attr("aria-controls", "palette-list")
                    attr("autocomplete", "off")
                    attr("spellcheck", "false")
                    if (matches.isNotEmpty()) attr("aria-activedescendant", "$OPTION_ID_PREFIX$active")
                    onInput {
                        query = it.value
                        selected = 0
                    }
                    onKeyDown { event ->
                        val current = matching()
                        val index = activeIn(current.size)
                        when (event.key) {
                            "ArrowDown" -> {
                                event.preventDefault()
                                if (current.isNotEmpty()) selected = (index + 1) % current.size
                            }
                            "ArrowUp" -> {
                                event.preventDefault()
                                if (current.isNotEmpty()) selected = (index - 1 + current.size) % current.size
                            }
                            "Enter" -> {
                                event.preventDefault()
                                current.getOrNull(index)?.let(::run)
                            }
                            "Escape" -> {
                                event.preventDefault()
                                onClose()
                            }
                            // The input is the dialog's only focus stop; keep focus inside it.
                            "Tab" -> event.preventDefault()
                        }
                    }
                    ref { element ->
                        // On touch screens focusing would pop the keyboard over a menu meant for tapping.
                        if (!isTouchOnly()) element.focus()
                        onDispose { }
                    }
                }
            }
            Ul(attrs = {
                id("palette-list")
                classes("palette-list")
                attr("role", "listbox")
            }) {
                matches.forEachIndexed { index, action ->
                    Li(attrs = {
                        id("$OPTION_ID_PREFIX$index")
                        classes("palette-item")
                        if (index == active) classes("is-active")
                        attr("role", "option")
                        attr("aria-selected", (index == active).toString())
                        onMouseMove { if (selected != index) selected = index }
                        onClick { run(action) }
                    }) {
                        Span(attrs = { classes("pi-group") }) { Text(action.group) }
                        Span(attrs = { classes("pi-label") }) { Text(action.label) }
                        Span(attrs = { classes("pi-hint") }) { Text(action.hint) }
                    }
                }
                if (matches.isEmpty()) {
                    Li(attrs = { classes("palette-empty") }) { Text("No match. Try \"work\" or \"email\".") }
                }
            }
            Div(attrs = { classes("palette-foot"); attr("aria-hidden", "true") }) {
                Span { Span(attrs = { classes("kbd") }) { Text("↑↓") }; Text("move") }
                Span { Span(attrs = { classes("kbd") }) { Text("↵") }; Text("run") }
                Span { Span(attrs = { classes("kbd") }) { Text("esc") }; Text("close") }
            }
        }
    }
}
