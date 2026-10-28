package dev.heydeepak.site.components

import androidx.compose.runtime.*
import dev.heydeepak.site.HERO_ID
import dev.heydeepak.site.SiteSection
import dev.heydeepak.site.isMacLike
import org.jetbrains.compose.web.dom.*

@Composable
fun TopBar(activeSection: String?, onOpenPalette: () -> Unit) {
    val shortcut = if (isMacLike()) "⌘K" else "Ctrl K"
    Header(attrs = { classes("topbar") }) {
        Div(attrs = { classes("wrap", "topbar-inner") }) {
            A(href = "#$HERO_ID", attrs = { classes("logo"); attr("aria-label", "deepak@heydeepak.dev, back to top") }) {
                Span(attrs = { classes("logo-user") }) { Text("deepak") }
                Span(attrs = { classes("logo-at") }) { Text("@") }
                Span(attrs = { classes("logo-host") }) { Text("heydeepak.dev") }
            }
            Nav(attrs = { classes("nav"); attr("aria-label", "Sections") }) {
                SiteSection.entries.filter { it.inNav }.forEach { section ->
                    A(href = "#${section.id}", attrs = {
                        classes("nav-link")
                        if (section.id == activeSection) {
                            classes("is-active")
                            attr("aria-current", "location")
                        }
                    }) { Text(section.label) }
                }
                Button(attrs = {
                    classes("k-btn")
                    attr("type", "button")
                    // Includes the visible shortcut so voice-control users can say what they see.
                    attr("aria-label", "Open command menu $shortcut")
                    attr("aria-haspopup", "dialog")
                    onClick { onOpenPalette() }
                }) {
                    Span(attrs = { classes("k-desktop") }) { Text(shortcut) }
                    Span(attrs = { classes("k-mobile") }) { Text("Menu") }
                }
            }
        }
    }
}
