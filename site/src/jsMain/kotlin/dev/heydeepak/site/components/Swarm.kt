package dev.heydeepak.site.components

import androidx.compose.runtime.*
import dev.heydeepak.site.*
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.DOMRect
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

// Buddy's agents as cursors: on every visit they build the first screen, then tour and build each
// command's output. Decoration only: every word is in the HTML from the start and in the a11y tree.

/** On <html> while the first screen waits for its agents; the head script sets it (SiteMeta.SWARM_BOOT_CLASS), the swarm clears it. */
const val SWARM_BOOT_CLASS = "ag-boot"
const val SWARM_PROGRESS_ID = "ag-progress"
/** On a next line that waits for a replay: how long, in ms (the swarm waits as long). */
const val NEXT_WAIT_ATTR = "data-wait-ms"
// How far a block has got (site.css styles each [Kind.attr]); no attribute means it still waits.
private const val AG_ATTR = "data-ag"

enum class SwarmPhase { BUILDING, BUILT, LIVE }

/**
 * This visit's swarm, or null when nothing may move (reduced motion, export, a background tab). It
 * builds the first screen only if the head script still hides it for the agents (no deep link, app on time).
 */
fun swarmFor(motion: Boolean, textMotion: Boolean): Swarm? {
    val root = document.documentElement
    val boot = motion && textMotion && root?.classList?.contains(SWARM_BOOT_CLASS) == true
    if (!boot) root?.classList?.remove(SWARM_BOOT_CLASS)
    return if (motion) Swarm(compact = window.innerWidth < PHONE_MAX_PX, boot = boot) else null
}

private const val PHONE_MAX_PX = 640
// The two-column hero (site.css): the cards sit beside the pitch, so they can be built alongside it.
private const val WIDE_HERO_PX = 1240
private const val PHONE_PACE = 0.78
// The stacked hero builds its cards after the stats, so it runs a little faster to stay near the desktop's length.
private const val STACKED_PACE = 0.9
private const val PHONE_CURSORS = 6
private const val FAST_CURSORS = 3
private const val PHONE_FAST_CURSORS = 2
private const val MAX_FRAME_MS = 50.0
private const val SELECT_MS = 260
// Of a block's select time: flying to its corner; the rest drags the outline open.
private const val FLY_SHARE = 0.38
private const val BOX_FADE_MS = 280.0
private const val BUILT_HOLD_MS = 1600
// The build opens with every agent's name on show, then helpers go quiet.
private const val INTRO_MS = 1600.0
private const val TAU_FLY_MS = 120.0
private const val TAU_HELP_MS = 320.0
private const val TAU_TOUR_MS = 1100.0
// A command's output: built in about this long, so the shell stays quick.
private const val FAST_SPAN_MS = 1500.0
private const val FAST_STEP_MIN_MS = 110.0
private const val FAST_STEP_MAX_MS = 300.0
private const val FAST_SELECT_MS = 150.0
private const val FAST_TYPE_MS_PER_CELL = 9.0
private const val FAST_TYPE_MAX_MS = 360.0
private const val FAST_SETTLE_MS = 260.0
private const val FAST_MAX_BLOCKS = 10
// Of a command's printed entries, only the first few get agents (`all` prints many).
private const val FAST_MAX_ENTRIES = 2
private const val SHORT_TEXT_CHARS = 90
private const val SHORT_TEXT_MAX_CHILDREN = 4
// A block taller than this share of the view is built part by part.
private const val TALL_SHARE = 0.45
private const val EDGE_PX = 6.0
private const val PROMPT_CLEAR_PX = 40.0
private const val TAG_GAP_PX = 22.0
// A tag hangs this far below its arrow's tip (site.css); with no room down there it moves beside the arrow.
private const val TAG_DROP_PX = 38.0
private const val HELPER_OFFSET_PX = 26.0
private const val CELL_EM = 0.6
private const val UNSET_LABEL = "\u0000"
private const val FOLLOW_MARGIN_PX = 24.0
// Past this, the output just shows (its failsafe) instead of waiting for agents.
private const val RENDER_WAIT_FRAMES = 12
// Below a followed block: room for the build pill, so it never covers the block being built.
private const val FOLLOW_BOTTOM_PX = 84.0
// The build pill's height above the transcript's bottom edge: working cursors stay above it.
private const val PILL_CLEAR_PX = 56.0
// The first frame has no previous one to measure from.
private const val FIRST_FRAME_MS = 16.0
private const val DEFAULT_FONT_PX = 16.0
// The intro spreads the team over the transcript's top 70%, with room for a name tag on the right.
private const val INTRO_MARGIN_PX = 40.0
private const val INTRO_TAG_ROOM_PX = 160.0
private const val INTRO_HEIGHT_SHARE = 0.7
// How long a helper or a touring cursor stays before it moves on.
private const val HELP_PICK_MIN_MS = 450.0
private const val HELP_PICK_MAX_MS = 1100.0
private const val TOUR_PICK_MIN_MS = 2600.0
private const val TOUR_PICK_MAX_MS = 5200.0
// A tour stop: a chip below this roll, a card, button or command below the next, else anywhere.
private const val TOUR_CHIP_ROLL = 0.12
private const val TOUR_LINK_ROLL = 0.27
private const val TOUR_MARGIN_PX = 24.0
private const val TOUR_RIGHT_ROOM_PX = 60.0
// A tour stop's point inside its element: a little right of and below its centre.
private const val HOVER_X_SHARE = 0.6
private const val HOVER_Y_SHARE = 0.55
// A helper on a settling block works along this part of its bottom edge.
private const val HELP_SWEEP_FROM = 0.15
private const val HELP_SWEEP_SHARE = 0.7
private val SCROLL_KEYS = setOf("PageDown", "PageUp", "ArrowDown", "ArrowUp", "Home", "End", " ")
// Scattering from the visitor's pointer: within this radius an agent is pushed away, harder the closer
// and the faster the pointer; a critically damped spring brings it back onto its own path.
private const val DODGE_RADIUS_PX = 140.0
private const val DODGE_PUSH_PX_S2 = 12000.0
private const val DODGE_SPRING = 25.0
private val DODGE_DAMPING = 2 * sqrt(DODGE_SPRING)
private const val DODGE_MAX_PX = 220.0
// A sweep at this speed doubles the push; the boost tops out at [DODGE_MAX_BOOST] times.
private const val DODGE_SPEED_PX_S = 800.0
private const val DODGE_MAX_BOOST = 3.0
// A tap has no speed, so it pushes as hard as a quick sweep.
private const val DODGE_TAP_BOOST = 2.5
// A fast sweep also pushes sideways off its path (a flock parting), up to this share of the push.
private const val DODGE_SIDEWAYS_SPEED_PX_S = 1200.0
private const val DODGE_MAX_SIDEWAYS = 0.8
// An agent at work dodges less, so the block it's building stays easy to follow.
private const val DODGE_WORKING_SHARE = 0.5
// Now and then, an idle agent near a resting pointer shows its name, as if it noticed you.
private const val GLANCE_REST_MS = 1500.0
private const val GLANCE_RADIUS_PX = 260.0
private const val GLANCE_SHOW_MS = 1000.0
private const val GLANCE_EVERY_MS = 7000.0
internal const val MS_PER_S = 1000.0

