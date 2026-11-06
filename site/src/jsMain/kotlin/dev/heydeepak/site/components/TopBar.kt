package dev.heydeepak.site.components

import androidx.compose.runtime.Composable
import dev.heydeepak.site.Profile
import org.jetbrains.compose.web.dom.*

/** The terminal window's title bar: the session, the agents' switch once they're done, and the resume always one tap away. */
@Composable
fun TitleBar(swarm: Swarm? = null) {
    Header(attrs = { classes("titlebar") }) {
        swarm?.let { SwarmToggle(it) }
        Span(attrs = { classes("titlebar-title") }) { Text("deepak@heydeepak.dev: ~") }
        ExternalLink(Profile.RESUME, "titlebar-resume") {
            Span(attrs = { attr("aria-hidden", "true") }) { Text("↓") }
            Text("resume.pdf")
        }
    }
}
