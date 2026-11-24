package dev.heydeepak.site.pages.heybuddy

import androidx.compose.runtime.Composable
import dev.heydeepak.site.HeyBuddy
import dev.heydeepak.site.MockKind
import dev.heydeepak.site.MockLine
import dev.heydeepak.site.components.Cursor
import dev.heydeepak.site.components.Icon
import dev.heydeepak.site.components.SvgIcon
import org.jetbrains.compose.web.dom.*

// Drawn product pictures for /heybuddy, all in HTML/CSS. Each is one image to screen readers (or hidden
// when the text beside it already says the same), so their sample text is never read out line by line.

/** The hero's picture: a Mac terminal and a phone showing the same session. */
@Composable
fun ProductShot() {
    Div(attrs = {
        classes("hb-shot")
        attr("role", "img")
        attr("aria-label", HeyBuddy.SHOT_ALT)
    }) {
        MacTerminal()
        Div(attrs = { classes("hb-link-row") }) {
            Span(attrs = { classes("hb-link-chip") }) {
                SvgIcon(Icon.ARROW_LEFT_RIGHT)
                Text(HeyBuddy.SAME_SESSION)
            }
        }
        PhoneScreen()
    }
}

@Composable
private fun MacTerminal() {
    Div(attrs = { classes("hb-term") }) {
        WindowBar(HeyBuddy.MAC_TITLE)
        Div(attrs = { classes("hb-term-body") }) {
            P(attrs = { classes("hb-ln") }) { ShellPrompt() }
            SessionLines(HeyBuddy.mockLines)
        }
    }
}

/** A window title bar with three traffic-light dots. */
@Composable
private fun WindowBar(title: String) {
    Div(attrs = { classes("hb-winbar") }) {
        Span(attrs = { classes("hb-lights") }) { repeat(TRAFFIC_LIGHTS) { Span() } }
        Span(attrs = { classes("hb-wintitle") }) { Text(title) }
    }
}

private const val TRAFFIC_LIGHTS = 3

@Composable
private fun ShellPrompt(cursor: Boolean = false) {
    Span(attrs = { classes("hb-path") }) { Text(HeyBuddy.MAC_PATH) }
    Span(attrs = { classes("hb-sign") }) { Text(" $ ") }
    Text(HeyBuddy.MAC_COMMAND)
    if (cursor) Cursor()
}

/** The session itself: tool lines with a dot, the permission question boxed, then a waiting prompt. */
@Composable
private fun SessionLines(lines: List<MockLine>) {
    val (asking, before) = lines.partition { it.kind == MockKind.ASK || it.kind == MockKind.CHOICE }
    before.forEach { line ->
        P(attrs = { classes("hb-ln", "is-${line.kind.name.lowercase()}") }) {
            if (line.kind == MockKind.TOOL) Span(attrs = { classes("hb-dot") }) { Text("● ") }
            Text(line.text)
        }
    }
    Div(attrs = { classes("hb-ask") }) {
        asking.forEach { line -> P(attrs = { classes("hb-ln", "is-${line.kind.name.lowercase()}") }) { Text(line.text) } }
    }
    P(attrs = { classes("hb-ln", "is-input") }) {
        Text(">")
        Cursor()
    }
}

@Composable
private fun PhoneScreen() {
    Div(attrs = { classes("hb-phone") }) {
        Div(attrs = { classes("hb-phone-screen") }) {
            Div(attrs = { classes("hb-phone-bar") }) {
                Span { Text(HeyBuddy.PHONE_TITLE) }
                LiveBadge()
            }
            Div(attrs = { classes("hb-phone-body") }) { SessionLines(HeyBuddy.mockLines) }
            KeyRow("hb-keys-phone")
        }
    }
}

@Composable
fun LiveBadge() {
    Span(attrs = { classes("hb-live") }) {
        Span(attrs = { classes("hb-live-dot") })
        Text(HeyBuddy.LIVE)
    }
}

// Arrow and Enter glyphs are tiny or missing in the mono font, so those keys are drawn as icons.
private val keyIcons = mapOf("←" to Icon.ARROW_LEFT, "↓" to Icon.ARROW_DOWN, "↑" to Icon.ARROW_UP, "→" to Icon.ARROW_RIGHT, "⏎" to Icon.ENTER)
private const val SHIFT = "⇧"

/** The phone app's two rows of terminal keys; decoration, since the text beside it names every key. */
@Composable
fun KeyRow(variant: String) {
    Div(attrs = { classes("hb-keys", variant); attr("aria-hidden", "true") }) {
        HeyBuddy.mockKeys.flatten().forEach { key ->
            Span(attrs = { classes("hb-key") }) {
                val icon = keyIcons[key]
                when {
                    icon != null -> SvgIcon(icon)
                    key.startsWith(SHIFT) -> {
                        SvgIcon(Icon.SHIFT, "hb-shift")
                        Text(key.removePrefix(SHIFT))
                    }
                    else -> Text(key)
                }
            }
        }
    }
}

