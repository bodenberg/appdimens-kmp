/**
 * EN Web (Wasm) entry point of the sample app — Compose Multiplatform demo
 *    in the browser.
 * PT Entry point Web (Wasm) do app sample — demo Compose Multiplatform no
 *    navegador.
 */
package com.example.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.appdimens.dynamic.core.AppDimensProvider
import com.example.app.compose.SdpDemoScreen

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport("composeApp") {
        AppDimensProvider {
            SdpDemoScreen()
        }
    }
}
