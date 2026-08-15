// Top-level build file for AppDimens KMP.
plugins {
    alias(libs.plugins.android.kmp.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.compose.multiplatform) apply false
}

// NOTE: no manual root `clean` task here — the Kotlin wasm Node.js root plugin
// registers its own root-level `clean`, and a manual one would collide
// (duplicate task). The root `clean` is provided by that plugin when any wasm
// module is configured; module-level cleans are always available via `:module:clean`.