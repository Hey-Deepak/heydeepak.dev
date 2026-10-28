package dev.heydeepak.site.components

import androidx.compose.runtime.*
import dev.heydeepak.site.HERO_ID
import dev.heydeepak.site.SiteSection
import dev.heydeepak.site.isMacLike
import org.jetbrains.compose.web.dom.*

@Composable
fun TopBar(activeSection: String?, onOpenPalette: () -> Unit) {
    Header(attrs = { classes("topbar") }) {
        Div(attrs = { classes("wrap", "topbar-inner") }) {
            A(href = "#$HERO_ID", attrs = { classes("logo"); attr("aria-label", "Deepak Choudhary, back to top") }) {
                Span(attrs = { classes("logo-user") }) { Text("deepak") }
                Span(attrs = { classes("logo-at") }) { Text("@") }
                Span(attrs = { classes("logo-host") }) { Text("heydeepak.dev") }
            }
            Nav(attrs = { classes("nav"); attr("aria-label", "Sections") }) {
                SiteSection.entries.forEach { section ->
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
                    attr("aria-label", "Open command menu")
                    attr("aria-haspopup", "dialog")
                    onClick { onOpenPalette() }
                }) {
                    Span(attrs = { classes("k-desktop") }) { Text(if (isMacLike()) "⌘K" else "Ctrl K") }
                    Span(attrs = { classes("k-mobile") }) { Text("Menu") }
                }
            }
        }
    }
}
