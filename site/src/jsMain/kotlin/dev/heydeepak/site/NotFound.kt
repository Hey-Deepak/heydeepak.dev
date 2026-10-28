package dev.heydeepak.site

import androidx.compose.runtime.*
import com.varabyte.kobweb.core.init.InitKobweb
import com.varabyte.kobweb.core.init.InitKobwebContext
import dev.heydeepak.site.components.Cursor
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.*

// Shown in the exported 404.html until the app takes over and swaps in the real path.
private const val EXPORT_PLACEHOLDER_PATH = "/this-page"
private const val NOT_FOUND_TITLE = "No such page · Deepak Choudhary"

@InitKobweb
fun initRouter(ctx: InitKobwebContext) {
    ctx.router.setErrorPage { NotFoundPage() }
    // Static hosts serve the home page at /index.html too; the router strips ".html" and would show the 404.
    ctx.router.registerRedirect("/index", "/")
}

@Composable
private fun NotFoundPage() {
    val path = remember { if (isExporting()) EXPORT_PLACEHOLDER_PATH else window.location.pathname }
    LaunchedEffect(Unit) { document.title = NOT_FOUND_TITLE }
    Main(attrs = { classes("wrap", "nf") }) {
        Div(attrs = { classes("term") }) {
            Div(attrs = { classes("term-bar") }) {
                Div(attrs = { classes("dots"); attr("aria-hidden", "true") }) {
                    Span(attrs = { classes("dot", "dot-red") })
                    Span(attrs = { classes("dot", "dot-amber") })
                    Span(attrs = { classes("dot", "dot-green") })
                }
                Span(attrs = { classes("term-title") }) { Text("deepak@heydeepak.dev: ~") }
            }
            Div(attrs = { classes("term-body") }) {
                H1(attrs = { classes("sr-only") }) { Text("404: no such page") }
                Div(attrs = { classes("t-line") }) {
                    Span(attrs = { classes("t-sign") }) { Text("$") }
                    Text("cd $path")
                }
                Div(attrs = { classes("t-line", "t-out") }) {
                    Span(attrs = { classes("t-accent") }) { Text("cd: no such page: ") }
                    Text(path)
                }
                Div(attrs = { classes("t-line", "nf-home") }) {
                    Span(attrs = { classes("t-sign") }) { Text("$") }
                    A(href = "/", attrs = { classes("nf-link") }) { Text("cd ~") }
                    Span(attrs = { classes("t-dim", "nf-hint") }) { Text("  # back to the home page") }
                    Cursor()
                }
            }
        }
    }
}
