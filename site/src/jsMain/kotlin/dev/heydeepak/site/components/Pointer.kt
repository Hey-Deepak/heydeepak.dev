package dev.heydeepak.site.components

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import kotlin.math.exp
import kotlin.math.hypot

// The visitor's pointer as one of the swarm: an agent-style arrow named `you` (mouse only; the native
// cursor comes back whenever this one can't be drawn), and where the agents should scatter from.

/** On <html> while the drawn pointer replaces the native one. */
private const val YOU_CLASS = "ag-you-on"
private const val FINE_POINTER = "(hover: hover) and (pointer: fine)"
private const val YOU_LABEL = "you"
// The `you` tag fades after this long without movement, and comes back on the next move.
private const val STILL_MS = 1400.0
// Pointer speed: smoothed over a few events, and fading when it stops.
private const val SPEED_SMOOTHING = 0.5
private const val SPEED_FADE_MS = 160.0
// A tap scatters the agents near it for this long (touch has no hover to follow).
private const val TAP_PULSE_MS = 220.0
// A drawn glyph is hit-tested once per frame at most; text within this many px counts as under the pointer.
private const val TEXT_SLOP_PX = 1.0
// Clickable things show the hand, the same set the site styles with a pointer cursor.
private const val CLICKABLE = "a[href], button, [role=option], .chip, .hcard, label[for], summary, select"
private const val TYPEABLE = "input, textarea, [contenteditable=true]"

/** What the drawn pointer shows: the arrow, a pointing hand over clickable things, an I-beam over text. */
private enum class Shape(val cls: String?) { ARROW(null), HAND("is-hand"), IBEAM("is-ibeam") }

/**
 * Something the agents scatter from: the pointer, or a tap. Its velocity (px/s; a tap has none) makes
 * the scatter stronger and, for a fast sweep, pushes the agents sideways off its path.
 */
internal class Repeller(val x: Double, val y: Double, val velocityX: Double = 0.0, val velocityY: Double = 0.0, val tap: Boolean = false) {
    val speed get() = hypot(velocityX, velocityY)
}

/**
 * Tracks the visitor's pointer for the swarm. With a fine pointer ([drawn]) it also draws it: moved in
 * the pointermove handler itself (no frame of lag), reshaped once per frame from what is under it.
 */
internal class VisitorPointer(private val drawn: Boolean = window.matchMedia(FINE_POINTER).matches) {
    private var root: HTMLElement? = null
    private var enabled = true
    private var showing = false
    private var awake = false
    private var shape = Shape.ARROW
    private var moved = false
    private var x = 0.0
    private var y = 0.0
    private var present = false
    private var velocityX = 0.0
    private var velocityY = 0.0
    private var lastMoveMs = 0.0
    private var lastFrameMs = 0.0
    private var tapX = 0.0
    private var tapY = 0.0
    private var tapAtMs = Double.NEGATIVE_INFINITY

    fun start(listen: (String, (Event) -> Unit) -> Unit) {
        if (drawn) root = makeRoot().also { document.body?.appendChild(it) }
        listen("pointermove", ::onMove)
        listen("pointerdown") { event ->
            val e = event.asDynamic()
            // Touch: no pointer to follow, so a tap scatters the agents near it (the tap still does its job).
            if (e.pointerType != "mouse") {
                tapX = e.clientX as Double
                tapY = e.clientY as Double
                tapAtMs = now()
            }
        }
        // Left the window (no element to go to), or the tab hid: the drawn pointer goes, the native one is back.
        listen("pointerout") { event -> if (event.asDynamic().relatedTarget == null) leave() }
        listen("visibilitychange") { if (document.asDynamic().visibilityState == "hidden") leave() }
    }

    fun dispose() {
        leave()
        root?.remove()
    }

    /** `agents: off` hands the pointer back to the system. */
    fun setEnabled(on: Boolean) {
        enabled = on
        if (!on) leave()
    }

    private fun onMove(event: Event) {
        val e = event.asDynamic()
        if (e.pointerType != "mouse") return
        val time = now()
        // The event's own position is its newest (coalesced) sample: draw there, now.
        val newX = e.clientX as Double
        val newY = e.clientY as Double
        if (present) {
            val elapsed = (time - lastMoveMs).coerceAtLeast(1.0)
            velocityX = velocityX * SPEED_SMOOTHING + (newX - x) / elapsed * MS_PER_S * (1 - SPEED_SMOOTHING)
            velocityY = velocityY * SPEED_SMOOTHING + (newY - y) / elapsed * MS_PER_S * (1 - SPEED_SMOOTHING)
        }
        x = newX
        y = newY
        present = true
        lastMoveMs = time
        moved = true
        if (!drawn || !enabled) return
        root?.style?.transform = "translate3d(${x}px, ${y}px, 0)"
        if (!showing) show()
        if (!awake) {
            awake = true
            root?.classList?.add("is-awake")
        }
    }