/** Step 1: the Mac app's setup checklist, all done. */
@Composable
fun SetupVisual() {
    Div(attrs = { classes("hb-mini", "hb-mini-setup"); attr("aria-hidden", "true") }) {
        Ul(attrs = { classes("hb-checks") }) {
            HeyBuddy.setupChecks.forEach { check ->
                Li {
                    Text(check)
                    SvgIcon(Icon.CHECK, "hb-check")
                }
            }
        }
        Span(attrs = { classes("hb-mini-button") }) { Text(HeyBuddy.SETUP_BUTTON) }
    }
}

/** Step 2: a QR-style block and the pairing code. */
@Composable
fun PairVisual() {
    Div(attrs = { classes("hb-mini", "hb-mini-pair"); attr("aria-hidden", "true") }) {
        Span(attrs = {
            classes("hb-qr")
            ref { span ->
                span.innerHTML = qrSvg()
                onDispose { }
            }
        })
        Div(attrs = { classes("hb-pair") }) {
            Code(attrs = { classes("hb-code") }) { Text(HeyBuddy.PAIR_CODE) }
            Span(attrs = { classes("hb-mini-note") }) { Text(HeyBuddy.PAIR_HINT) }
        }
    }
}

/** Step 3: the command on the Mac, and the phone lighting up. */
@Composable
fun TypeVisual() {
    Div(attrs = { classes("hb-mini", "hb-mini-type"); attr("aria-hidden", "true") }) {
        P(attrs = { classes("hb-ln", "hb-oneline") }) { ShellPrompt(cursor = true) }
        Div(attrs = { classes("hb-handoff") }) {
            SvgIcon(Icon.ARROW_RIGHT)
            SvgIcon(Icon.PHONE, "hb-handoff-phone")
            Span { Text(HeyBuddy.ON_PHONE) }
            LiveBadge()
        }
    }
}

/** Security: phone and Mac joined directly, the cloud server crossed out above the link. */
@Composable
fun SecurityDiagram() {
    Div(attrs = {
        classes("hb-diagram")
        attr("role", "img")
        attr("aria-label", HeyBuddy.DIAGRAM_ALT)
    }) {
        Div(attrs = { classes("hb-cloud") }) {
            SvgIcon(Icon.CLOUD_OFF)
            Span { Text(HeyBuddy.DIAGRAM_CLOUD) }
        }
        Div(attrs = { classes("hb-diagram-row") }) {
            DiagramNode(Icon.PHONE, HeyBuddy.DIAGRAM_PHONE)
            Span(attrs = { classes("hb-wire") })
            DiagramNode(Icon.LAPTOP, HeyBuddy.DIAGRAM_MAC)
        }
        Span(attrs = { classes("hb-wire-label") }) { Text(HeyBuddy.DIAGRAM_LINK) }
    }
}

@Composable
private fun DiagramNode(icon: Icon, label: String) {
    Div(attrs = { classes("hb-node") }) {
        SvgIcon(icon)
        Span { Text(label) }
    }
}

// A QR-looking block (not a real code): three finder squares and a fixed scatter of modules.
private const val QR_SIZE = 21
private const val QR_FINDER = 7
private const val QR_TIMING = 6
private const val QR_FILL_IN = 5
private const val QR_FILL_OF = 11

private fun qrSvg(): String {
    val path = StringBuilder()
    for (y in 0 until QR_SIZE) for (x in 0 until QR_SIZE) {
        if (qrModule(x, y)) path.append("M$x ${y}h1v1h-1z")
    }
    return "<svg viewBox=\"-1 -1 ${QR_SIZE + 2} ${QR_SIZE + 2}\" shape-rendering=\"crispEdges\">" +
        "<path fill=\"currentColor\" d=\"$path\"/></svg>"
}

private fun qrModule(x: Int, y: Int): Boolean {
    finderCorner(x, y)?.let { (fx, fy) ->
        val ring = maxOf(kotlin.math.abs(x - fx - QR_FINDER / 2), kotlin.math.abs(y - fy - QR_FINDER / 2))
        return ring != 2 && ring <= 3
    }
    if (inFinderMargin(x, y)) return false
    if (x == QR_TIMING || y == QR_TIMING) return (x + y) % 2 == 0
    return (x * 73 + y * 151 + x * y * 7) % QR_FILL_OF < QR_FILL_IN
}

private fun finderCorner(x: Int, y: Int): Pair<Int, Int>? {
    val far = QR_SIZE - QR_FINDER
    return listOf(0 to 0, far to 0, 0 to far).firstOrNull { (fx, fy) -> x in fx until fx + QR_FINDER && y in fy until fy + QR_FINDER }
}

private fun inFinderMargin(x: Int, y: Int): Boolean {
    val far = QR_SIZE - QR_FINDER - 1
    return (x <= QR_FINDER && y <= QR_FINDER) || (x >= far && y <= QR_FINDER) || (x <= QR_FINDER && y >= far)
}
