package dev.heydeepak.site.components

import androidx.compose.runtime.*
import dev.heydeepak.site.*
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.*
import kotlin.math.roundToInt

private const val HERO_TITLE_ID = "hero-title"
private const val TAG_SEPARATOR = " · "
private const val ART_FILL = '█'
// The longest SDK name ("React Native"), so the versions line up.
private const val SDK_NAME_COLUMNS = 12
private const val METER_CELLS = 20
private const val CORE_COLUMNS = 22
private const val NARROW_CORE_COLUMNS = 20
private const val CORE_RULE_COLUMNS = 15
// The neofetch logo: the "D" of the art, its first 8 columns.
private const val LOGO_COLUMNS = 8

/** An output block: hidden until its turn, playing its animation, or shown as is. */
enum class Reveal(val cls: String?) { WAITING("is-waiting"), PLAYING("is-playing"), DONE(null) }

/**
 * One run of a text-art line; [cls] (space-separated) picks the colour and animation part. A [lit] run
 * is drawn twice in one cell: a dim copy that is always readable, and a lit copy that fades in on top.
 */
internal class Seg(val text: String, val cls: String? = null, val lit: Boolean = false)

/** An output block's reveal state, plus `data-static` on the export so the head script can hide it before the app. */
internal fun AttrsScope<*>.revealState(state: Reveal) {
    state.cls?.let { classes(it) }
    if (isExporting()) attr("data-static", "")
}

/** Streams in as rows; [index] orders the stagger. */
internal fun AttrsScope<*>.streamRow(index: Int) {
    classes("stream")
    style { property("--i", index.toString()) }
}

/** `whoami`: the name art, who I am, the headline, the pitch and the buttons. */
@Composable
fun WhoAmI(reveal: Reveal, withTitle: Boolean = true) {
    Div(attrs = { classes("whoami"); revealState(reveal) }) {
        // Wide screens: the art and who-I-am side by side; stacked otherwise.
        Div(attrs = { classes("whoami-top") }) {
            Pre(attrs = { classes("art"); attr("aria-hidden", "true") }) {
                heroArt.forEachIndexed { index, row ->
                    // Solid blocks in amber, the box-line "shadow" a shade darker.
                    Span(attrs = { classes("art-row"); streamRow(index) }) {
                        artRuns(row.padEnd(HERO_ART_COLUMNS)).forEach { run ->
                            Span(attrs = { classes(if (run.first() == ART_FILL) "art-fill" else "art-shade") }) { Text(run) }
                        }
                    }
                }
            }
            if (withTitle) H1(attrs = { id(HERO_TITLE_ID); classes("sr-only") }) { Text(HERO_NAME) }
            Div(attrs = { classes("whoami-id") }) {
                Div(attrs = { classes("hello"); streamRow(heroArt.size) }) {
                    Img(src = PHOTO_SRC, alt = PHOTO_ALT, attrs = {
                        classes("hero-photo")
                        attr("width", PHOTO_SIZE_PX.toString())
                        attr("height", PHOTO_SIZE_PX.toString())
                    })
                    Div {
                        P(attrs = { classes("hello-text") }) { Text("Hey, I’m Deepak") }
                        P(attrs = { classes("hero-role") }) {
                            Text("${Profile.ROLE} ")
                            Span(attrs = { classes("at") }) { Text("@") }
                            Text(" ${Profile.COMPANY}")
                        }
                    }
                }
                // A comment line; each tag phrase stays whole when it wraps.
                P(attrs = { classes("hero-tag"); streamRow(heroArt.size + 1) }) {
                    Span(attrs = { classes("comment-mark"); attr("aria-hidden", "true") }) { Text("# ") }
                    HERO_TAG.split(TAG_SEPARATOR).forEachIndexed { index, phrase ->
                        if (index > 0) Text(DOT_SEP)
                        Span(attrs = { classes("nowrap") }) { Text(phrase) }
                    }
                }
            }
        }
        var row = heroArt.size + 2
        P(attrs = { classes("tagline"); streamRow(row++) }) {
            Text("$HERO_HEADLINE_LEAD ")
            Span(attrs = { classes("keep-line") }) { Text(HERO_HEADLINE_TAIL) }
        }
        P(attrs = { classes("hero-pitch"); streamRow(row++) }) { Text(HERO_PITCH) }
        Div(attrs = { classes("actions"); streamRow(row) }) {
            ResumeButton()
            A(href = "mailto:${Profile.EMAIL}", attrs = { classes("tbtn") }) { TermButtonLabel("email", glyph = "✉") }
            ExternalLink(Profile.LINKEDIN, "tbtn") { TermButtonLabel("linkedin", trailing = "↗") }
            ExternalLink(Profile.GITHUB, "tbtn") { TermButtonLabel("github", trailing = "↗") }
        }
    }
}

