package dev.heydeepak.site.components

import androidx.compose.runtime.Composable
import dev.heydeepak.site.Profile
import org.jetbrains.compose.web.dom.*

/** The terminal window's title bar: just the session, and the resume always one tap away. */
@Composable
fun TitleBar() {
    Header(attrs = { classes("titlebar") }) {
        Span(attrs = { classes("titlebar-title") }) { Text("deepak@heydeepak.dev: ~") }
        ExternalLink(Profile.RESUME, "titlebar-resume") {
            Span(attrs = { attr("aria-hidden", "true") }) { Text("↓") }
            Text("resume.pdf")
        }
    }
}
