package dev.heydeepak.site

import kotlinx.browser.document
import org.w3c.dom.Element

/** What a page puts in the head instead of the home page's title, description, social tags and canonical. */
/** [image] is an absolute URL of a 1200x630 share picture; null keeps a text-only card. */
class PageHead(val title: String, val description: String, val url: String, val image: String? = null)

// The site-wide head carries the employer (Person JSON-LD) and og.png (the role line); a page with its own head drops them.
private val HOME_ONLY_HEAD = listOf(
    "script[type='application/ld+json']",
    "meta[property='og:image']",
    "meta[property='og:image:width']",
    "meta[property='og:image:height']",
)

/**
 * Points the head at [page]; returns how to put the site-wide head back (client-side navigation).
 * Runs during export too, so the exported page ships with its own head.
 */
fun applyPageHead(page: PageHead): () -> Unit {
    val head = document.head ?: return {}
    val oldTitle = document.title
    document.title = page.title
    val swapped = listOf(
        Triple("meta[name='description']", "content", page.description),
        Triple("meta[property='og:title']", "content", page.title),
        Triple("meta[property='og:description']", "content", page.description),
        Triple("meta[property='og:url']", "content", page.url),
        Triple("link[rel='canonical']", "href", page.url),
        Triple("meta[name='twitter:card']", "content", if (page.image != null) "summary_large_image" else "summary"),
    ).mapNotNull { (selector, attribute, value) ->
        val element = head.querySelector(selector) ?: return@mapNotNull null
        val old = element.getAttribute(attribute)
        element.setAttribute(attribute, value)
        val undo: () -> Unit = { if (old != null) element.setAttribute(attribute, old) }
        undo
    }
    val dropped: List<Pair<Element, Element?>> = HOME_ONLY_HEAD.mapNotNull { selector ->
        head.querySelector(selector)?.let { element -> element to element.nextElementSibling }
    }
    dropped.forEach { (element, _) -> element.remove() }
    val added = page.image?.let { imageTags(it) }.orEmpty().map { (attribute, name, value) ->
        document.createElement("meta").also { meta ->
            meta.setAttribute(attribute, name)
            meta.setAttribute("content", value)
            head.appendChild(meta)
        }
    }
    return {
        added.forEach { it.remove() }
        document.title = oldTitle
        swapped.forEach { it() }
        // Reversed: each node goes back before its old neighbour, which may itself be one we dropped.
        dropped.asReversed().forEach { (element, next) -> head.insertBefore(element, next) }
    }
}

private const val SHARE_WIDTH = "1200"
private const val SHARE_HEIGHT = "630"

private fun imageTags(image: String) = listOf(
    Triple("property", "og:image", image),
    Triple("property", "og:image:width", SHARE_WIDTH),
    Triple("property", "og:image:height", SHARE_HEIGHT),
    Triple("name", "twitter:image", image),
)