/** Splits an art row into runs of solid blocks and everything else. */
private fun artRuns(row: String): List<String> {
    val runs = mutableListOf<String>()
    var start = 0
    for (index in 1..row.length) {
        if (index == row.length || (row[index] == ART_FILL) != (row[start] == ART_FILL)) {
            runs += row.substring(start, index)
            start = index
        }
    }
    return runs
}

/**
 * The 4 SDKs each feeding one shared core: a wide drawing, and a vertical one for phones. On its turn
 * the lines draw, a dot travels each line into the core, and the ✓ lights up; the labels never wait.
 */
@Composable
fun ShipMap(reveal: Reveal) {
    Div(attrs = { classes("ship"); revealState(reveal) }) {
        TextArt(wideShipMap(), "ship-wide") {
            CoreGlow()
            WideDots()
        }
        TextArt(narrowShipMap(), "ship-narrow") {
            CoreGlow()
            NarrowDots()
        }
        P(attrs = { classes("sr-only") }) { Text("All 4 SDKs on $SHARED_CORE_LABEL: ${shippedTogether()} $SHARED_CORE_WHEN.") }
    }
}

@Composable
internal fun TextArt(lines: List<List<Seg>>, variant: String, base: String = "ship-map", overlay: @Composable () -> Unit = {}) {
    Pre(attrs = { classes(base, variant); attr("aria-hidden", "true") }) {
        lines.forEachIndexed { row, line ->
            Span(attrs = { classes("m-row"); style { property("--row", row.toString()) } }) {
                line.forEach { seg -> ArtSeg(seg) }
            }
        }
        overlay()
    }
}

@Composable
internal fun ArtSeg(seg: Seg) {
    val classNames = seg.cls?.split(' ')?.toTypedArray()
    when {
        seg.lit -> Span(attrs = { classes("m-pair") }) {
            Span(attrs = { classes("m-dim") }) { Text(seg.text) }
            Span(attrs = { classes(*classNames.orEmpty()) }) { Text(seg.text) }
        }
        classNames == null -> Text(seg.text)
        else -> Span(attrs = { classes(*classNames) }) { Text(seg.text) }
    }
}

/** A soft glow behind the core box: it pulses once when the ✓ lights, then settles. */
@Composable
private fun CoreGlow() {
    Span(attrs = { classes("m-glow"); attr("aria-hidden", "true") })
}

@Composable
private fun WideDots() {
    // One per SDK row (rows 1–4 of the drawing), starting where its line starts.
    sharedCoreReleases.indices.forEach { index ->
        Span(attrs = { classes("m-dot"); style { property("--row", (index + 1).toString()) } }) { Text("●") }
    }
}

@Composable
private fun NarrowDots() {
    // Each runs down the rail from its SDK's row to the arrow row, then into the core.
    sharedCoreReleases.indices.forEach { index ->
        Span(attrs = {
            classes("m-dot")
            style {
                property("--row", index.toString())
                property("--drop", (NARROW_ARROW_ROW - index).toString())
            }
        }) { Text("●") }
    }
}

private const val NARROW_ARROW_ROW = 5

private fun centred(text: String, width: Int): Pair<String, String> {
    val left = (width - text.length) / 2
    return " ".repeat(left) to " ".repeat(width - text.length - left)
}

/** A line inside the core: [segs] padded to [width] columns. */
private fun coreLine(width: Int, content: String, segs: List<Seg>): List<Seg> {
    val (left, right) = centred(content, width)
    return listOf(Seg(left)) + segs + Seg(right)
}

/** The "✓ all 4 shipped" / date lines: lit up when the dots arrive, readable before. */
private fun litLine(width: Int, text: String, cls: String): List<Seg> {
    val (left, right) = centred(text, width)
    return listOf(Seg(left), Seg(text, cls, lit = true), Seg(right))
}

