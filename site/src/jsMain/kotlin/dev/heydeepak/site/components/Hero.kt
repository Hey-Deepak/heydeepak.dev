package dev.heydeepak.site.components

import androidx.compose.runtime.*
import dev.heydeepak.site.*
import kotlinx.browser.window
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.dom.*
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random

private const val TYPING_START_DELAY_MS = 600L
private const val KEYSTROKE_MS = 34L
private const val KEYSTROKE_JITTER_MS = 40L
private const val SPACE_PAUSE_MS = 60L
private const val COMMAND_RUN_MS = 320L
private const val STAGGER_MS = 170L
private const val AFTER_OUTPUT_MS = 750L
private const val COUNT_UP_DELAY_MS = 350L
private const val COUNT_UP_MS = 1100.0
private const val COUNT_UP_STAGGER_MS = 140L
private const val COUNT_UP_FRAME_MS = 16L

@Composable
fun Hero() {
    Section(attrs = { id(HERO_ID); classes("hero") }) {
        Div(attrs = { classes("wrap") }) {
            Div(attrs = { classes("hero-grid") }) {
                HeroIntro()
                HeroTerminal(heroSteps)
            }
            ProofStrip(proofStats)
        }
    }
}

@Composable
private fun HeroIntro() {
    Div(attrs = { classes("hero-intro") }) {
        P(attrs = { classes("hero-tag") }) { Text("ANDROID · REAL-TIME VIDEO SDKs · RUST") }
        H1(attrs = { classes("hero-title") }) {
            Text("Hey, I'm ")
            Span(attrs = { classes("name") }) { Text("Deepak") }
        }
        P(attrs = { classes("hero-role") }) {
            Text("SDE 3 · Tech Lead, Client SDKs ")
            Span(attrs = { classes("at") }) { Text("at") }
            Text(" VideoSDK")
        }
        P(attrs = { classes("hero-pitch") }) {
            Text(
                "6+ years on Android. I lead VideoSDK's 4 client SDKs — Android, iOS, Flutter and React Native — " +
                    "on one shared Rust core, and build AI-agent tools on the side."
            )
        }
        Div(attrs = { classes("actions") }) {
            ExternalLink(Profile.RESUME, "btn", "btn-primary") {
                Span(attrs = { classes("btn-glyph"); attr("aria-hidden", "true") }) { Text("↓") }
                Text("Resume")
            }
            A(href = "mailto:${Profile.EMAIL}", attrs = { classes("btn") }) {
                Span(attrs = { classes("btn-glyph"); attr("aria-hidden", "true") }) { Text("✉") }
                Text("Email")
            }
            ExternalLink(Profile.LINKEDIN, "btn") { Text("LinkedIn") }
            ExternalLink(Profile.GITHUB, "btn") { Text("GitHub") }
        }
    }
}

@Composable
private fun HeroTerminal(steps: List<TerminalStep>) {
    val motion = LocalMotion.current
    // current == steps.size means the whole script has run.
    var current by remember { mutableStateOf(if (motion) 0 else steps.size) }
    var typed by remember { mutableStateOf(0) }
    var shown by remember { mutableStateOf(-1) } // output segments printed; -1 = command not run yet
    // On phones the terminal sits below the fold, so wait until it is seen.
    var inView by remember { mutableStateOf(false) }

    LaunchedEffect(inView) {
        if (!motion || !inView) return@LaunchedEffect
        delay(TYPING_START_DELAY_MS)
        steps.forEachIndexed { index, step ->
            current = index
            typed = 0
            shown = -1
            step.command.forEachIndexed { charIndex, char ->
                delay(keystrokeDelay(char))
                typed = charIndex + 1
            }
            delay(COMMAND_RUN_MS)
            if (step.stagger) {
                for (count in 0..step.output.size) {
                    shown = count
                    delay(STAGGER_MS)
                }
            } else {
                shown = step.output.size
            }
            delay(AFTER_OUTPUT_MS)
        }
        current = steps.size
    }

    Div(attrs = {
        classes("term")
        if (motion) {
            ref { element ->
                val stop = observeOnce(element, rootMargin = "0px 0px -20% 0px") { inView = true }
                onDispose { stop() }
            }
        }
    }) {
        Div(attrs = { classes("term-bar") }) {
            Div(attrs = { classes("dots"); attr("aria-hidden", "true") }) {
                Span(attrs = { classes("dot", "dot-red") })
                Span(attrs = { classes("dot", "dot-amber") })
                Span(attrs = { classes("dot", "dot-green") })
            }
            Span(attrs = { classes("term-title") }) { Text("deepak@heydeepak.dev: ~") }
        }
        Div(attrs = { classes("term-body") }) {
            // Screen readers get the whole transcript once instead of a stream of keystrokes.
            Div(attrs = { classes("sr-only") }) { Transcript(steps, steps.size, 0, -1, showCursor = false) }
            // The invisible full transcript reserves the final height, so typing never shifts the layout.
            Div(attrs = { classes("term-layer", "term-ghost"); attr("aria-hidden", "true") }) {
                Transcript(steps, steps.size, 0, -1, showCursor = true)
            }
            Div(attrs = {
                classes("term-layer", "term-live")
                attr("aria-hidden", "true")
                if (isExporting()) attr("data-static", "")
            }) {
                Transcript(steps, current, typed, shown, showCursor = true)
            }
        }
    }
}

