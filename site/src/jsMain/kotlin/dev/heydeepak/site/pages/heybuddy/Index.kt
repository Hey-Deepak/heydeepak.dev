package dev.heydeepak.site.pages.heybuddy

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import dev.heydeepak.site.*
import dev.heydeepak.site.components.*
import org.jetbrains.compose.web.dom.*

/** heydeepak.dev/heybuddy: what Hey Buddy is, how to set it up, and how to get the beta. */
@Page
@Composable
fun HeyBuddyPage() {
    DisposableEffect(Unit) {
        val restore = applyPageHead(HeyBuddy.head)
        onDispose { restore() }
    }
    Div(attrs = { classes("term-page") }) {
        Main(attrs = { classes("term") }) {
            Header(attrs = { classes("titlebar", "hb-titlebar") }) {
                Span(attrs = { classes("titlebar-title") }) { Text(HeyBuddy.WINDOW_TITLE) }
            }
            Div(attrs = {
                classes("transcript", "hb")
                attr("role", "region")
                attr("aria-label", "Hey Buddy")
                attr("tabindex", "0")
            }) {
                Entry("cat README.md") { Intro() }
                Entry("cat SETUP.md", HeyBuddy.SETUP_ID) { Setup() }
                Entry("heybuddy links") { Keyed(HeyBuddy.LINKS_TITLE, HeyBuddy.links) }
                Entry("cat SECURITY.md") { Keyed(HeyBuddy.SECURITY_TITLE, HeyBuddy.security) }
                Entry("ls ./download", HeyBuddy.DOWNLOAD_ID) { Download() }
                Entry("cat FAQ.md") { Faqs() }
                P(attrs = { classes("t-comment", "hb-note") }) { Text("# ${HeyBuddy.DISCLAIMER}") }
                HomeLine()
            }
        }
    }
}

/** One command and its output. The prompt is plain text, never hidden waiting for the app. */
@Composable
private fun Entry(command: String, anchor: String? = null, output: @Composable () -> Unit) {
    Section(attrs = {
        classes("entry")
        anchor?.let { id(it) }
    }) {
        Div(attrs = { classes("prompt"); attr("aria-hidden", "true") }) {
            Span(attrs = { classes("prompt-sign") }) { Text("$") }
            Text(" $command")
        }
        Div(attrs = { classes("out") }) { output() }
    }
}

@Composable
private fun SectionTitle(title: String) {
    H2(attrs = { classes("h2") }) {
        Span(attrs = { classes("h2-mark"); attr("aria-hidden", "true") }) { Text("▸") }
        Text(title)
    }
}

/** Plain text where `backticked` parts are drawn as code. */
@Composable
private fun WithCode(text: String) {
    text.split('`').forEachIndexed { index, part ->
        if (index % 2 == 1) Code(attrs = { classes("man-cmd") }) { Text(part) } else Text(part)
    }
}

@Composable
private fun Intro() {
    Div(attrs = { classes("hb-hero") }) {
        Div(attrs = { classes("hb-hero-grid") }) {
            Div {
                P(attrs = { classes("hero-tag") }) { Text(HeyBuddy.TAG) }
                H1(attrs = { classes("tagline") }) {
                    Text(HeyBuddy.HEADLINE_LEAD)
                    Text(" ")
                    Span(attrs = { classes("man-cmd") }) { Text(HeyBuddy.HEADLINE_TAIL) }
                }
                P(attrs = { classes("hero-pitch") }) { WithCode(HeyBuddy.PITCH) }
                MetaLine(HeyBuddy.TERMINALS_LABEL, HeyBuddy.terminals)
                MetaLine(HeyBuddy.PLATFORMS_LABEL, HeyBuddy.platforms)
                Div(attrs = { classes("actions") }) {
                    A(href = "#${HeyBuddy.DOWNLOAD_ID}", attrs = { classes("tbtn", "tbtn-primary") }) {
                        TermButtonLabel(HeyBuddy.GET_BETA, trailing = "↓")
                    }
                    A(href = "#${HeyBuddy.SETUP_ID}", attrs = { classes("tbtn") }) { TermButtonLabel(HeyBuddy.HOW_IT_WORKS) }
                }
            }
            PhoneMock()
        }
    }
}

@Composable
private fun MetaLine(label: String, items: List<String>) {
    Div(attrs = { classes("hb-meta") }) {
        Span(attrs = { classes("ck") }) { Text(label) }
        DotList(items, "tech", label = label)
    }
}