private fun coreContent(width: Int, row: Int): List<Seg> = when (row) {
    0 -> coreLine(width, SHARED_CORE_LABEL, listOf(Seg(SHARED_CORE_LABEL, "m-core-label")))
    1 -> coreLine(width, "─".repeat(CORE_RULE_COLUMNS), listOf(Seg("─".repeat(CORE_RULE_COLUMNS), "m-rule")))
    2 -> litLine(width, "✓ $SHARED_CORE_SHIPPED", "m-lit m-lit-ok")
    else -> litLine(width, SHARED_CORE_WHEN, "m-lit")
}

private const val WIDE_LINE = "───"

/** 45 columns: `name ver ───▶┃ core ┃`, so it fits a ~7/12 pane at about 25px. */
private fun wideShipMap(): List<List<Seg>> {
    fun label(release: SdkRelease) = release.name.padEnd(SDK_NAME_COLUMNS) + " "
    val first = sharedCoreReleases.first()
    val lead = label(first).length + first.version.length + 1 + WIDE_LINE.length + 1
    val rule = "━".repeat(CORE_COLUMNS)
    return listOf(listOf(Seg(" ".repeat(lead)), Seg("┏$rule┓", "m-box"))) +
        sharedCoreReleases.mapIndexed { index, release ->
            listOf(
                Seg(label(release)), Seg(release.version, "m-ver"), Seg(" "),
                Seg(WIDE_LINE, "m-draw"), Seg("▶", "m-head"), Seg("┃", "m-box"),
            ) + coreContent(CORE_COLUMNS, index) + Seg("┃", "m-box")
        } +
        listOf(listOf(Seg(" ".repeat(lead)), Seg("┗$rule┛", "m-box")))
}

private fun narrowShipMap(): List<List<Seg>> {
    val rule = "━".repeat(NARROW_CORE_COLUMNS)
    val indent = Seg("     ")
    return sharedCoreReleases.mapIndexed { index, release ->
        listOf(
            Seg(if (index == 0) "┌─ " else "├─ ", "m-rail"),
            Seg(release.name.padEnd(SDK_NAME_COLUMNS) + "  "), Seg(release.version, "m-ver"),
        )
    } + listOf(
        listOf(Seg("│", "m-rail"), Seg("    "), Seg("┏$rule┓", "m-box")),
        listOf(Seg("└───", "m-rail"), Seg("▶", "m-head"), Seg("┃", "m-box")) +
            coreContent(NARROW_CORE_COLUMNS, 0) + Seg("┃", "m-box"),
        listOf(indent, Seg("┃", "m-box")) + coreContent(NARROW_CORE_COLUMNS, 2) + Seg("┃", "m-box"),
        listOf(indent, Seg("┗$rule┛", "m-box")),
        listOf(indent) + coreContent(NARROW_CORE_COLUMNS + 2, 3),
    )
}

/** "Android 3.0, iOS 4.0, Flutter 5.0 and React Native 2.0 shipped". */
private fun shippedTogether(): String {
    val releases = sharedCoreReleases.map { "${it.name} ${it.version}" }
    return releases.dropLast(1).joinToString(", ") + " and " + releases.last() + " shipped"
}

/** Keys are drawn by CSS (`data-key`), so each value + label reads as one sentence in the page text. */
private fun AttrsScope<*>.factKey(key: String) = attr("data-key", key)

/** `./ship --all-sdks --core shared`: the map, then the release and commit proof (the heading is the title line). */
@Composable
fun ShipOutput(map: Reveal) {
    val filled = (COMMIT_SHARE_PERCENT * METER_CELLS / 100.0).roundToInt()
    val releases = proofStats.first()
    ShipMap(map)
    Ul(attrs = { classes("pane-facts") }) {
        Li(attrs = { classes("nf-line", "nf-stat"); factKey(releases.key) }) {
            Span(attrs = { classes("nf-num") }) { Text(releases.value) }
            Text(" ")
            Span(attrs = { classes("nf-label") }) { Text(releases.label) }
        }
        Li(attrs = { classes("nf-line", "nf-stat", "nf-commits"); factKey("commits") }) {
            Span(attrs = { classes("nf-num") }) { Text("$COMMIT_SHARE_PERCENT%") }
            Text(" ")
            Span(attrs = { classes("nf-label") }) {
                Span(attrs = { classes("meter"); attr("aria-hidden", "true") }) {
                    Span(attrs = { classes("meter-fill") }) { Text("█".repeat(filled)) }
                    Span(attrs = { classes("meter-empty") }) { Text("░".repeat(METER_CELLS - filled)) }
                }
                Text(" $COMMIT_SHARE_LABEL")
            }
        }
        Li(attrs = { classes("nf-line"); factKey("3.x") }) { Span(attrs = { classes("nf-text") }) { Text(KOTLIN_3X_LINE) } }
    }
}