/** One cursor's name and its colour token; an unlabelled one only shows a name while it works. */
private class AgentSpec(val name: String, val colour: String, val labelled: Boolean = true)

// While touring, only these keep a name tag, so the page stays readable.
private val touringNames = setOf(SwarmNames.BUDDY, SwarmNames.BUILDER, SwarmNames.DESIGNER, SwarmNames.CHECKS)

// Phones keep the first six.
private val roster = listOf(
    AgentSpec(SwarmNames.BUDDY, "--amber"),
    AgentSpec(SwarmNames.BUILDER, "--teal"),
    AgentSpec(SwarmNames.DESIGNER, "--purple"),
    AgentSpec(SwarmNames.CHECKS, "--green"),
    AgentSpec(SwarmNames.claude("whoami"), "--red"),
    AgentSpec(SwarmNames.CLAUDE, "--blue", labelled = false),
    AgentSpec(SwarmNames.SIMPLIFY, "--blue"),
    AgentSpec(SwarmNames.claude("pitch"), "--teal"),
    AgentSpec(SwarmNames.claude("stats"), "--amber"),
    AgentSpec(SwarmNames.claude("cards"), "--purple"),
    AgentSpec(SwarmNames.claude("motd"), "--blue"),
    AgentSpec(SwarmNames.CLAUDE, "--green", labelled = false),
    AgentSpec(SwarmNames.CLAUDE, "--amber", labelled = false),
)

/** How a block draws in: typed behind an I-beam, swept row by row, settled in, children settled in turn, or only outlined. */
private enum class Kind(val attr: String) { TYPE("type"), SWEEP("sweep"), SETTLE("settle"), KIDS("kids"), OUTLINE("done") }

/** One block of the first screen: who builds it, when (desktop ms, phones run faster) and how. */
private class BootStep(
    val selector: String,
    val kind: Kind,
    val agent: String,
    val action: String,
    val atMs: Int,
    val drawMs: Int,
    val selectMs: Int = SELECT_MS,
)

private const val BOOT = ".entry.is-boot"

/**
 * The first screen in reading order; several agents work at once, but one agent holds one block at a
 * time (so a name never shows on two cursors). Cards come after the stats unless they sit beside the pitch.
 */
private fun bootPlan(wideHero: Boolean): List<BootStep> {
    val cardsAt = if (wideHero) listOf(4350, 5000, 5650) else listOf(6300, 6650, 7050)
    val motdAt = if (wideHero) 6750 else 7500
    // Side by side the cards overlap in time, so three agents share them; stacked, they follow each other.
    val cardAgents = listOf(
        SwarmNames.claude("cards") to "building the cards",
        (if (wideHero) SwarmNames.SIMPLIFY to "tidying the cards" else SwarmNames.DESIGNER to "placing the cards"),
        SwarmNames.claude("cards") to "building the cards",
    )
    return listOf(
        BootStep("$BOOT .last-login", Kind.TYPE, SwarmNames.BUDDY, "starting the session", 200, 450),
        BootStep("$BOOT:nth-child(2) > .prompt", Kind.TYPE, SwarmNames.BUDDY, "running whoami", 950, 380, 200),
        BootStep("$BOOT .art", Kind.SWEEP, SwarmNames.DESIGNER, "drawing the name", 1150, 1300, 380),
        BootStep("$BOOT .hero-photo", Kind.SETTLE, SwarmNames.claude("whoami"), "placing the photo", 1700, 380),
        BootStep("$BOOT .hello-text", Kind.TYPE, SwarmNames.BUILDER, "writing whoami", 1950, 520),
        BootStep("$BOOT .hero-role", Kind.TYPE, SwarmNames.BUILDER, "writing whoami", 2750, 560, 200),
        BootStep("$BOOT .hero-tag", Kind.TYPE, SwarmNames.claude("whoami"), "writing the tags", 2650, 650),
        BootStep("$BOOT .tagline", Kind.TYPE, SwarmNames.BUILDER, "writing the headline", 3550, 1100, 320),
        BootStep("$BOOT .hero-pitch", Kind.TYPE, SwarmNames.claude("pitch"), "writing the pitch", 3950, 1700),
        BootStep("$BOOT .actions", Kind.KIDS, SwarmNames.DESIGNER, "placing the buttons", 5150, 650),
        BootStep("$BOOT .hero-stats", Kind.KIDS, SwarmNames.claude("stats"), "setting the stats", 5750, 850),
    ) + cardsAt.mapIndexed { index, at ->
        val (agent, action) = cardAgents[index]
        BootStep("$BOOT .hero-cards > li:nth-child(${index + 1})", Kind.SETTLE, agent, action, at, 450)
    } + listOf(
        BootStep("$BOOT:nth-child(3) > .prompt", Kind.TYPE, SwarmNames.BUDDY, "running motd", motdAt, 450, 200),
        BootStep("$BOOT .motd > :nth-child(1)", Kind.TYPE, SwarmNames.claude("motd"), "writing the motd", motdAt + 400, 480, 220),
        BootStep("$BOOT .motd > :nth-child(2)", Kind.TYPE, SwarmNames.BUILDER, "writing the motd", motdAt + 780, 480, 220),
        BootStep("$BOOT .motd > :nth-child(3)", Kind.TYPE, SwarmNames.claude("motd"), "writing the motd", motdAt + 1160, 480, 220),
        BootStep("$BOOT .motd > :nth-child(4)", Kind.TYPE, SwarmNames.CHECKS, "checking the links", motdAt + 1500, 520, 220),
        BootStep("$BOOT .whoami", Kind.OUTLINE, SwarmNames.CHECKS, "all pass", motdAt + 2300, 500, 420),
    )
}

private class Line(val top: Double, val bottom: Double, val left: Double, val right: Double, val cells: Int)

private class Cursor(val spec: AgentSpec, val root: HTMLElement, val tag: HTMLElement) {
    var x = 0.0
    var y = 0.0
    var targetX = 0.0
    var targetY = 0.0
    var tau = TAU_TOUR_MS
    /** Follows its target exactly (typing, dragging) instead of easing towards it. */
    var snap = false
    var task: Task? = null
    var nextPickMs = 0.0
    // Unset until the first label, so a first "no label" still marks it quiet.
    var label: String? = UNSET_LABEL
    var labelWidth = 0.0
    var ibeam = false
    var flipped = false
    var raised = false
    // Its dodge away from the visitor's pointer, added on top of its own path (x, y).
    var dodgeX = 0.0
    var dodgeY = 0.0
    var dodgeSpeedX = 0.0
    var dodgeSpeedY = 0.0
    var glanceUntilMs = 0.0
}

private enum class Stage { WAIT, SELECT, DRAW, FADE, DONE }

