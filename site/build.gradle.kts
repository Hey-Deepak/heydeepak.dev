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
    const val TITLE = "Deepak Choudhary — Android & Real-time SDK Tech Lead"
    const val DESCRIPTION = "Deepak Choudhary, SDE 3 and Tech Lead for VideoSDK's Android, iOS, Flutter and " +
        "React Native SDKs on one shared Rust core. 6+ years on Android."
}

kobweb {
    app {
        index {
            description.set(SiteMeta.DESCRIPTION)
            faviconPath.set("/favicon.svg")
            head.add {
                meta { charset = "utf-8" }
                meta { attributes["property"] = "og:type"; content = "website" }
                meta { attributes["property"] = "og:title"; content = SiteMeta.TITLE }
                meta { attributes["property"] = "og:description"; content = SiteMeta.DESCRIPTION }
                meta { attributes["property"] = "og:url"; content = "https://heydeepak.dev/" }
                meta(name = "twitter:card", content = "summary")
                meta(name = "theme-color", content = "#080A10")
                link(rel = "preconnect", href = "https://fonts.googleapis.com")
                link(rel = "preconnect", href = "https://fonts.gstatic.com") { attributes["crossorigin"] = "" }
                link(
                    rel = "stylesheet",
                    href = "https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=JetBrains+Mono:wght@400;500;600;700&display=swap",
                )
                link(rel = "stylesheet", href = "/site.css")
                // Lets CSS hide baked-in (exported) animation targets until the app takes over, avoiding a flash.
                script { unsafe { raw("if (!location.search.includes('_kobwebIsExporting')) document.documentElement.classList.add('js')") } }
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
