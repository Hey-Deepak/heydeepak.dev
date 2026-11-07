package dev.heydeepak.site.components

import androidx.compose.runtime.Composable
import dev.heydeepak.site.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement

// What Buddy is and does (from its public README), a replayed sample session, and its screens on sample data.

private const val BUDDY_REPO_LABEL = "github.com/Hey-Deepak/Hey-Buddy"
private const val DEMO_NAME_COLUMNS = 12

/** `man buddy`: what it is, how it fits together, what it can do, its live `ps`, and where to read more. */
@Composable
fun BuddyOutput() {
    Div(attrs = { classes("man") }) {
        ManHeading("NAME")
        P(attrs = { classes("man-body") }) {
            Span(attrs = { classes("man-cmd") }) { Text("buddy") }
            Text(" — ${BuddyInfo.ONE_LINE}")
        }
        ManHeading("DESCRIPTION")
        P(attrs = { classes("man-body") }) { Text(BuddyInfo.DESCRIPTION) }
        ManHeading("HOW IT WORKS")
        Div(attrs = { classes("bd-diagram") }) {
            TextArt(wideFlow(), "bd-wide", base = "bd-map")
            TextArt(narrowFlow(), "bd-narrow", base = "bd-map")
            P(attrs = { classes("sr-only") }) {
                Text(
                    "Your phone talks to a daemon on your PC. Buddy, the dispatcher, answers chat on your phone and " +
                        "hands tasks to specialist agents, Claude Code sessions in tmux. The daemon connects to " +
                        "MCP, A2A and ACP, and to an LLM wiki, its shared memory.",
                )
            }
        }
        ManHeading("FEATURES")
        Ul(attrs = { classes("man-body", "man-features") }) {
            BuddyInfo.features.forEach { (key, text) ->
                Li(attrs = { classes("nf-line"); attr("data-key", key) }) { Span(attrs = { classes("nf-text") }) { Text(text) } }
            }
        }
        ManHeading("PROCESSES")
        Div(attrs = { classes("man-body") }) {
            Prompt("buddy ps", variant = "prompt-sub")
            BuddyPs()
        }
        ManHeading("SEE ALSO")
        Ul(attrs = { classes("man-body", "man-links") }) {
            Li { OutLink(buddyProject.url, BUDDY_REPO_LABEL) }
            Li { OutLink(Profile.LINKEDIN, BuddyInfo.LINKEDIN_LABEL) }
        }
    }
}

@Composable
private fun ManHeading(name: String) {
    H3(attrs = { classes("man-h") }) { Text(name) }
}

/** The README's flow, one line per hop, for wide outputs (about 85 columns). */
private fun wideFlow(): List<List<Seg>> {
    val drop = " ".repeat(14)
    val branch = " ".repeat(36 - 15)
    return listOf(
        listOf(Seg("phone", "bd-node"), Seg(" ←───→ ", "m-line"), Seg("daemon on your PC", "bd-node"), Seg(" ───▶ ", "m-line"), Seg("buddy (dispatcher)", "m-core-label")),
        listOf(Seg(drop), Seg("│", "m-line"), Seg(branch), Seg("├─ ", "m-line"), Seg("chat"), Seg(" ─▶ ", "m-line"), Seg("your phone", "bd-node")),
        listOf(Seg(drop), Seg("│", "m-line"), Seg(branch), Seg("└─ ", "m-line"), Seg("task"), Seg(" ─▶ ", "m-line"), Seg("specialist agents", "bd-node"), Seg(" (Claude Code in tmux)", "m-dim")),
        listOf(Seg(drop), Seg("├─▶ ", "m-line"), Seg("MCP · A2A · ACP", "bd-node")),
        listOf(Seg(drop), Seg("└─▶ ", "m-line"), Seg("LLM wiki", "bd-node"), Seg(" (shared memory)", "m-dim")),
    )
}

