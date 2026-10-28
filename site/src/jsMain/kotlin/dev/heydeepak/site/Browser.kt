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

/** False while exporting (snapshot holds final content), for reduced motion, and for tabs opened in the background (throttled timers). */
val LocalMotion = staticCompositionLocalOf { true }

fun isExporting(): Boolean = AppGlobals.isExporting

fun motionAllowed(): Boolean =
    !isExporting() &&
        document.asDynamic().visibilityState != "hidden" &&
        !window.matchMedia("(prefers-reduced-motion: reduce)").matches

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

fun scrollToSection(id: String, smooth: Boolean) {
    val target = document.getElementById(id) as? HTMLElement ?: return
    val options: dynamic = js("({})")
    options.behavior = if (smooth) "smooth" else "auto"
    options.block = "start"
    target.scrollIntoView(options)
    window.history.replaceState(null, "", "#$id")
    focusWithoutScroll(target)
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
