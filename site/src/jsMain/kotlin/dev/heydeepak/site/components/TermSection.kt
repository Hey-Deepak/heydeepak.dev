package dev.heydeepak.site.components

import androidx.compose.runtime.*
import dev.heydeepak.site.Profile
import dev.heydeepak.site.isExporting
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement
import kotlin.random.Random

/** How a person types: [baseMs] + up to [jitterMs] per key, [gapMs] more at word breaks, a rare [hesitateMs], then [enterMs] before Enter. */
class TypingPace(val baseMs: Long, val jitterMs: Long, val gapMs: Long, val hesitateMs: Long, val enterMs: Long)

/** `whoami`: quick but human, so the hero is readable within about 1.2 s. */
val QUICK_PACE = TypingPace(baseMs = 45, jitterMs = 30, gapMs = 40, hesitateMs = 0, enterMs = 100)
/** The hero's later commands: slower, with the odd pause. */
val DRAMATIC_PACE = TypingPace(baseMs = 40, jitterMs = 55, gapMs = 80, hesitateMs = 220, enterMs = 350)
/** A chip's command, typed into the prompt for you: quick, about 20 ms a key. */
val FAST_PACE = TypingPace(baseMs = 14, jitterMs = 12, gapMs = 0, hesitateMs = 0, enterMs = 90)

private const val HESITATION_CHANCE_PCT = 7
// People pause between words and path parts, not inside flags like "--all-sdks".
private const val WORD_BREAKS = " /"

private fun keystrokeMs(char: Char, pace: TypingPace): Long {
    val gap = if (char in WORD_BREAKS) pace.gapMs else 0L
    val hesitate = if (pace.hesitateMs > 0 && Random.nextInt(100) < HESITATION_CHANCE_PCT) pace.hesitateMs else 0L
    return pace.baseMs + Random.nextLong(pace.jitterMs + 1) + gap + hesitate
}

/** Types [command] one key at a time, reporting how many characters are in, then pauses before Enter. */
suspend fun typeCommand(command: String, pace: TypingPace, onTyped: (Int) -> Unit) {
    command.forEachIndexed { index, char ->
        delay(keystrokeMs(char, pace))
        onTyped(index + 1)
    }
    delay(pace.enterMs)
}

/**
 * `$ command` as decoration: an invisible copy of the full command reserves the final size, so typing
 * never shifts the layout. The exported snapshot holds the full command; the head script hides it
 * (data-static) until the app types it, or shows it after the grace period.
 */
@Composable
fun Prompt(command: String, typed: Int = command.length, cursor: Boolean = false, variant: String? = null) {
    Div(attrs = {
        classes("prompt")
        variant?.let { classes(it) }
        attr("aria-hidden", "true")
    }) {
        Span(attrs = { classes("prompt-sign") }) { Text("$") }
        Text(" ")
        Span(attrs = { classes("prompt-cmd") }) {
            Span(attrs = { classes("prompt-ghost") }) { Text(command.ifEmpty { "\u200B" }) }
            Span(attrs = {
                classes("prompt-live")
                if (isExporting()) attr("data-static", "")
            }) {
                Text(command.take(typed))
                if (cursor) Cursor(solid = typed in 1 until command.length)
            }
        }
    }
}

@Composable
fun Cursor(solid: Boolean = false) {
    Span(attrs = {
        classes("cursor")
        if (solid) classes("is-solid")
        attr("aria-hidden", "true")
    })
}

/** " · " with a non-breaking first space: a wrapped line may end on the dot but never starts with one. */
const val DOT_SEP = "\u00A0· "

/** Items on one line, dot-separated by CSS, each kept whole when the line wraps. */
@Composable
fun DotList(items: List<String>, vararg classNames: String, label: String? = null) {
    if (items.isEmpty()) return
    Ul(attrs = {
        classes("dots", *classNames)
        label?.let { attr("aria-label", it) }
    }) {
        items.forEach { item -> Li { Text(item) } }
    }
}

@Composable
fun TechLine(chips: List<String>) = DotList(chips, "tech", label = "Tech")

@Composable
fun ExternalLink(href: String, vararg classNames: String, content: @Composable () -> Unit) {
    A(href = href, attrs = {
        classes(*classNames)
        attr("target", "_blank")
        // The resume is disallowed in robots.txt; nofollow keeps crawlers from treating the link as a lead.
        attr("rel", if (href == Profile.RESUME) "noopener noreferrer nofollow" else "noopener noreferrer")
    }) { content() }
}

/** A link to another site, marked with ↗. */
@Composable
fun OutLink(href: String, label: String, vararg classNames: String) {
    ExternalLink(href, "tlink", *classNames) {
        Text(label)
        Span(attrs = { classes("tlink-arrow"); attr("aria-hidden", "true") }) { Text("↗") }
    }
}

/** A terminal button: `[ label ]`, the brackets and glyphs drawn but not read aloud. */
@Composable
fun TermButtonLabel(label: String, glyph: String? = null, trailing: String? = null) {
    Span(attrs = { classes("tb-bracket"); attr("aria-hidden", "true") }) { Text("[") }
    glyph?.let { Span(attrs = { classes("tb-glyph"); attr("aria-hidden", "true") }) { Text(it) } }
    Text(label)
    trailing?.let { Span(attrs = { classes("tb-glyph"); attr("aria-hidden", "true") }) { Text(it) } }
    Span(attrs = { classes("tb-bracket"); attr("aria-hidden", "true") }) { Text("]") }
}

@Composable
fun ResumeButton() {
    ExternalLink(Profile.RESUME, "tbtn", "tbtn-primary") { TermButtonLabel("resume.pdf", glyph = "↓") }
}

// Compose HTML has no builders for these tags.
private val dlBuilder = ElementBuilder.createBuilder<HTMLElement>("dl")
private val dtBuilder = ElementBuilder.createBuilder<HTMLElement>("dt")
private val ddBuilder = ElementBuilder.createBuilder<HTMLElement>("dd")

@Composable
fun Dl(attrs: AttrBuilderContext<HTMLElement>? = null, content: ContentBuilder<HTMLElement>? = null) =
    TagElement(dlBuilder, attrs, content)

@Composable
fun Dt(attrs: AttrBuilderContext<HTMLElement>? = null, content: ContentBuilder<HTMLElement>? = null) =
    TagElement(dtBuilder, attrs, content)

@Composable
fun Dd(attrs: AttrBuilderContext<HTMLElement>? = null, content: ContentBuilder<HTMLElement>? = null) =
    TagElement(ddBuilder, attrs, content)
