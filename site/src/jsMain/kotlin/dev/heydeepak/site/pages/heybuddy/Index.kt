package dev.heydeepak.site.pages.heybuddy

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.Page
import dev.heydeepak.site.*
import dev.heydeepak.site.components.Cursor
import dev.heydeepak.site.components.Icon
import dev.heydeepak.site.components.SvgIcon
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.HTMLElement

/** heydeepak.dev/heybuddy: the Hey Buddy product page. Unlike the rest of the site, it is not a terminal transcript. */
@Page
@Composable
fun HeyBuddyPage() {
    DisposableEffect(Unit) {
        val restore = applyPageHead(HeyBuddy.head)
        val clarity = loadClarity()
        onDispose {
            restore()
            clarity?.remove()
        }
    }
    Div(attrs = { classes("hbp") }) {
        A(href = "#${HeyBuddy.MAIN_ID}", attrs = { classes("hb-skip") }) { Text(HeyBuddy.SKIP) }
        TopBar()
        Main(attrs = { id(HeyBuddy.MAIN_ID) }) {
            Hero()
            HowItWorks()
            Features()
            Security()
            Download()
            Faqs()
            FinalCta()
        }
        PageFooter()
    }
}

@Composable
private fun TopBar() {
    Header(attrs = { classes("hb-top") }) {
        Div(attrs = { classes("hb-wrap", "hb-top-row") }) {
            A(href = HeyBuddy.PATH, attrs = { classes("hb-brand") }) {
                Span(attrs = { classes("hb-brand-mark") }) { SvgIcon(Icon.TERMINAL) }
                Text(HeyBuddy.NAME)
            }
            Nav(attrs = { classes("hb-nav"); attr("aria-label", "Sections") }) {
                HeyBuddy.nav.forEach { link -> A(href = "#${link.anchor}") { Text(link.label) } }
            }
            BetaButton("hb-btn-sm")
        }
    }
}

@Composable
private fun BetaButton(vararg extra: String) {
    A(href = "#${HeyBuddy.DOWNLOAD_ID}", attrs = { classes("hb-btn", "hb-btn-primary", *extra) }) {
        Text(HeyBuddy.GET_BETA)
        SvgIcon(Icon.ARROW_RIGHT)
    }
}

@Composable
private fun Hero() {
    Section(attrs = { classes("hb-hero") }) {
        Div(attrs = { classes("hb-wrap", "hb-hero-grid") }) {
            Div(attrs = { classes("hb-hero-copy") }) {
                P(attrs = { classes("hb-tag") }) {
                    Span(attrs = { classes("hb-live-dot") })
                    Text(HeyBuddy.TAG)
                }
                H1(attrs = { classes("hb-h1") }) {
                    Span { Text(HeyBuddy.HEADLINE_LEAD) }
                    Text(" ")
                    Span(attrs = { classes("hb-accent") }) { Text(HeyBuddy.HEADLINE_TAIL) }
                }
                P(attrs = { classes("hb-lead") }) { WithCode(HeyBuddy.PITCH) }
                Div(attrs = { classes("hb-ctas") }) {
                    BetaButton("hb-btn-lg")
                    A(href = "#${HeyBuddy.HOW_ID}", attrs = { classes("hb-btn", "hb-btn-ghost", "hb-btn-lg") }) {
                        Text(HeyBuddy.SEE_HOW)
                    }
                }
                Div(attrs = { classes("hb-works") }) {
                    Span(attrs = { classes("hb-works-label"); id("hb-works-label") }) { Text(HeyBuddy.TERMINALS_LABEL) }
                    Ul(attrs = { classes("hb-chips"); attr("aria-labelledby", "hb-works-label") }) {
                        HeyBuddy.terminals.forEach { Li(attrs = { classes("hb-chip") }) { Text(it) } }
                    }
                }
            }
            ProductShot()
        }
    }
}

/** Eyebrow + heading + optional lead, the same at the top of every section. */
@Composable
private fun SectionHead(eyebrow: String, title: String, lead: String? = null, centered: Boolean = false) {
    Div(attrs = {
        classes("hb-head")
        if (centered) classes("is-centered")
    }) {
        P(attrs = { classes("hb-eyebrow") }) { Text(eyebrow) }
        H2(attrs = { classes("hb-h2") }) { Text(title) }
        lead?.let { P(attrs = { classes("hb-sub") }) { WithCode(it) } }
    }
}