private class Box(val root: HTMLElement, val top: HTMLElement, val left: HTMLElement, val bottom: HTMLElement, val right: HTMLElement, val handles: List<HTMLElement>)

private class Task(
    val el: HTMLElement,
    val kind: Kind,
    val agent: String,
    val action: String?,
    val startMs: Double,
    val selectMs: Double,
    val drawMs: Double,
    val boot: Boolean,
    /** Only for `buddy build`: the pane whose typing this agent follows, timed from [originMs]. */
    val follow: Boolean = false,
    val originMs: Double = startMs,
    /** Cursors that may take it (a command's few agents); empty means any idle one. */
    val crew: List<Cursor> = emptyList(),
) {
    var stage = Stage.WAIT
    var cursor: Cursor? = null
    var box: Box? = null
    var lines: List<Line> = emptyList()
    var cells = 0
    var linesDirty = true
    var boxGoneMs = 0.0
    val drawStartMs get() = startMs + selectMs
    val endMs get() = drawStartMs + drawMs
}

/** A command's printed entries: the element id of each and the agent who leads its build. */
class PrintedEntry(val elementId: String, val lead: String)

/**
 * The swarm. [start] builds the first screen when [boot] (else the agents only tour); [printed]
 * builds a command's output; [skip] finishes the first-screen build at once.
 */
class Swarm(private val compact: Boolean, private val boot: Boolean) {
    var phase by mutableStateOf(if (boot) SwarmPhase.BUILDING else SwarmPhase.LIVE)
        private set
    var enabled by mutableStateOf(true)
        private set
    val building get() = phase == SwarmPhase.BUILDING

    private var layer: HTMLElement? = null
    // Outlines draw only over the transcript's visible area, never over the prompt or chips.
    private var boxLayer: HTMLElement? = null
    private var boxClip = ""
    // The build scrolls the transcript to the block being built, until the visitor scrolls it.
    private var followed = false
    private var visitorScrolled = false
    private val cursors = mutableListOf<Cursor>()
    private val tasks = mutableListOf<Task>()
    private var clockMs = 0.0
    private var lastFrame = -1.0
    private var frameRequested = false
    private var bootStartMs = 0.0
    private var bootEndMs = 0.0
    private val listeners = mutableListOf<Pair<String, (Event) -> Unit>>()
    private val pointer = VisitorPointer()
    private var nextGlanceMs = 0.0

    fun start() {
        layer = element("div", "swarm").apply { setAttribute("aria-hidden", "true") }.also { document.body?.appendChild(it) }
        boxLayer = element("div", "swarm-boxes").also { layer?.appendChild(it) }
        val count = if (compact) PHONE_CURSORS else roster.size
        roster.take(count).forEachIndexed { index, spec -> cursors += makeCursor(spec, index, count) }
        // Without a boot, swarmFor already cleared the boot class.
        if (boot) planBoot()
        listen("resize") { tasks.forEach { it.linesDirty = true } }
        // Focus moving into a block that is still waiting: show everything, so focus is never invisible.
        listen("focusin") { event -> if (building && (event.target as? Element)?.closest(BOOT) != null) skip() }
        listOf("wheel", "touchmove").forEach { type -> listen(type) { visitorScrolled = true } }
        listen("keydown") { event ->
            val typing = (event.target as? Element)?.tagName == "INPUT"
            if (!typing && event.asDynamic().key in SCROLL_KEYS) visitorScrolled = true
        }
        pointer.start(::listen)
        // Fade the cursors in on the next frame.
        window.requestAnimationFrame { cursors.forEach { it.root.classList.add("is-on") } }
        requestFrame()
    }

    fun dispose() {
        listeners.forEach { (type, handler) -> window.removeEventListener(type, handler, true) }
        pointer.dispose()
        layer?.remove()
    }

    private fun listen(type: String, handler: (Event) -> Unit) {
        window.addEventListener(type, handler, true)
        listeners += type to handler
    }

    /** Esc, Skip, a command, or focus: the first screen is shown in full at once. */
    fun skip() = skipBoot(backToTop = true)

    private fun skipBoot(backToTop: Boolean) {
        if (!building) return
        tasks.filter { it.boot }.forEach { finish(it, instantly = true) }
        finishBoot(backToTop)
    }

    fun toggle() {
        enabled = !enabled
        layer?.classList?.toggle("is-off", !enabled)
        pointer.setEnabled(enabled)
        if (enabled) {
            cursors.forEach { it.nextPickMs = 0.0 }
            requestFrame()
        } else {
            tasks.toList().forEach { finish(it, instantly = true) }
        }
    }

    /** Whether a new command's output should wait for its agents. */
    val buildsOutput get() = enabled

    /** A command printed [entries]: a few agents fly in and build the first of them fast. */
    fun printed(entries: List<PrintedEntry>) {
        // The command's own output is scrolled to; the first screen just finishes.
        skipBoot(backToTop = false)
        if (!enabled || entries.isEmpty()) return
        whenRendered(entries.first().elementId, RENDER_WAIT_FRAMES) {
            cursors.filter { it.task == null }.forEach { it.nextPickMs = 0.0 }
            val crew = crewFor()
            entries.forEachIndexed { index, entry ->
                val element = document.getElementById(entry.elementId) as? HTMLElement ?: return@forEachIndexed
                // Only the first entries get agents; the rest (`all`) show as they are.
                if (index < FAST_MAX_ENTRIES) planOutput(element, entry.lead, crew) else revealOutput(element)
            }
            requestFrame()
        }
    }

    /** Compose renders the new entry on one of the next frames; [then] runs once it is in the page. */
    private fun whenRendered(elementId: String, framesLeft: Int, then: () -> Unit) {
        window.requestAnimationFrame {
            when {
                document.getElementById(elementId) != null -> then()
                framesLeft > 0 -> whenRendered(elementId, framesLeft - 1, then)
            }
        }
    }

    // ---------- planning ----------

    private fun planBoot() {
        val wideHero = window.innerWidth >= WIDE_HERO_PX
        val pace = when {
            compact -> PHONE_PACE
            wideHero -> 1.0
            else -> STACKED_PACE
        }
        bootStartMs = clockMs
        bootPlan(wideHero).forEach { step ->
            val element = document.querySelector(step.selector) as? HTMLElement ?: return@forEach
            tasks += Task(
                element, step.kind, step.agent, step.action,
                startMs = bootStartMs + step.atMs * pace,
                selectMs = step.selectMs * pace,
                drawMs = step.drawMs * pace,
                boot = true,
            )
        }
        bootEndMs = tasks.maxOfOrNull { it.endMs } ?: bootStartMs
    }

    /** The output's blocks, in order; a tall block is built part by part. */
    private fun outputBlocks(entry: HTMLElement): List<HTMLElement> {
        val out = entry.querySelector(":scope > .out") as? HTMLElement ?: return emptyList()
        val tall = window.innerHeight * TALL_SHARE
        val blocks = mutableListOf<HTMLElement>()
        out.children.asList().filterIsInstance<HTMLElement>().forEach { child ->
            if (child.classList.contains("sr-only")) child.markDone()
            else if (child.offsetHeight > tall && child.childElementCount > 1) {
                child.setAttribute(AG_ATTR, "open")
                blocks += child.children.asList().filterIsInstance<HTMLElement>()
            } else blocks += child
        }
        return blocks
    }

