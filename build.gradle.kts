plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

// Pin Node.js to v22 LTS — Vercel's Linux env lacks libatomic.so.1 needed by Node v25
// The Kotlin Gradle plugin creates WasmNodeJsEnvSpec on BOTH the root project (for npm/yarn
// setup tasks) and each subproject that uses wasmJs (for webpack bundling). We must pin the
// version on all of them, otherwise webpack will still try to use Node v25.
allprojects {
    plugins.withType<org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsPlugin> {
        extensions.findByType<org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsEnvSpec>()?.apply {
            version.set("22.12.0")
        }
    }
}
plugins.withType<org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsRootPlugin> {
    extensions.findByType<org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsEnvSpec>()?.apply {
        version.set("22.12.0")
    }
}