    private fun show() {
        showing = true
        root?.classList?.add("is-on")
        // Only now, with the drawn pointer in place, does the native one go.
        document.documentElement?.classList?.add(YOU_CLASS)
    }

    private fun leave() {
        present = false
        if (!showing) return
        showing = false
        root?.classList?.remove("is-on")
        document.documentElement?.classList?.remove(YOU_CLASS)
    }

    /** Once per swarm frame: the shape under the pointer, the `you` tag after stillness, the speed fading out. */
    fun frame() {
        val time = now()
        val dt = time - lastFrameMs
        lastFrameMs = time
        if (time - lastMoveMs > dt) {
            val fade = exp(-dt / SPEED_FADE_MS)
            velocityX *= fade
            velocityY *= fade
        }
        if (!showing) return
        if (moved) {
            moved = false
            setShape(shapeAt(x, y))
        }
        if (awake && time - lastMoveMs > STILL_MS) {
            awake = false
            root?.classList?.remove("is-awake")
        }
    }

    /** The pointer (if it's over the page) and a recent tap: what the agents scatter from. */
    fun repellers(): List<Repeller> {
        val tap = if (now() - tapAtMs < TAP_PULSE_MS) Repeller(tapX, tapY, tap = true) else null
        val mouse = if (present) Repeller(x, y, velocityX, velocityY) else null
        return listOfNotNull(mouse, tap)
    }

    /** How long the pointer has rested over the page, or null if it isn't there. */
    fun restingMs(): Double? = if (present) now() - lastMoveMs else null

    /** Where the pointer is, if it's over the page. */
    fun position(): Pair<Double, Double>? = if (present) x to y else null

    private fun setShape(next: Shape) {
        if (next == shape) return
        shape.cls?.let { root?.classList?.remove(it) }
        next.cls?.let { root?.classList?.add(it) }
        shape = next
    }

    private fun shapeAt(px: Double, py: Double): Shape {
        val target = document.elementFromPoint(px, py) ?: return Shape.ARROW
        return when {
            target.closest(TYPEABLE) != null -> Shape.IBEAM
            target.closest(CLICKABLE) != null -> Shape.HAND
            overText(target, px, py) -> Shape.IBEAM
            else -> Shape.ARROW
        }
    }

    /** Whether the point is on one of [element]'s own lines of text (not the empty space beside them). */
    private fun overText(element: Element, px: Double, py: Double): Boolean {
        val range = document.createRange()
        return element.childNodes.asList().any { node ->
            if (node.nodeType != Node.TEXT_NODE || node.textContent.isNullOrBlank()) return@any false
            range.selectNodeContents(node)
            val rects = range.asDynamic().getClientRects()
            (0 until (rects.length as Int)).any { index ->
                val r = rects.item(index)
                px >= (r.left as Double) - TEXT_SLOP_PX && px <= (r.right as Double) + TEXT_SLOP_PX &&
                    py >= (r.top as Double) - TEXT_SLOP_PX && py <= (r.bottom as Double) + TEXT_SLOP_PX
            }
        }
    }

    private fun makeRoot(): HTMLElement {
        val root = element("div", "ag-you")
        root.setAttribute("aria-hidden", "true")
        root.innerHTML = ARROW_SVG + HAND_SVG
        root.appendChild(element("span", "ag-ibeam"))
        root.appendChild(element("span", "ag-tag").also { it.textContent = YOU_LABEL })
        return root
    }

    private fun now() = window.performance.now()
}

/** The swarm's arrow, tip at the element's origin (agents and `you` share it). */
internal const val ARROW_SVG =
    "<svg class=\"ag-arrow\" viewBox=\"0 0 18 22\" aria-hidden=\"true\"><path d=\"M1.5 1.5 L1.5 18.5 L6 14.2 L9.2 20.5 L12 19.2 L8.9 13 L15 13 Z\"/></svg>"

// A pointing hand in the same style, fingertip at (6.5, 1.5); site.css puts that on the pointer.
private const val HAND_SVG =
    "<svg class=\"ag-hand\" viewBox=\"0 0 18 22\" aria-hidden=\"true\"><path d=\"M6.5 1.5C7.55 1.5 8.4 2.35 8.4 3.4V9.3" +
        "C9.5 8.7 10.9 9.1 11.3 10.2C12.3 9.7 13.6 10.1 14 11.2C15 10.8 16.2 11.5 16.2 12.7V16C16.2 18.5 14.2 20.5 11.7 20.5" +
        "H9.7C8.3 20.5 7 19.9 6.1 18.8L2.6 14.5C2 13.8 2.1 12.8 2.8 12.2C3.5 11.7 4.4 11.8 4.6 12.2V3.4C4.6 2.35 5.45 1.5 6.5 1.5Z\"/>" +
        "<path class=\"ag-crease\" d=\"M8.4 9.3V12.6M11.3 10.2V12.8M14 11.2V13.2\"/></svg>"