    private fun planOutput(entry: HTMLElement, lead: String, crew: List<Cursor>) {
        val blocks = outputBlocks(entry)
        if (blocks.isEmpty()) return
        val shown = blocks.take(FAST_MAX_BLOCKS)
        val step = (FAST_SPAN_MS / shown.size).coerceIn(FAST_STEP_MIN_MS, FAST_STEP_MAX_MS)
        val replay = entry.querySelector(".build.is-playing") != null
        shown.forEachIndexed { index, block ->
            val isNext = block.classList.contains("next")
            // A replay's next line waits for the replay, as its own delay says.
            val waitMs = if (isNext) block.getAttribute(NEXT_WAIT_ATTR)?.toDoubleOrNull() else null
            val start = clockMs + (waitMs ?: (index * step))
            val kind = fastKind(block)
            val draw = if (kind == Kind.TYPE) min(FAST_TYPE_MAX_MS, block.textContent.orEmpty().length * FAST_TYPE_MS_PER_CELL) else FAST_SETTLE_MS
            // The lead's blocks carry the session's name (claude · work); helpers keep their own.
            val helper = crew.getOrNull(index % max(crew.size, 1))
            val agent = if (helper == null || helper == crew.first()) lead else helper.spec.name
            tasks += Task(block, kind, agent, null, start, FAST_SELECT_MS, draw, boot = false, crew = crew)
        }
        // Past the cap: shown with the last one.
        blocks.drop(FAST_MAX_BLOCKS).forEach { it.markDone() }
        if (replay) planReplay(entry, crew)
    }

    /** `buddy build`: each pane's own agent follows its typing to the end of the replay. */
    private fun planReplay(entry: HTMLElement, crew: List<Cursor>) {
        val panes = entry.querySelectorAll(".bpane").asList().filterIsInstance<HTMLElement>()
        panes.forEachIndexed { index, pane ->
            val name = pane.getAttribute("aria-label").orEmpty()
            val agent = if (pane.classList.contains("is-phone")) SwarmNames.BUDDY else name
            val lines = pane.querySelector(".bpane-lines") as? HTMLElement ?: return@forEachIndexed
            val preferred = cursors.firstOrNull { it.spec.name == agent }
            tasks += Task(
                lines, Kind.OUTLINE, agent, null,
                startMs = clockMs + FAST_SELECT_MS * (index + 1), selectMs = 0.0, drawMs = buildReplayMs.toDouble(),
                boot = false, follow = true, originMs = clockMs, crew = listOfNotNull(preferred) + crew,
            )
        }
    }

    private fun fastKind(block: HTMLElement): Kind {
        val text = block.textContent.orEmpty()
        val short = text.length <= SHORT_TEXT_CHARS && block.childElementCount <= SHORT_TEXT_MAX_CHILDREN
        return when {
            block.tagName == "H2" || block.tagName == "H3" -> Kind.TYPE
            block.tagName == "P" && short -> Kind.TYPE
            else -> Kind.SETTLE
        }
    }

    private fun revealOutput(entry: HTMLElement) {
        (entry.querySelector(":scope > .out") as? HTMLElement)?.children?.asList()?.forEach { it.markDone() }
    }

    /** A command's agents: a Claude Code session leads, then the designer and checks. */
    private fun crewFor(): List<Cursor> {
        val size = if (compact) PHONE_FAST_CURSORS else FAST_CURSORS
        val idle = cursors.filter { it.task == null }
        val lead = idle.firstOrNull { it.spec.name == SwarmNames.CLAUDE } ?: idle.firstOrNull { it.spec.name.startsWith(SwarmNames.CLAUDE) }
        val helpers = listOf(SwarmNames.DESIGNER, SwarmNames.CHECKS).mapNotNull { name -> idle.firstOrNull { it.spec.name == name } }
        return (listOfNotNull(lead) + helpers + idle).distinct().take(size)
    }

    // ---------- the frame loop ----------

    private fun requestFrame() {
        if (frameRequested || !enabled) return
        frameRequested = true
        window.requestAnimationFrame(::frame)
    }

    private fun frame(now: Double) {
        frameRequested = false
        // Clamped, so a hidden tab (no frames) pauses the build instead of skipping it.
        val dt = if (lastFrame < 0) FIRST_FRAME_MS else min(now - lastFrame, MAX_FRAME_MS)
        lastFrame = now
        clockMs += dt
        val view = view()
        clipBoxes(view)
        tasks.toList().forEach { update(it, view) }
        tasks.removeAll { it.stage == Stage.DONE }
        if (building) {
            val progress = ((clockMs - bootStartMs) / (bootEndMs - bootStartMs)).coerceIn(0.0, 1.0)
            (document.getElementById(SWARM_PROGRESS_ID) as? HTMLElement)?.style?.transform = "scaleX($progress)"
            if (tasks.none { it.boot }) finishBoot(backToTop = true)
        }
        pointer.frame()
        val repellers = pointer.repellers()
        cursors.forEach { steer(it, dt, view, repellers) }
        glance()
        requestFrame()
    }

    /** Pushes [cursor] away from the pointer and taps; its spring pulls it back to its own path. */
    private fun dodge(cursor: Cursor, dt: Double, repellers: List<Repeller>) {
        val seconds = dt / MS_PER_S
        var forceX = -DODGE_SPRING * cursor.dodgeX - DODGE_DAMPING * cursor.dodgeSpeedX
        var forceY = -DODGE_SPRING * cursor.dodgeY - DODGE_DAMPING * cursor.dodgeSpeedY
        val share = if (cursor.task != null) DODGE_WORKING_SHARE else 1.0
        repellers.forEach { from ->
            val dx = cursor.x + cursor.dodgeX - from.x
            val dy = cursor.y + cursor.dodgeY - from.y
            val distance = hypot(dx, dy)
            if (distance >= DODGE_RADIUS_PX) return@forEach
            val closeness = 1 - distance / DODGE_RADIUS_PX
            val speed = from.speed
            val boost = if (from.tap) DODGE_TAP_BOOST else 1 + min(speed / DODGE_SPEED_PX_S, DODGE_MAX_BOOST - 1)
            var nx: Double
            var ny: Double
            if (distance < 1) {
                // Right on top of it: away in any direction.
                val angle = Random.nextDouble(2 * PI)
                nx = cos(angle)
                ny = sin(angle)
            } else {
                nx = dx / distance
                ny = dy / distance
            }
            if (speed > 0) {
                // Sideways from the pointer's path, on the agent's side of it.
                val ux = from.velocityX / speed
                val uy = from.velocityY / speed
                val side = if (ux * ny - uy * nx >= 0) 1.0 else -1.0
                val weight = min(speed / DODGE_SIDEWAYS_SPEED_PX_S, DODGE_MAX_SIDEWAYS)
                nx = nx * (1 - weight) + (-uy * side) * weight
                ny = ny * (1 - weight) + (ux * side) * weight
                // Zero only when the two cancel exactly: then no push this frame.
                val length = hypot(nx, ny).takeIf { it > 0 } ?: 1.0
                nx /= length
                ny /= length
            }
            val push = DODGE_PUSH_PX_S2 * closeness * boost * share
            forceX += nx * push
            forceY += ny * push
        }
        cursor.dodgeSpeedX += forceX * seconds
        cursor.dodgeSpeedY += forceY * seconds
        cursor.dodgeX += cursor.dodgeSpeedX * seconds
        cursor.dodgeY += cursor.dodgeSpeedY * seconds
        val length = hypot(cursor.dodgeX, cursor.dodgeY)
        if (length > DODGE_MAX_PX) {
            cursor.dodgeX *= DODGE_MAX_PX / length
            cursor.dodgeY *= DODGE_MAX_PX / length
        }
    }