/** The same flow, top to bottom, for phones (about 34 columns). */
private fun narrowFlow(): List<List<Seg>> = listOf(
    listOf(Seg("phone", "bd-node")),
    listOf(Seg("  "), Seg("↑↓", "m-line")),
    listOf(Seg("daemon on your PC", "bd-node")),
    listOf(Seg("  ├─▶ ", "m-line"), Seg("buddy (dispatcher)", "m-core-label")),
    listOf(Seg("  │   ├─ ", "m-line"), Seg("chat"), Seg(" ─▶ ", "m-line"), Seg("your phone", "bd-node")),
    listOf(Seg("  │   └─ ", "m-line"), Seg("task"), Seg(" ─▶ ", "m-line"), Seg("specialist agents", "bd-node")),
    listOf(Seg("  │", "m-line"), Seg("       Claude Code in tmux", "m-dim")),
    listOf(Seg("  ├─▶ ", "m-line"), Seg("MCP · A2A · ACP", "bd-node")),
    listOf(Seg("  └─▶ ", "m-line"), Seg("LLM wiki", "bd-node"), Seg(" (shared memory)", "m-dim")),
)

/** When `buddy demo`'s last line is in (site.css: 7 lines, 300 ms + 650 ms apart, 320 ms each): its next line waits this long. */
const val DEMO_REPLAY_MS = 4600

/** `buddy demo`: a clearly labelled replay; when [playing], the lines arrive one by one. */
@Composable
fun BuddyDemoOutput(playing: Boolean) {
    Div(attrs = {
        classes("demo")
        if (playing) classes("is-playing")
    }) {
        P(attrs = { classes("demo-title") }) { Text("# sample session (replay)") }
        Ul(attrs = { classes("demo-lines") }) {
            buddyDemo.forEachIndexed { index, line ->
                Li(attrs = {
                    classes("demo-line", "demo-${line.speaker.name.lowercase()}")
                    style { property("--i", index.toString()) }
                }) {
                    Span(attrs = { classes("demo-who") }) { Text(line.name.padEnd(DEMO_NAME_COLUMNS)) }
                    Span(attrs = { classes("demo-sep"); attr("aria-hidden", "true") }) { Text("› ") }
                    Span(attrs = { classes("demo-text") }) { Text(line.text) }
                }
            }
        }
    }
}

// buddy build's pace: each pane types its lines one after another; panes run side by side.
private const val BUILD_CHAR_MS = 16
private const val BUILD_LINE_GAP_MS = 200
// The three agents start a beat apart; the phone pane waits for all of them, then the shipped line.
private val BUILD_AGENT_START_MS = listOf(200, 500, 1300)
private const val BUILD_ASK_GAP_MS = 250
private const val BUILD_DONE_GAP_MS = 200
private const val BUILD_DONE_FADE_MS = 300

/** One replay line: [text] starts typing at [atMs] and takes [durationMs]. */
private class TimedLine(val text: String, val atMs: Int, val durationMs: Int)

private fun typeLines(lines: List<String>, startMs: Int): List<TimedLine> {
    var at = startMs
    return lines.map { text ->
        val duration = text.length * BUILD_CHAR_MS
        TimedLine(text, at, duration).also { at += duration + BUILD_LINE_GAP_MS }
    }
}

private fun List<TimedLine>.endMs() = last().let { it.atMs + it.durationMs }

// Lazy: built from Content.kt's lists, whatever order the files initialise in.
private val buildTimeline: List<List<TimedLine>> by lazy {
    val agents = buildPanes.filterNot { it.phone }
        .mapIndexed { index, pane -> pane to typeLines(pane.lines, BUILD_AGENT_START_MS[index]) }.toMap()
    val askAt = agents.values.maxOf { it.endMs() } + BUILD_ASK_GAP_MS
    buildPanes.map { pane -> agents[pane] ?: typeLines(pane.lines, askAt) }
}

private val buildDoneAtMs by lazy { buildTimeline.maxOf { it.endMs() } + BUILD_DONE_GAP_MS }

/** When the replay's last line is in: its next line waits this long. */
val buildReplayMs by lazy { buildDoneAtMs + BUILD_DONE_FADE_MS }

/**
 * `buddy build`: a labelled replay of how this page was made, as tmux panes (2×2 when wide, stacked
 * otherwise). When [playing], every pane types at once; otherwise, and for reduced motion, it is the final state.
 */
