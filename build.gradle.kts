plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

// Pin Node.js to v22 LTS — Vercel's Linux env lacks libatomic.so.1 needed by Node v25
// Kotlin/Wasm uses WasmNodeJsEnvSpec (separate from JS NodeJsEnvSpec)
plugins.withType<org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsRootPlugin> {
    extensions.findByType<org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsEnvSpec>()?.apply {
        version.set("22.12.0")
    }
}