    /** Once in a while, an idle agent near the resting pointer shows its name for a moment. */
    private fun glance() {
        cursors.filter { it.glanceUntilMs > 0 && clockMs > it.glanceUntilMs }.forEach {
            it.glanceUntilMs = 0.0
            if (it.task == null) setLabel(it, null)
        }
        if (compact || phase != SwarmPhase.LIVE || clockMs < nextGlanceMs) return
        val resting = pointer.restingMs() ?: return
        if (resting < GLANCE_REST_MS) return
        val (px, py) = pointer.position() ?: return
        fun distance(cursor: Cursor) = hypot(cursor.x - px, cursor.y - py)
        val near = cursors.filter { it.task == null && it.label == null }
            .minByOrNull(::distance)
            ?.takeIf { distance(it) < GLANCE_RADIUS_PX } ?: return
        setLabel(near, near.spec.name)
        near.glanceUntilMs = clockMs + GLANCE_SHOW_MS
        nextGlanceMs = clockMs + GLANCE_EVERY_MS
    }

    /** The window, where the prompt starts, and the transcript's visible area (where blocks are built). */
    private class View(val width: Double, val height: Double, val promptTop: Double, val stage: Rect)

    private fun view(): View {
        val transcript = (document.getElementById(TRANSCRIPT_ID) as? HTMLElement)?.getBoundingClientRect()
        val width = window.innerWidth.toDouble()
        val height = window.innerHeight.toDouble()
        val stage = transcript?.let { Rect(it.left, it.top, it.right, it.bottom) } ?: Rect(0.0, 0.0, width, height)
        return View(width, height, promptTop(), stage)
    }

    private fun clipBoxes(view: View) {
        val clip = "inset(${view.stage.top}px ${view.width - view.stage.right}px ${view.height - view.stage.bottom}px ${view.stage.left}px)"
        if (clip != boxClip) {
            boxClip = clip
            boxLayer?.style?.setProperty("clip-path", clip)
        }
    }

    /** The camera returns to the first screen as designed, unless the visitor took over. */
    private fun backToTop() {
        if (!followed || visitorScrolled) return
        followed = false
        (document.getElementById(TRANSCRIPT_ID) as? HTMLElement)?.let { smoothScroll(it, 0.0) }
    }

    /** The first-screen build brings its next block into view, like a camera following the agents. */
    private fun follow(outline: Rect, view: View) {
        if (visitorScrolled) return
        val transcript = document.getElementById(TRANSCRIPT_ID) as? HTMLElement ?: return
        val below = outline.bottom - (view.stage.bottom - FOLLOW_BOTTOM_PX)
        if (below <= 0) return
        // Bring its bottom into view, but never push its top out.
        val by = min(below, outline.top - view.stage.top - FOLLOW_MARGIN_PX)
        if (by <= 0) return
        followed = true
        smoothScroll(transcript, transcript.scrollTop + by)
    }

    private fun smoothScroll(element: HTMLElement, top: Double) {
        val options: dynamic = js("({})")
        options.top = top
        options.behavior = "smooth"
        element.asDynamic().scrollTo(options)
    }

    private class Rect(val left: Double, val top: Double, val right: Double, val bottom: Double) {
        val width get() = right - left
        val height get() = bottom - top
    }

    private fun promptTop(): Double =
        (document.querySelector(".prompt-line") as? HTMLElement)?.getBoundingClientRect()?.top ?: window.innerHeight.toDouble()

    private fun update(task: Task, view: View) {
        when (task.stage) {
            Stage.WAIT -> if (clockMs >= task.startMs) beginSelect(task, view)
            Stage.SELECT -> select(task)
            Stage.DRAW -> draw(task)
            Stage.FADE -> {
                task.box?.let { place(it, outlineOf(task), 1.0) }
                if (clockMs >= task.boxGoneMs) {
                    task.box?.root?.remove()
                    task.stage = Stage.DONE
                }
            }
            Stage.DONE -> Unit
        }
    }

    /** What the outline hugs: a text block's lines, a row's children, or the block itself. */
    private fun outlineOf(task: Task): Rect {
        val box = task.el.getBoundingClientRect()
        if ((task.kind == Kind.TYPE || task.kind == Kind.SWEEP) && task.lines.isNotEmpty()) {
            return Rect(
                box.left + task.lines.minOf { it.left }, box.top + task.lines.first().top,
                box.left + task.lines.maxOf { it.right }, box.top + task.lines.last().bottom,
            )
        }
        if (task.kind == Kind.KIDS) {
            val kids = task.el.children.asList().map { it.getBoundingClientRect() }.filter { it.width > 0 }
            if (kids.isNotEmpty()) return Rect(kids.minOf { it.left }, kids.minOf { it.top }, kids.maxOf { it.right }, kids.maxOf { it.bottom })
        }
        return Rect(box.left, box.top, box.right, box.bottom)
    }

    private fun beginSelect(task: Task, view: View) {
        if (task.kind == Kind.TYPE || task.kind == Kind.SWEEP) measure(task)
        var rect = outlineOf(task)
        if (task.boot && building) {
            // The last check looks over the whole first screen, so the camera returns to the top for it.
            if (task.kind == Kind.OUTLINE) backToTop() else follow(rect, view)
            rect = outlineOf(task)
        }
        // On stage: in the transcript's visible area now, or about to be (the follow scroll is smooth).
        val onScreen = rect.height > 0 && rect.top < view.stage.bottom + (if (followed && !visitorScrolled) rect.height else 0.0) && rect.bottom > view.stage.top
        // Off screen: a command's block just shows; a first-screen block builds on time with no agent.
        if (!onScreen && !task.boot && !task.follow) {
            finish(task, instantly = true)
            return
        }
        val cursor = if (onScreen) assign(task) else null
        // A replay pane waits for its own agent (busy for a moment with the output's blocks).
        if (task.follow && cursor == null && clockMs < task.originMs + task.drawMs) return
        task.cursor = cursor
        if (cursor != null) {
            cursor.task = task
            cursor.snap = false
            cursor.tau = TAU_FLY_MS
            // Phones: the name only, so a tag fits beside its cursor.
            setLabel(cursor, task.agent + (task.action?.takeIf { !compact }?.let { " · $it" } ?: ""))
            task.box = makeBox(cursor.spec.colour)
        }
        task.stage = Stage.SELECT
        if (task.selectMs <= 0.0) beginDraw(task)
    }