@Composable
private fun Band(anchor: String?, vararg extra: String, content: @Composable () -> Unit) {
    Section(attrs = {
        classes("hb-band", *extra)
        anchor?.let { id(it) }
    }) {
        Div(attrs = { classes("hb-wrap") }) { content() }
    }
}

@Composable
private fun HowItWorks() {
    Band(HeyBuddy.HOW_ID) {
        SectionHead(HeyBuddy.HOW_EYEBROW, HeyBuddy.SETUP_TITLE)
        Ol(attrs = { classes("hb-steps") }) {
            val visuals: List<@Composable () -> Unit> = listOf({ SetupVisual() }, { PairVisual() }, { TypeVisual() })
            HeyBuddy.setup.zip(visuals).forEach { (step, visual) ->
                Li(attrs = { classes("hb-card", "hb-step") }) {
                    visual()
                    Div(attrs = { classes("hb-step-text") }) {
                        H3(attrs = { classes("hb-h3") }) { WithCode(step.title) }
                        P(attrs = { classes("hb-body") }) { WithCode(step.text) }
                    }
                }
            }
        }
    }
}

@Composable
private fun Features() {
    Band(HeyBuddy.FEATURES_ID) {
        SectionHead(HeyBuddy.FEATURES_EYEBROW, HeyBuddy.FEATURES_TITLE)
        Div(attrs = { classes("hb-bento") }) {
            Tile(HeyBuddy.realTerminal, "t-terminal") {
                Div(attrs = { classes("hb-keypanel") }) {
                    P(attrs = { classes("hb-keypanel-line"); attr("aria-hidden", "true") }) {
                        Text(HeyBuddy.mockLines.first().text)
                        Cursor()
                    }
                    KeyRow("hb-keys-tile")
                }
            }
            Tile(HeyBuddy.anyTerminal, "t-ides") {
                Ul(attrs = { classes("hb-ide-list"); attr("aria-label", HeyBuddy.TERMINALS_LABEL) }) {
                    HeyBuddy.terminals.forEach { name ->
                        Li {
                            SvgIcon(Icon.TERMINAL)
                            Text(name)
                        }
                    }
                }
            }
            Tile(HeyBuddy.reach, "t-reach") {
                Ul(attrs = { classes("hb-reach") }) {
                    HeyBuddy.links.forEach { link ->
                        Li {
                            SvgIcon(link.icon, "hb-reach-icon")
                            Div {
                                P(attrs = { classes("hb-reach-title") }) { Text(link.title) }
                                P(attrs = { classes("hb-reach-text") }) { Text(link.text) }
                            }
                        }
                    }
                }
            }
            Tile(HeyBuddy.phoneKey, "t-key")
            Tile(HeyBuddy.noCloud, "t-cloud")
            Tile(HeyBuddy.platformsTile, "t-platforms") {
                Ul(attrs = { classes("hb-pills"); attr("aria-label", "Platforms") }) {
                    HeyBuddy.builds.forEach { build ->
                        Li(attrs = {
                            classes("hb-pill")
                            if (build.accessSubject != null) classes("is-beta")
                        }) { Text("${build.name} · ${build.status.lowercase()}") }
                    }
                }
            }
        }
    }
}

@Composable
private fun Tile(point: IconPoint, variant: String, extra: (@Composable () -> Unit)? = null) {
    Article(attrs = { classes("hb-card", "hb-tile", variant) }) {
        SvgIcon(point.icon, "hb-tile-icon")
        H3(attrs = { classes("hb-h3") }) { Text(point.title) }
        if (point.text.isNotEmpty()) P(attrs = { classes("hb-body") }) { WithCode(point.text) }
        extra?.let { Div(attrs = { classes("hb-tile-extra") }) { it() } }
    }
}

