// Top-level build file for AppDimens KMP.
plugins {
    alias(libs.plugins.android.kmp.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    // EN Same publishing plugin as the Android original (appdimens-dynamic):
    //    com.vanniktech.maven.publish. Each library module applies it in its own
    //    plugins {} block and configures mavenPublishing {} inline in its own
    //    build.gradle.kts — exactly like the Android original does.
    // PT Mesmo plugin de publicação do Android original (appdimens-dynamic):
    //    com.vanniktech.maven.publish. Cada módulo de biblioteca o aplica no seu
    //    próprio bloco plugins {} e configura mavenPublishing {} inline no seu
    //    build.gradle.kts — exatamente como o Android original faz.
    alias(libs.plugins.vanniktech.maven.publish) apply false
}

// NOTE: no manual root `clean` task here — the Kotlin wasm Node.js root plugin
// registers its own root-level `clean`, and a manual one would collide
// (duplicate task). The root `clean` is provided by that plugin when any wasm
// module is configured; module-level cleans are always available via `:module:clean`.