    private fun select(task: Task) {
        val rect = outlineOf(task)
        val share = ((clockMs - task.startMs) / task.selectMs).coerceIn(0.0, 1.0)
        val cursor = task.cursor
        if (share < FLY_SHARE) {
            cursor?.apply { targetX = rect.left; targetY = rect.top }
            task.box?.let { place(it, rect, 0.0) }
        } else {
            val drag = easeInOut((share - FLY_SHARE) / (1 - FLY_SHARE))
            cursor?.apply {
                snap = true
                targetX = rect.left + rect.width * drag
                targetY = rect.top + rect.height * drag
            }
            task.box?.let { place(it, rect, drag) }
        }
        if (share >= 1.0) beginDraw(task)
    }

    private fun beginDraw(task: Task) {
        task.stage = Stage.DRAW
        if (task.kind == Kind.TYPE || task.kind == Kind.SWEEP) {
            measure(task)
            // Clip first, then show: the block appears already masked, never in full for a frame.
            task.el.style.setProperty("clip-path", staircase(task, 0))
            task.cursor?.let { cursor ->
                cursor.ibeam = task.kind == Kind.TYPE
                task.lines.firstOrNull()?.let { cursor.root.style.setProperty("--ib", "${(it.bottom - it.top).roundToInt()}px") }
            }
        }
        if (task.follow) task.cursor?.ibeam = true
        // A waiting next line's own delay was for the plain reveal; the agent reveals it now.
        if (task.el.hasAttribute(NEXT_WAIT_ATTR)) task.el.style.removeProperty("animation-delay")
        task.el.setAttribute(AG_ATTR, task.kind.attr)
    }

    private fun draw(task: Task) {
        val rect = task.el.getBoundingClientRect()
        val share = ((clockMs - task.drawStartMs) / task.drawMs).coerceIn(0.0, 1.0)
        val outline = outlineOf(task)
        task.box?.let { place(it, outline, 1.0) }
        val cursor = task.cursor
        when {
            task.follow -> cursor?.let { followTyping(task, it, rect) }
            task.kind == Kind.TYPE || task.kind == Kind.SWEEP -> {
                if (task.linesDirty) measure(task)
                val revealed = (share * task.cells).toInt()
                task.el.style.setProperty("clip-path", staircase(task, revealed))
                caret(task, revealed)?.let { (x, y) -> cursor?.apply { snap = true; targetX = rect.left + x; targetY = rect.top + y } }
            }
            // Settling and outlined blocks: the agent works along the bottom edge, off the text.
            else -> cursor?.apply {
                snap = false
                tau = TAU_HELP_MS
                targetX = outline.left + outline.width * (HELP_SWEEP_FROM + HELP_SWEEP_SHARE * share)
                targetY = outline.bottom + HELPER_OFFSET_PX / 2
            }
        }
        if (share >= 1.0) finish(task, instantly = false)
    }

    /** `buddy build`: the I-beam sits at the end of whichever line of the pane is typing now. */
    private fun followTyping(task: Task, cursor: Cursor, pane: DOMRect) {
        val elapsed = clockMs - task.originMs
        val lines = task.el.querySelectorAll(".bl-line").asList().filterIsInstance<HTMLElement>()
        val current = lines.lastOrNull { msOf(it, "--at") <= elapsed } ?: return run {
            cursor.snap = false
            cursor.targetX = pane.left + EDGE_PX * 2
            cursor.targetY = pane.top + EDGE_PX * 2
        }
        val text = current.querySelector(".bl-text") as? HTMLElement ?: return
        val box = text.getBoundingClientRect()
        cursor.root.style.setProperty("--ib", "${box.height.roundToInt()}px")
        cursor.snap = true
        cursor.targetX = box.right
        cursor.targetY = box.top + box.height / 2
    }

    private fun msOf(element: HTMLElement, property: String) =
        element.style.getPropertyValue(property).removeSuffix("ms").trim().toDoubleOrNull() ?: 0.0

    /** Shows the block for good, lets its outline fade and frees its agent. */
    private fun finish(task: Task, instantly: Boolean) {
        task.el.style.removeProperty("clip-path")
        // Settled blocks keep their kind, so their move-in animation isn't cut short.
        if (instantly || (task.kind != Kind.SETTLE && task.kind != Kind.KIDS)) task.el.markDone()
        task.cursor?.let { release(it) }
        task.cursor = null
        if (instantly) {
            task.box?.root?.remove()
            task.stage = Stage.DONE
        } else {
            task.box?.root?.classList?.add("is-out")
            task.boxGoneMs = clockMs + BOX_FADE_MS
            task.stage = Stage.FADE
        }
    }

    private fun finishBoot(backToTop: Boolean) {
        if (!building) return
        if (backToTop) backToTop()
        document.documentElement?.classList?.remove(SWARM_BOOT_CLASS)
        document.querySelectorAll("$BOOT [$AG_ATTR]").asList().forEach { (it as? Element)?.removeAttribute(AG_ATTR) }
        phase = SwarmPhase.BUILT
        cursors.filter { it.task == null }.forEach { setLabel(it, null) }
        window.setTimeout({ phase = SwarmPhase.LIVE }, BUILT_HOLD_MS)
    }

    // ---------- text geometry ----------

    /** The block's text lines, relative to its box, each split into whole cells. */
    private fun measure(task: Task) {
        val base = task.el.getBoundingClientRect()
        val range = document.createRange()
        range.selectNodeContents(task.el)
        val rects = range.asDynamic().getClientRects()
        val count = (rects.length as Int)
        val bands = mutableListOf<DoubleArray>()
        for (index in 0 until count) {
            val r = rects.item(index) ?: continue
            val width = (r.width as Double)
            val height = (r.height as Double)
            if (width < 1 || height < 1) continue
            val top = (r.top as Double) - base.top
            val bottom = (r.bottom as Double) - base.top
            val left = (r.left as Double) - base.left
            val right = (r.right as Double) - base.left
            val middle = (top + bottom) / 2
            val band = bands.firstOrNull { middle >= it[0] && middle <= it[1] }
            if (band == null) bands += doubleArrayOf(top, bottom, left, right)
            else {
                band[0] = min(band[0], top); band[1] = max(band[1], bottom)
                band[2] = min(band[2], left); band[3] = max(band[3], right)
            }
        }
        val cell = CELL_EM * (window.getComputedStyle(task.el).fontSize.removeSuffix("px").toDoubleOrNull() ?: DEFAULT_FONT_PX)
        task.lines = bands.sortedBy { it[0] }.map { Line(it[0], it[1], it[2], it[3], max(1, ((it[3] - it[2]) / cell).roundToInt())) }
        task.cells = task.lines.sumOf { it.cells }
        task.linesDirty = false
    }