/** A phone-shaped frame with a sample terminal and the app's key row; one image to screen readers. */
@Composable
private fun PhoneMock() {
    Div(attrs = { classes("hb-phone-wrap") }) {
        Div(attrs = {
            classes("hb-phone")
            attr("role", "img")
            attr("aria-label", HeyBuddy.MOCK_ALT)
        }) {
            Div(attrs = { classes("hb-screen") }) {
                Div(attrs = { classes("hb-status") }) {
                    Span { Text(HeyBuddy.MOCK_TITLE) }
                    Span(attrs = { classes("hb-live") }) {
                        Span(attrs = { classes("status-dot") }) { Text("●") }
                        Text(" ${HeyBuddy.MOCK_LIVE}")
                    }
                }
                Div(attrs = { classes("hb-lines") }) {
                    HeyBuddy.mockLines.forEach { line ->
                        P(attrs = { classes("hb-line", "is-${line.kind.name.lowercase()}") }) {
                            if (line.kind == MockKind.TOOL) Span(attrs = { classes("hb-dot") }) { Text("● ") }
                            Text(line.text)
                        }
                    }
                    P(attrs = { classes("hb-line", "is-input") }) {
                        Text(">")
                        Cursor()
                    }
                }
                Div(attrs = { classes("hb-keys") }) {
                    HeyBuddy.mockKeys.flatten().forEach { key -> Span(attrs = { classes("hb-key") }) { Text(key) } }
                }
            }
        }
        P(attrs = { classes("hb-note"); attr("aria-hidden", "true") }) { Text(HeyBuddy.MOCK_NOTE) }
    }
}

@Composable
private fun Setup() {
    SectionTitle(HeyBuddy.SETUP_TITLE)
    Ol(attrs = { classes("hb-steps") }) {
        HeyBuddy.setup.forEach { step ->
            Li(attrs = { classes("hb-step") }) {
                Div {
                    H3(attrs = { classes("hb-step-title") }) { WithCode(step.title) }
                    P(attrs = { classes("say") }) { WithCode(step.text) }
                }
            }
        }
    }
}

@Composable
private fun Keyed(title: String, lines: List<KeyedLine>) {
    SectionTitle(title)
    Ul(attrs = { classes("man-body", "man-features") }) {
        lines.forEach { line ->
            Li(attrs = { classes("nf-line"); attr("data-key", line.key) }) {
                Span(attrs = { classes("nf-text") }) { Text(line.text) }
            }
        }
    }
}

@Composable
private fun Download() {
    SectionTitle(HeyBuddy.DOWNLOAD_TITLE)
    P(attrs = { classes("say") }) { Text(HeyBuddy.BETA_NOTE) }
    Ul(attrs = { classes("man-body", "man-features", "hb-builds") }) {
        HeyBuddy.builds.forEach { build ->
            Li(attrs = { classes("nf-line"); attr("data-key", build.key) }) {
                Div(attrs = { classes("nf-text", "hb-build") }) {
                    Span { Text(build.name) }
                    Span(attrs = {
                        classes("hb-status-word")
                        if (build.accessSubject != null) classes("is-beta")
                    }) { Text(build.status) }
                    build.accessSubject?.let { subject ->
                        A(href = HeyBuddy.accessHref(subject), attrs = {
                            classes("tbtn")
                            attr("aria-label", "Ask for access to the ${build.name} beta by email")
                        }) { TermButtonLabel(HeyBuddy.ASK_FOR_ACCESS, glyph = "✉") }
                    }
                }
            }
        }
    }
}

@Composable
private fun Faqs() {
    SectionTitle(HeyBuddy.FAQ_TITLE)
    Dl(attrs = { classes("hb-faq") }) {
        HeyBuddy.faq.forEach { item ->
            Div {
                Dt(attrs = { classes("md-h") }) {
                    Span(attrs = { classes("md-mark"); attr("aria-hidden", "true") }) { Text("## ") }
                    Text(item.question)
                }
                Dd(attrs = { classes("md-p") }) { WithCode(item.answer) }
            }
        }
    }
}

@Composable
private fun HomeLine() {
    Div(attrs = { classes("entry") }) {
        P(attrs = { classes("t-line") }) {
            Span(attrs = { classes("prompt-sign"); attr("aria-hidden", "true") }) { Text("$ ") }
            A(href = "/", attrs = { classes("nf-link") }) { Text("cd ~") }
            Span(attrs = { classes("t-comment") }) { Text(HeyBuddy.HOME_NOTE) }
            Cursor()
        }
    }
}
