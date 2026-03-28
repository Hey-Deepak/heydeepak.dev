plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(17)

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "buddy-web.js"
            }
        }
        binaries.executable()
    }
}

// Override binaryen version - default 123 doesn't exist on GitHub releases
@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
rootProject.extensions.findByName("wasmJsBinaryenSpec")?.let {
    @Suppress("DEPRECATION_ERROR")
    (it as org.jetbrains.kotlin.gradle.targets.js.binaryen.BinaryenEnvSpec).version.set("128")
}

kotlin {
    sourceSets {
        wasmJsMain.dependencies {
            // Required npm dependency for kotlinx-datetime (transitive from Ktor)
            implementation(npm("@js-joda/core", "5.6.1"))

            // Compose Multiplatform
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)

            // Ktor Client (JS engine for Wasm)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.js)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // Serialization
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