@Composable
private fun Transcript(steps: List<TerminalStep>, current: Int, typed: Int, shown: Int, showCursor: Boolean) {
    steps.forEachIndexed { index, step ->
        when {
            index < current -> {
                PromptLine(step.command)
                OutputLine(step.output, step.output.size)
            }
            index == current -> {
                PromptLine(step.command.take(typed), cursor = showCursor && shown < 0, solid = typed > 0)
                if (shown >= 0) OutputLine(step.output, shown)
            }
        }
    }
    val idle = current >= steps.size || shown == steps[current].output.size
    if (idle) PromptLine("", cursor = showCursor)
}

@Composable
private fun PromptLine(command: String, cursor: Boolean = false, solid: Boolean = false) {
    Div(attrs = { classes("t-line", "t-cmd") }) {
        Span(attrs = { classes("t-sign") }) { Text("$") }
        Text(command)
        if (cursor) Cursor(solid)
    }
}

@Composable
private fun OutputLine(segments: List<Seg>, count: Int) {
    Div(attrs = { classes("t-line", "t-out") }) {
        segments.take(count).forEach { seg ->
            when (seg.tone) {
                Tone.PLAIN -> Text(seg.text)
                Tone.ACCENT -> Span(attrs = { classes("t-accent") }) { Text(seg.text) }
                Tone.DIM -> Span(attrs = { classes("t-dim") }) { Text(seg.text) }
                Tone.CHECK -> {
                    Span(attrs = { classes("t-ok") }) { Text(seg.text.take(1)) }
                    Text(seg.text.drop(1))
                }
            }
        }
        // Keeps an empty output line one line tall while its first segment is pending.
        if (count == 0) Text("​")
    }
}

private fun keystrokeDelay(char: Char): Long =
    KEYSTROKE_MS + Random.nextLong(KEYSTROKE_JITTER_MS) + if (char == ' ') SPACE_PAUSE_MS else 0L

@Composable
private fun ProofStrip(stats: List<Stat>) {
    val motion = LocalMotion.current
    var inView by remember { mutableStateOf(!motion) }
    Ul(attrs = {
        classes("proof")
        attr("aria-label", "Highlights")
        if (motion) {
            ref { element ->
                val stop = observeOnce(element, rootMargin = "0px") { inView = true }
                onDispose { stop() }
            }
        }
    }) {
        stats.forEachIndexed { index, stat ->
            Li(attrs = { classes("stat") }) {
                CountUp(stat, start = inView, delayMs = COUNT_UP_DELAY_MS + index * COUNT_UP_STAGGER_MS)
                Div(attrs = { classes("stat-label") }) { Text(stat.label) }
            }
        }
    }
}

@Composable
private fun CountUp(stat: Stat, start: Boolean, delayMs: Long) {
    val motion = LocalMotion.current
    var value by remember { mutableStateOf(if (motion) 0 else stat.value) }
    LaunchedEffect(start) {
        if (!motion || !start) return@LaunchedEffect
        delay(delayMs)
        val startedAt = window.performance.now()
        while (true) {
            val progress = ((window.performance.now() - startedAt) / COUNT_UP_MS).coerceAtMost(1.0)
            value = (easeOutCubic(progress) * stat.value).roundToInt()
            if (progress >= 1.0) break
            delay(COUNT_UP_FRAME_MS)
        }
    }
    Div(attrs = {
        classes("stat-num")
        if (isExporting()) attr("data-static", "")
    }) {
        Text("$value${stat.suffix}")
    }
}

private fun easeOutCubic(progress: Double): Double = 1 - (1 - progress).pow(3)