@Composable
fun BuddyBuildOutput(playing: Boolean) {
    Div(attrs = {
        classes("build")
        if (playing) classes("is-playing")
    }) {
        P(attrs = { classes("build-note") }) { Text(BUILD_REPLAY_NOTE) }
        Div(attrs = { classes("build-panes") }) {
            buildPanes.forEachIndexed { index, pane ->
                Div(attrs = {
                    // Its agent's colour, the same as that agent's cursor.
                    classes("bpane", "agent-${pane.name.substringBefore(' ')}")
                    if (pane.phone) classes("is-phone")
                    attr("role", "group")
                    attr("aria-label", pane.name)
                }) {
                    // tmux draws the pane title in its top border.
                    P(attrs = { classes("bpane-head"); attr("aria-hidden", "true") }) {
                        Span(attrs = { classes("bpane-title") }) { Text(pane.name) }
                    }
                    Ul(attrs = { classes("bpane-lines") }) {
                        buildTimeline[index].forEach { line ->
                            Li(attrs = {
                                classes("bl-line")
                                if (line.text.startsWith("✓")) classes("is-ok")
                                style {
                                    property("--n", line.text.length.toString())
                                    property("--at", "${line.atMs}ms")
                                    property("--dur", "${line.durationMs}ms")
                                }
                            }) { Span(attrs = { classes("bl-text") }) { Text(line.text) } }
                        }
                    }
                }
            }
        }
        P(attrs = {
            classes("build-done")
            style { property("--at", "${buildDoneAtMs}ms") }
        }) {
            Span(attrs = { classes("build-ok"); attr("aria-hidden", "true") }) { Text(BUILD_DONE.take(1)) }
            Text(BUILD_DONE.drop(1))
        }
    }
}

/**
 * `buddy screenshots`: real screens in phone-shaped frames, labelled as sample data. A grid when the
 * output is wide, a strip you swipe when it isn't (site.css); each opens full size in a new tab.
 */
@Composable
fun BuddyScreenshotsOutput() {
    Div(attrs = { classes("gallery") }) {
        P(attrs = { classes("gallery-head") }) {
            Span(attrs = { classes("gallery-note") }) { Text(BUDDY_SHOTS_NOTE) }
            // Only shown while the shots form a strip.
            Span(attrs = { classes("gallery-hint"); attr("aria-hidden", "true") }) {
                Span(attrs = { classes("hint-touch") }) { Text("swipe →") }
                Span(attrs = { classes("hint-mouse") }) { Text("scroll sideways →") }
            }
        }
        Ul(attrs = { classes("shots"); attr("aria-label", "Buddy screenshots") }) {
            buddyShots.forEach { shot -> Li(attrs = { classes("shot-item") }) { Screenshot(shot) } }
        }
    }
}

@Composable
private fun Screenshot(shot: BuddyShot) {
    val src = "/buddy/${shot.file}"
    TagElement(figureBuilder, {
        classes("shot")
        if (shot.width * SCREEN_RATIO_HEIGHT != shot.height * SCREEN_RATIO_WIDTH) classes("is-cropped")
    }) {
        ExternalLink(src, "shot-frame") {
            Img(src = src, alt = shot.alt, attrs = {
                attr("width", shot.width.toString())
                attr("height", shot.height.toString())
                attr("loading", "lazy")
                attr("decoding", "async")
            })
            Span(attrs = { classes("sr-only") }) { Text(" (full size, opens in a new tab)") }
        }
        TagElement(figcaptionBuilder, { classes("shot-caption") }) {
            Span(attrs = { classes("shot-area") }) { Text(shot.area) }
            Text("$DOT_SEP${shot.caption}")
        }
    }
}

// A full phone screen is 9:20, the frame's shape in site.css; a shorter (cropped) screen fades out inside it.
private const val SCREEN_RATIO_WIDTH = 9
private const val SCREEN_RATIO_HEIGHT = 20

// Compose HTML has no builders for these tags.
private val figureBuilder = ElementBuilder.createBuilder<HTMLElement>("figure")
private val figcaptionBuilder = ElementBuilder.createBuilder<HTMLElement>("figcaption")