    /** The line [revealed] cells reach, and how far along it: (line, x). */
    private fun reach(task: Task, revealed: Int): Pair<Line, Double>? {
        var left = revealed
        task.lines.forEach { line ->
            if (left < line.cells) return line to line.left + (line.right - line.left) * left / line.cells
            left -= line.cells
        }
        return task.lines.lastOrNull()?.let { it to it.right }
    }

    /** A clip that shows every line above the current one, and the current one up to the caret. */
    private fun staircase(task: Task, revealed: Int): String {
        val (line, x) = reach(task, revealed) ?: return "inset(0 0 0 0)"
        return "polygon(0 0, 100% 0, 100% ${line.top}px, ${x}px ${line.top}px, ${x}px ${line.bottom}px, 0 ${line.bottom}px)"
    }

    private fun caret(task: Task, revealed: Int): Pair<Double, Double>? =
        reach(task, revealed)?.let { (line, x) -> x to (line.top + line.bottom) / 2 }

    // ---------- cursors ----------

    /**
     * The agent's own cursor; else, for a Claude Code session, any free session cursor (sessions have no
     * fixed colour). A named role is never shown under another cursor: no free cursor, no agent.
     */
    private fun assign(task: Task): Cursor? {
        val idle = cursors.filter { it.task == null }
        idle.firstOrNull { it.spec.name == task.agent }?.let { return it }
        if (!task.agent.startsWith(SwarmNames.CLAUDE)) return null
        val sessions = idle.filter { it.spec.name.startsWith(SwarmNames.CLAUDE) }
        return sessions.firstOrNull { it in task.crew } ?: sessions.firstOrNull { !it.spec.labelled } ?: sessions.firstOrNull()
    }

    private fun release(cursor: Cursor) {
        cursor.task = null
        cursor.snap = false
        cursor.ibeam = false
        cursor.nextPickMs = 0.0
        // Off task, no tag: it would sit on text being read or built.
        setLabel(cursor, null)
    }

    /** Touring shows a core agent's name only while it hovers a chip or card. */
    private fun touringLabel(cursor: Cursor): String? =
        if (!compact && cursor.spec.name in touringNames) cursor.spec.name else null

    private fun setLabel(cursor: Cursor, text: String?) {
        if (cursor.label == text) return
        cursor.label = text
        cursor.root.classList.toggle("is-quiet", text == null)
        cursor.tag.textContent = text.orEmpty()
        // Measured on the next frame, with the others, not now.
        cursor.labelWidth = -1.0
    }

    private fun steer(cursor: Cursor, dt: Double, view: View, repellers: List<Repeller>) {
        if (cursor.task == null && clockMs >= cursor.nextPickMs) pick(cursor, view)
        val follow = if (cursor.snap) 1.0 else 1 - exp(-dt / cursor.tau)
        cursor.x += (cursor.targetX - cursor.x) * follow
        cursor.y += (cursor.targetY - cursor.y) * follow
        dodge(cursor, dt, repellers)
        // Always inside the view, so a cursor never widens the page; at work, inside the transcript, off the prompt.
        val stage = view.stage
        val area = when {
            cursor.task == null -> Rect(0.0, 0.0, view.width, view.height)
            building -> Rect(stage.left, stage.top, stage.right, stage.bottom - PILL_CLEAR_PX)
            else -> stage
        }
        val x = (cursor.x + cursor.dodgeX).coerceIn(area.left, area.right - EDGE_PX * 3)
        val y = (cursor.y + cursor.dodgeY).coerceIn(area.top, area.bottom - EDGE_PX * 3)
        cursor.root.style.transform = "translate3d(${x}px, ${y}px, 0)"
        if (cursor.labelWidth < 0) cursor.labelWidth = cursor.tag.offsetWidth.toDouble()
        val flip = x + cursor.labelWidth + TAG_GAP_PX > view.width
        if (flip != cursor.flipped) { cursor.flipped = flip; cursor.root.classList.toggle("is-flip", flip) }
        // Never over the build pill or off the bottom: the tag moves beside the arrow instead.
        val floor = if (building) stage.bottom - PILL_CLEAR_PX else view.height
        val raise = !cursor.ibeam && y + TAG_DROP_PX > floor
        if (raise != cursor.raised) { cursor.raised = raise; cursor.root.classList.toggle("is-up", raise) }
        cursor.root.classList.toggle("is-ibeam", cursor.ibeam)
    }

    /** No task: arrive (the intro), help an agent at work (hover its block's edge), or tour the screen slowly. */
    private fun pick(cursor: Cursor, view: View) {
        val intro = building && clockMs - bootStartMs < INTRO_MS
        if (intro) {
            // The team arrives spread over the screen, names on show, before it gathers at the blocks.
            val stage = view.stage
            val x = randomBetween(stage.left + INTRO_MARGIN_PX, stage.right - INTRO_TAG_ROOM_PX)
            val y = randomBetween(stage.top + INTRO_MARGIN_PX, stage.top + stage.height * INTRO_HEIGHT_SHARE)
            aim(cursor, x, y, view, TAU_HELP_MS * 2)
            cursor.nextPickMs = bootStartMs + INTRO_MS
            return
        }
        val active = tasks.filter { it.cursor != null && (it.stage == Stage.DRAW || it.stage == Stage.SELECT) }
        val next = if (building) tasks.filter { it.stage == Stage.WAIT }.minByOrNull { it.startMs } else null
        val around = active.randomOrNull() ?: next
        if (around != null) {
            setLabel(cursor, null)
            val rect = outlineOf(around)
            val (x, y) = aroundEdge(rect)
            aim(cursor, x, y, view, TAU_HELP_MS)
            cursor.nextPickMs = clockMs + randomBetween(HELP_PICK_MIN_MS, HELP_PICK_MAX_MS)
            return
        }
        val transcript = (document.getElementById(TRANSCRIPT_ID) as? HTMLElement)?.getBoundingClientRect() ?: return
        val roll = Random.nextDouble()
        val spot = when {
            roll < TOUR_CHIP_ROLL -> visibleCentre(".chip", view)
            roll < TOUR_LINK_ROLL -> visibleCentre(".hcard, .tbtn, .cmd", view)
            else -> null
        }
        val x = spot?.first ?: randomBetween(transcript.left + TOUR_MARGIN_PX, transcript.right - TOUR_RIGHT_ROOM_PX)
        val y = spot?.second ?: randomBetween(transcript.top + TOUR_MARGIN_PX, view.promptTop - PROMPT_CLEAR_PX)
        setLabel(cursor, if (spot != null) touringLabel(cursor) else null)
        aim(cursor, x, y, view, TAU_TOUR_MS, allowBelowPrompt = spot != null)
        cursor.nextPickMs = clockMs + randomBetween(TOUR_PICK_MIN_MS, TOUR_PICK_MAX_MS)
    }