/** `cat agents.md`: rusty's numbers set large, then the voice-agent work. */
@Composable
fun AgentsOutput() {
    Div(attrs = { classes("agents-out") }) {
        var row = 0
        P(attrs = { classes("md-h"); streamRow(row++) }) {
            Span(attrs = { classes("md-mark"); attr("aria-hidden", "true") }) { Text("# ") }
            Text(Rusty.NAME)
        }
        P(attrs = { classes("md-p"); streamRow(row++) }) { Text(Rusty.WHAT) }
        Ul(attrs = { classes("big-counts"); streamRow(row++) }) {
            Rusty.counts.forEach { (count, label) ->
                Li {
                    Span(attrs = { classes("nf-num") }) { Text(count) }
                    Text(" ")
                    Span(attrs = { classes("big-label") }) { Text(label) }
                }
            }
        }
        P(attrs = { classes("md-p"); streamRow(row++) }) { Text("and ${Rusty.WIKI}.") }
        P(attrs = { classes("md-p"); streamRow(row++) }) {
            Text(Rusty.ADOPTION_LEAD)
            Span(attrs = { classes("num-inline") }) { Text(Rusty.ADOPTION_PRS) }
            Text(Rusty.ADOPTION_TAIL)
        }
        P(attrs = { classes("md-h"); streamRow(row++) }) {
            Span(attrs = { classes("md-mark"); attr("aria-hidden", "true") }) { Text("# ") }
            Text("voice agents")
        }
        P(attrs = { classes("md-p"); streamRow(row) }) { Text(VOICE_AGENTS_LINE) }
    }
}

/** `neofetch`: the logo, then the four proof numbers set large, in their page order, and the colour bar. */
@Composable
fun StatsOutput() {
    Div(attrs = { classes("neofetch") }) {
        Pre(attrs = { classes("nf-logo"); attr("aria-hidden", "true") }) {
            heroArt.forEach { row ->
                Span(attrs = { classes("art-row") }) {
                    artRuns(row.take(LOGO_COLUMNS)).forEach { run ->
                        Span(attrs = { classes(if (run.first() == ART_FILL) "art-fill" else "art-shade") }) { Text(run) }
                    }
                }
            }
        }
        Div(attrs = { classes("nf-info") }) {
            P(attrs = { classes("nf-title") }) {
                Span(attrs = { classes("nf-user") }) { Text("deepak") }
                Text("@")
                Span(attrs = { classes("nf-user") }) { Text("heydeepak.dev") }
            }
            P(attrs = { classes("nf-rule"); attr("aria-hidden", "true") }) { Text("─".repeat("deepak@heydeepak.dev".length)) }
            P(attrs = { classes("nf-line"); factKey("role") }) { Span(attrs = { classes("nf-text") }) { Text(Profile.ROLE) } }
            Ul(attrs = { classes("nf-stats"); attr("aria-label", "Highlights") }) {
                proofStats.forEach { stat ->
                    Li(attrs = { classes("nf-line", "nf-stat"); factKey(stat.key) }) {
                        Span(attrs = { classes("nf-num") }) { Text(stat.value) }
                        Text(" ")
                        Span(attrs = { classes("nf-label") }) { Text(stat.label) }
                    }
                }
            }
            // neofetch's colour bar: the Terminal Warmth swatches.
            Div(attrs = { classes("nf-swatches"); attr("aria-hidden", "true") }) {
                listOf("amber", "teal", "green", "blue", "purple", "mid", "text", "border").forEach { swatch ->
                    Span(attrs = { classes("sw", "sw-$swatch") })
                }
            }
        }
    }
}