@Composable
private fun Security() {
    Band(HeyBuddy.SECURITY_ID) {
        Div(attrs = { classes("hb-security") }) {
            Div {
                SectionHead(HeyBuddy.SECURITY_EYEBROW, HeyBuddy.SECURITY_TITLE, HeyBuddy.SECURITY_LEAD)
                SecurityDiagram()
            }
            Ul(attrs = { classes("hb-points") }) {
                HeyBuddy.security.forEach { point ->
                    Li(attrs = { classes("hb-point") }) {
                        SvgIcon(point.icon, "hb-point-icon")
                        Div {
                            H3(attrs = { classes("hb-point-title") }) { Text(point.title) }
                            P(attrs = { classes("hb-body") }) { Text(point.text) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Download() {
    Band(HeyBuddy.DOWNLOAD_ID) {
        SectionHead(HeyBuddy.DOWNLOAD_EYEBROW, HeyBuddy.DOWNLOAD_TITLE, HeyBuddy.BETA_NOTE)
        Ul(attrs = { classes("hb-builds") }) {
            HeyBuddy.builds.forEach { build ->
                val subject = build.accessSubject
                Li(attrs = {
                    classes("hb-card", "hb-build")
                    if (subject == null) classes("is-later")
                }) {
                    Div(attrs = { classes("hb-build-top") }) {
                        SvgIcon(build.icon, "hb-build-icon")
                        Span(attrs = {
                            classes("hb-status")
                            if (subject != null) classes("is-beta")
                        }) { Text(build.status) }
                    }
                    H3(attrs = { classes("hb-h3") }) { Text(build.name) }
                    P(attrs = { classes("hb-body") }) { Text(build.detail) }
                    if (subject != null) {
                        A(href = HeyBuddy.accessHref(subject), attrs = {
                            classes("hb-btn", "hb-btn-outline")
                            attr("aria-label", "Ask for access to the ${build.name} beta by email")
                        }) {
                            SvgIcon(Icon.MAIL)
                            Text(HeyBuddy.ASK_FOR_ACCESS)
                        }
                    } else {
                        Span(attrs = { classes("hb-btn", "hb-btn-disabled"); attr("aria-disabled", "true") }) {
                            Text(HeyBuddy.NOT_YET)
                        }
                    }
                }
            }
        }
    }
}

private val detailsBuilder = ElementBuilder.createBuilder<HTMLElement>("details")
private val summaryBuilder = ElementBuilder.createBuilder<HTMLElement>("summary")

@Composable
private fun Faqs() {
    Band(HeyBuddy.FAQ_ID) {
        Div(attrs = { classes("hb-faq-grid") }) {
            SectionHead(HeyBuddy.FAQ_EYEBROW, HeyBuddy.FAQ_TITLE)
            Div(attrs = { classes("hb-faq") }) {
                HeyBuddy.faq.forEach { item ->
                    TagElement(detailsBuilder, { classes("hb-qa") }) {
                        TagElement(summaryBuilder, { classes("hb-q") }) {
                            Span { Text(item.question) }
                            SvgIcon(Icon.PLUS, "hb-q-icon")
                        }
                        P(attrs = { classes("hb-a") }) { WithCode(item.answer) }
                    }
                }
            }
        }
    }
}

@Composable
private fun FinalCta() {
    Band(null, "hb-band-cta") {
        Div(attrs = { classes("hb-cta") }) {
            H2(attrs = { classes("hb-h2") }) { Text(HeyBuddy.CTA_TITLE) }
            P(attrs = { classes("hb-sub") }) { Text(HeyBuddy.CTA_TEXT) }
            BetaButton("hb-btn-lg")
        }
    }
}

@Composable
private fun PageFooter() {
    Footer(attrs = { classes("hb-foot") }) {
        Div(attrs = { classes("hb-wrap", "hb-foot-row") }) {
            P(attrs = { classes("hb-foot-owner") }) {
                Text("${HeyBuddy.FOOTER_OWNER} · ")
                A(href = "/") { Text(HeyBuddy.FOOTER_HOME) }
            }
            P(attrs = { classes("hb-foot-note") }) { Text(HeyBuddy.PRIVACY_NOTE) }
            P(attrs = { classes("hb-foot-note") }) { Text(HeyBuddy.DISCLAIMER) }
        }
    }
}

/** Plain text where `backticked` parts are drawn as code. */
@Composable
private fun WithCode(text: String) {
    text.split('`').forEachIndexed { index, part ->
        if (index % 2 == 1) Code(attrs = { classes("hb-inline-code") }) { Text(part) } else Text(part)
    }
}

// Only in a real visit: the static export runs this page in a headless (webdriver) browser.
private fun loadClarity(): org.w3c.dom.Element? {
    if (kotlinx.browser.window.navigator.asDynamic().webdriver == true) return null
    // Clarity's tag expects this queue function, as in its official snippet.
    js("window.clarity = window.clarity || function () { (window.clarity.q = window.clarity.q || []).push(arguments) }")
    val script = kotlinx.browser.document.createElement("script")
    script.setAttribute("src", HeyBuddy.CLARITY_TAG)
    script.setAttribute("async", "")
    kotlinx.browser.document.head?.appendChild(script)
    return script
}
