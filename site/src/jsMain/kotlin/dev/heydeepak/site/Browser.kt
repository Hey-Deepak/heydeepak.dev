package dev.heydeepak.site

import androidx.compose.runtime.staticCompositionLocalOf
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.isExporting
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

external class IntersectionObserver(
    callback: (entries: Array<IntersectionObserverEntry>, observer: IntersectionObserver) -> Unit,
    options: dynamic = definedExternally,
) {
    fun observe(target: Element)
    fun disconnect()
}

external interface IntersectionObserverEntry {
    val isIntersecting: Boolean
    val target: Element
}

/** Whether decoration may animate (typing, map lines): false while exporting, for reduced motion, background tabs, or once the baked prompts were shown. */
val LocalMotion = staticCompositionLocalOf { true }

/** Whether hero output may stream in: [LocalMotion] and the head script still hiding the baked text (its shorter grace). */
val LocalTextMotion = staticCompositionLocalOf { true }

fun isExporting(): Boolean = AppGlobals.isExporting

/** False while exporting, for reduced motion, and for tabs opened in the background. */
fun motionAllowed(): Boolean =
    !isExporting() &&
        document.asDynamic().visibilityState != "hidden" &&
        !window.matchMedia("(prefers-reduced-motion: reduce)").matches

/** False once the head script's grace period has shown the baked prompts, so typing them again would hide them. */
fun bakedHeroHidden(): Boolean = document.documentElement?.classList?.contains("js") == true

/** False once the head script's shorter text grace has shown the baked hero output. */
fun bakedTextHidden(): Boolean = document.documentElement?.classList?.contains("js-text") == true

/** Milliseconds since navigation start. */
fun msSinceLoad(): Double = window.performance.now()

/** Stops the head script's grace timer from unhiding the baked prompts; never set during export, or the snapshot would keep it. */
fun markAppReady() {
    if (!isExporting()) document.documentElement.asDynamic().dataset.app = "ready"
}

/** Calls [onVisible] once when [element] enters the viewport; returns a disposer. */
fun observeOnce(element: Element, rootMargin: String = "0px 0px -12% 0px", onVisible: () -> Unit): () -> Unit {
    val options: dynamic = js("({})")
    options.rootMargin = rootMargin
    val observer = IntersectionObserver({ entries, self ->
        if (entries.any { it.isIntersecting }) {
            self.disconnect()
            onVisible()
        }
    }, options)
    observer.observe(element)
    return { observer.disconnect() }
}

fun focusWithoutScroll(element: HTMLElement) {
    val options: dynamic = js("({})")
    options.preventScroll = true
    element.asDynamic().focus(options)
}

/**
 * Brings [entryId] to the top of the scrolling [containerId], growing the [spacerId] at the end so even
 * a short last output can sit at the top, like a terminal you read from the command down.
 */
fun scrollEntryToTop(containerId: String, entryId: String, spacerId: String, smooth: Boolean) {
    val box = document.getElementById(containerId) as? HTMLElement ?: return
    val entry = document.getElementById(entryId) as? HTMLElement ?: return
    val spacer = document.getElementById(spacerId) as? HTMLElement ?: return
    spacer.style.height = "0px"
    val top = entry.offsetTop - SCROLL_TOP_GAP_PX
    // Measured from the spacer, not scrollHeight: scrollHeight never reports less than the box.
    val bottomPadding = window.getComputedStyle(box).paddingBottom.removeSuffix("px").toDoubleOrNull() ?: 0.0
    val missing = box.clientHeight - (spacer.offsetTop - top) - bottomPadding
    spacer.style.height = "${missing.coerceAtLeast(0.0)}px"
    val options: dynamic = js("({})")
    options.top = top
    options.behavior = if (smooth) "smooth" else "auto"
    box.asDynamic().scrollTo(options)
}

// Space kept above the command line when it's scrolled to the top.
private const val SCROLL_TOP_GAP_PX = 12

/** The URL hash without "#", or null. */
fun currentHash(): String? = window.location.hash.removePrefix("#").ifBlank { null }

/** Sets (or clears) the hash without the browser jumping to it. */
fun setHash(id: String?) {
    val url = if (id == null) window.location.pathname + window.location.search else "#$id"
    window.history.replaceState(null, "", url)
}

fun focusById(id: String) {
    (document.getElementById(id) as? HTMLElement)?.let(::focusWithoutScroll)
}

fun openInNewTab(url: String) {
    window.open(url, "_blank", "noopener")
}

fun copyToClipboard(text: String, onCopied: () -> Unit, onFailed: () -> Unit) {
    val clipboard = window.navigator.asDynamic().clipboard
    if (clipboard == null) {
        onFailed()
        return
    }
    clipboard.writeText(text).then({ onCopied() }, { onFailed() })
}

fun isTouchOnly(): Boolean = window.matchMedia("(hover: none)").matches

fun isMacLike(): Boolean = window.navigator.platform.contains("Mac") ||
    window.navigator.userAgent.contains("iPhone") || window.navigator.userAgent.contains("iPad")
