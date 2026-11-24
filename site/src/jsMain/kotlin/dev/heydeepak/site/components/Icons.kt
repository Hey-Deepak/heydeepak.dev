package dev.heydeepak.site.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Span

/** Line icons in the Lucide style (24 grid, 2px stroke, currentColor); only their inner markup lives here. */
enum class Icon(val paths: String) {
    TERMINAL("<polyline points=\"4 17 10 11 4 5\"/><line x1=\"12\" x2=\"20\" y1=\"19\" y2=\"19\"/>"),
    PHONE("<rect width=\"14\" height=\"20\" x=\"5\" y=\"2\" rx=\"2\"/><path d=\"M12 18h.01\"/>"),
    LAPTOP(
        "<path d=\"M20 16V7a2 2 0 0 0-2-2H6a2 2 0 0 0-2 2v9m16 0H4m16 0 1.28 2.55a1 1 0 0 1-.9 1.45H3.62a1 1 0 0 1-.9-1.45L4 16\"/>",
    ),
    WINDOWS("<rect width=\"20\" height=\"16\" x=\"2\" y=\"4\" rx=\"2\"/><path d=\"M2 9h20M6 4v5M10 4v5\"/>"),
    KEY(
        "<path d=\"M2.586 17.414A2 2 0 0 0 2 18.828V21a1 1 0 0 0 1 1h3a1 1 0 0 0 1-1v-1a1 1 0 0 1 1-1h1a1 1 0 0 0 " +
            "1-1v-1a1 1 0 0 1 1-1h.172a2 2 0 0 0 1.414-.586l.814-.814a6.5 6.5 0 1 0-4-4z\"/>" +
            "<circle cx=\"16.5\" cy=\"7.5\" r=\".5\" fill=\"currentColor\"/>",
    ),
    SHIELD(
        "<path d=\"M20 13c0 5-3.5 7.5-7.66 8.95a1 1 0 0 1-.67-.01C7.5 20.5 4 18 4 13V6a1 1 0 0 1 1-1c2 0 4.5-1.2 " +
            "6.24-2.72a1.17 1.17 0 0 1 1.52 0C14.51 3.81 17 5 19 5a1 1 0 0 1 1 1z\"/><path d=\"m9 12 2 2 4-4\"/>",
    ),
    UNLINK("<path d=\"M18 6 6 18M6 6l12 12\"/>"),
    CLOUD_OFF(
        "<path d=\"m2 2 20 20\"/><path d=\"M5.782 5.782A7 7 0 0 0 9 19h8.5a4.5 4.5 0 0 0 1.307-.193\"/>" +
            "<path d=\"M21.532 16.5A4.5 4.5 0 0 0 17.5 10h-1.79A7.008 7.008 0 0 0 10 5.07\"/>",
    ),
    GLOBE("<circle cx=\"12\" cy=\"12\" r=\"10\"/><path d=\"M12 2a14.5 14.5 0 0 0 0 20 14.5 14.5 0 0 0 0-20M2 12h20\"/>"),
    HOTSPOT(
        "<path d=\"M4.9 19.1C1 15.2 1 8.8 4.9 4.9M7.8 16.2c-2.3-2.3-2.3-6.1 0-8.5\"/><circle cx=\"12\" cy=\"12\" r=\"2\"/>" +
            "<path d=\"M16.2 7.8c2.3 2.3 2.3 6.1 0 8.5M19.1 4.9C23 8.8 23 15.1 19.1 19\"/>",
    ),
    USB(
        "<circle cx=\"10\" cy=\"7\" r=\"1\"/><circle cx=\"4\" cy=\"20\" r=\"1\"/><path d=\"M4.7 19.3 19 5\"/>" +
            "<path d=\"m21 3-3 1 2 2Z\"/><path d=\"M9.26 7.68 5 12l2 5\"/><path d=\"m10 14 5 2 3.5-3.5\"/>" +
            "<path d=\"m18 12 1-1 1 1-1 1Z\"/>",
    ),
    ARROW_LEFT("<path d=\"m12 19-7-7 7-7\"/><path d=\"M19 12H5\"/>"),
    ARROW_UP("<path d=\"m5 12 7-7 7 7\"/><path d=\"M12 19V5\"/>"),
    ARROW_DOWN("<path d=\"M12 5v14\"/><path d=\"m19 12-7 7-7-7\"/>"),
    SHIFT("<path d=\"M9 18v-6H5l7-7 7 7h-4v6H9z\"/>"),
    ENTER("<polyline points=\"9 10 4 15 9 20\"/><path d=\"M20 4v7a4 4 0 0 1-4 4H4\"/>"),
    ARROW_RIGHT("<path d=\"M5 12h14\"/><path d=\"m12 5 7 7-7 7\"/>"),
    ARROW_LEFT_RIGHT("<path d=\"M8 3 4 7l4 4M4 7h16M16 21l4-4-4-4M20 17H4\"/>"),
    CHECK("<path d=\"M20 6 9 17l-5-5\"/>"),
    MAIL("<rect width=\"20\" height=\"16\" x=\"2\" y=\"4\" rx=\"2\"/><path d=\"m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7\"/>"),
    PLUS("<path d=\"M5 12h14M12 5v14\"/>"),
}

/** A decorative icon; whatever it stands for is always in the text beside it. */
@Composable
fun SvgIcon(icon: Icon, vararg classNames: String) {
    Span(attrs = {
        classes("icon", *classNames)
        attr("aria-hidden", "true")
        // Compose HTML has no SVG builder here, so the markup goes in once the span exists (export bakes it in).
        ref { span ->
            span.innerHTML = "<svg viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\" " +
                "stroke-linecap=\"round\" stroke-linejoin=\"round\">${icon.paths}</svg>"
            onDispose { }
        }
    })
}