    /** A random point from [from] up to [until], or just past [from] when the span is empty (a tiny view). */
    private fun randomBetween(from: Double, until: Double) = Random.nextDouble(from, max(from + 1, until))

    private fun aim(cursor: Cursor, x: Double, y: Double, view: View, tau: Double, allowBelowPrompt: Boolean = false) {
        cursor.snap = false
        cursor.tau = tau
        cursor.targetX = x.coerceIn(EDGE_PX, view.width - EDGE_PX * 4)
        val floor = if (allowBelowPrompt) view.height - EDGE_PX * 4 else view.promptTop - PROMPT_CLEAR_PX
        cursor.targetY = y.coerceIn(EDGE_PX, max(EDGE_PX, floor))
    }

    private fun aroundEdge(rect: Rect): Pair<Double, Double> {
        val along = Random.nextDouble()
        return when (Random.nextInt(4)) {
            0 -> rect.left + rect.width * along to rect.top - HELPER_OFFSET_PX
            1 -> rect.right + HELPER_OFFSET_PX to rect.top + rect.height * along
            2 -> rect.left + rect.width * along to rect.bottom + HELPER_OFFSET_PX
            else -> rect.left - HELPER_OFFSET_PX to rect.top + rect.height * along
        }
    }

    private fun visibleCentre(selector: String, view: View): Pair<Double, Double>? {
        val seen = document.querySelectorAll(selector).asList().filterIsInstance<HTMLElement>()
            .map { it.getBoundingClientRect() }
            .filter { it.width > 0 && it.top > 0 && it.bottom < view.height }
        return seen.randomOrNull()?.let { it.left + it.width * HOVER_X_SHARE to it.top + it.height * HOVER_Y_SHARE }
    }

    // ---------- elements ----------

    private fun makeCursor(spec: AgentSpec, index: Int, count: Int): Cursor {
        val root = element("div", "ag-cur")
        root.style.setProperty("--c", "var(${spec.colour})")
        root.innerHTML = ARROW_SVG
        root.appendChild(element("span", "ag-ibeam"))
        val tag = element("span", "ag-tag")
        root.appendChild(tag)
        layer?.appendChild(root)
        val cursor = Cursor(spec, root, tag)
        // Enter from the transcript's edges, spread out.
        val stage = view().stage
        val along = (index + 0.5) / count
        val (x, y) = when (index % 4) {
            0 -> stage.left + stage.width * along to stage.top + EDGE_PX
            1 -> stage.right - EDGE_PX * 4 to stage.top + (stage.height - PROMPT_CLEAR_PX) * along
            2 -> stage.left + stage.width * (1 - along) to stage.bottom - PROMPT_CLEAR_PX
            else -> stage.left + EDGE_PX to stage.top + (stage.height - PROMPT_CLEAR_PX) * (1 - along)
        }
        cursor.x = x; cursor.y = y; cursor.targetX = x; cursor.targetY = y
        // The build opens with the team's names; they go quiet as they start helping.
        setLabel(cursor, if (building && spec.labelled) spec.name else null)
        return cursor
    }

    private fun makeBox(colour: String): Box {
        val root = element("div", "ag-sel")
        root.style.setProperty("--c", "var($colour)")
        val edges = listOf("h", "v", "h", "v").map { element("i", "ag-e $it").also(root::appendChild) }
        val handles = List(4) { element("i", "ag-h").also(root::appendChild) }
        root.appendChild(element("i", "ag-x"))
        boxLayer?.appendChild(root)
        return Box(root, edges[0], edges[1], edges[2], edges[3], handles)
    }

    /** Draws the outline at [rect], opened [open] of the way from its top-left corner (transforms only). */
    private fun place(box: Box, rect: Rect, open: Double) {
        val width = rect.width
        val height = rect.height
        box.root.style.transform = "translate3d(${rect.left}px, ${rect.top}px, 0)"
        val w = "${width}px"
        val h = "${height}px"
        if (box.top.style.width != w) { box.top.style.width = w; box.bottom.style.width = w }
        if (box.left.style.height != h) { box.left.style.height = h; box.right.style.height = h }
        val x = width * open
        val y = height * open
        box.top.style.transform = "scaleX($open)"
        box.left.style.transform = "scaleY($open)"
        box.bottom.style.transform = "translateY(${y}px) scaleX($open)"
        box.right.style.transform = "translateX(${x}px) scaleY($open)"
        listOf(0.0 to 0.0, x to 0.0, 0.0 to y, x to y).forEachIndexed { index, (hx, hy) ->
            box.handles[index].style.transform = "translate(${hx}px, ${hy}px)"
        }
    }
}

/** A new, detached element: the swarm's and the pointer's decoration. */
internal fun element(tag: String, className: String) = (document.createElement(tag) as HTMLElement).also { it.className = className }

private fun easeInOut(t: Double) = if (t < 0.5) 2 * t * t else 1 - (-2 * t + 2).let { it * it } / 2

/** Shown as it is, with no build animation. */
private fun Element.markDone() = setAttribute(AG_ATTR, Kind.OUTLINE.attr)

/** The build's status pill above the prompt: who is building, how far along, and Skip (or Esc). */
@Composable
fun SwarmBar(swarm: Swarm) {
    if (swarm.phase == SwarmPhase.LIVE) return
    Div(attrs = { classes("ag-anchor") }) {
        Div(attrs = {
            classes("ag-bar")
            if (swarm.phase == SwarmPhase.BUILT) classes("is-done")
        }) {
            if (swarm.phase == SwarmPhase.BUILDING) {
                Span(attrs = { classes("ag-text") }) {
                    Span(attrs = { classes("ag-who") }) { Text(SwarmNames.BUDDY) }
                    Text(DOT_SEP)
                    Span(attrs = { classes("ag-long") }) { Text(SWARM_BUILDING) }
                    Span(attrs = { classes("ag-short") }) { Text(SWARM_BUILDING_SHORT) }
                }
                Button(attrs = {
                    classes("ag-skip")
                    attr("type", "button")
                    onClick { swarm.skip() }
                }) {
                    Text("skip")
                    Span(attrs = { classes("ag-esc"); attr("aria-hidden", "true") }) { Text("esc") }
                }
                Span(attrs = { classes("ag-track"); attr("aria-hidden", "true") }) {
                    Span(attrs = { id(SWARM_PROGRESS_ID); classes("ag-fill") })
                }
            } else {
                Span(attrs = { classes("ag-text") }) { Text(SWARM_BUILT) }
            }
        }
    }
}

/** After the build: the agents can be switched off (and on again) for this visit. */
@Composable
fun SwarmToggle(swarm: Swarm) {
    if (swarm.phase != SwarmPhase.LIVE) return
    Button(attrs = {
        classes("titlebar-agents")
        attr("type", "button")
        attr("aria-pressed", swarm.enabled.toString())
        onClick { swarm.toggle() }
    }) {
        Text("agents: ${if (swarm.enabled) "on" else "off"}")
    }
}
