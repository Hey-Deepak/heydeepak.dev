import com.varabyte.kobweb.gradle.application.extensions.export
import com.varabyte.kobweb.gradle.application.extensions.index
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.unsafe

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
}

group = "dev.heydeepak.site"
version = "1.0-SNAPSHOT"

// const so the head lambda below doesn't capture the script object (breaks the configuration cache).
object SiteMeta {
    const val URL = "https://heydeepak.dev/"
    const val PERSON_JSON_LD =
        """{"@context":"https://schema.org","@type":"Person","name":"Deepak Choudhary","url":"https://heydeepak.dev/",""" +
            """"image":"https://heydeepak.dev/photo.jpg","jobTitle":"SDE 3 · Tech Lead, Client SDKs",""" +
            """"worksFor":{"@type":"Organization","name":"VideoSDK","url":"https://www.videosdk.live/"},""" +
            """"alumniOf":{"@type":"CollegeOrUniversity","name":"JECRC University"},""" +
            """"sameAs":["https://github.com/Hey-Deepak","https://www.linkedin.com/in/hey-deepak","https://medium.com/@HeyDeepak"]}"""
    // <title> comes from site.title in site/.kobweb/conf.yaml, which must equal this.
    const val TITLE = "Deepak Choudhary — SDE 3 · Tech Lead, Client SDKs"
    const val DESCRIPTION = "Deepak Choudhary, SDE 3 and Tech Lead for VideoSDK's Android, iOS, Flutter and " +
        "React Native SDKs on one shared core. 6+ years in mobile (Android); builds AI-agent tools."
    // Longest the baked hero text stays hidden for the app to stream it: hero text is readable by then, app or not.
    const val TEXT_GRACE_MS = 1200
    // Longest the baked decoration (prompts, map lines) waits for the app to animate it.
    const val ANIMATION_GRACE_MS = 3000
    // On <html> while the first screen waits for the agents to build it (Swarm.kt SWARM_BOOT_CLASS).
    const val SWARM_BOOT_CLASS = "ag-boot"
}

kobweb {
    app {
        export {
            // Any unregistered route renders the error page; static hosts serve that file for missing paths.
            addExtraRoute("/this-page-does-not-exist", "404.html")
        }
        index {
            description.set(SiteMeta.DESCRIPTION)
            faviconPath.set("/favicon.svg")
            // The exported HTML is already the readable page, so the bundle must not delay its first paint.
            scriptAttributes.put("defer", "")
            head.add {
                link(rel = "canonical", href = SiteMeta.URL)
                meta { attributes["property"] = "og:type"; content = "website" }
                meta { attributes["property"] = "og:title"; content = SiteMeta.TITLE }
                meta { attributes["property"] = "og:description"; content = SiteMeta.DESCRIPTION }
                meta { attributes["property"] = "og:url"; content = SiteMeta.URL }
                meta { attributes["property"] = "og:image"; content = "${SiteMeta.URL}og.png" }
                meta { attributes["property"] = "og:image:width"; content = "1200" }
                meta { attributes["property"] = "og:image:height"; content = "630" }
                meta(name = "twitter:card", content = "summary_large_image")
                meta(name = "theme-color", content = "#080A10")
                // Starts the fonts alongside the CSS so text rarely paints in a fallback font that shifts on swap.
                listOf("/fonts/jetbrains-mono-latin.woff2", "/fonts/jetbrains-mono-symbols.woff2").forEach { font ->
                    link(rel = "preload", href = font) {
                        attributes["as"] = "font"
                        attributes["type"] = "font/woff2"
                        attributes["crossorigin"] = ""
                    }
                }
                link(rel = "apple-touch-icon", href = "/apple-touch-icon.png")
                link(rel = "stylesheet", href = "/site.css")
                script(type = "application/ld+json") { unsafe { raw(SiteMeta.PERSON_JSON_LD) } }
                // Hides the baked hero text (js-text), decoration (js) and, for the agents to build, the first screen
                // (ag-boot; not for deep links or reduced motion) until the app takes over; each grace period shows them if it never does.
                script {
                    unsafe {
                        raw(
                            "if (!location.search.includes('_kobwebIsExporting')) { const root = document.documentElement; " +
                                // Nested ifs: Kobweb reads the head as XML, so no `&&`.
                                "root.classList.add('js', 'js-text'); if (!location.hash) { if (!matchMedia(" +
                                "'(prefers-reduced-motion: reduce)').matches) root.classList.add('${SiteMeta.SWARM_BOOT_CLASS}') } " +
                                "const drop = (name) => { if (root.dataset.app !== 'ready') root.classList.remove(name) }; " +
                                "setTimeout(() => { drop('js-text'); drop('${SiteMeta.SWARM_BOOT_CLASS}') }, ${SiteMeta.TEXT_GRACE_MS}); " +
                                "setTimeout(() => drop('js'), ${SiteMeta.ANIMATION_GRACE_MS}) }",
                        )
                    }
                }
            }
        }
    }
}

kotlin {
    configAsKobwebApplication("heydeepak")

    sourceSets {
        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
        }
    }
}
