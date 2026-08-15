/**
 * EN Desktop (JVM) entry point of the sample app — Compose Desktop demo.
 * PT Entry point Desktop (JVM) do app sample — demo Compose Desktop.
 */
package com.example.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.appdimens.dynamic.core.AppDimensProvider
import com.example.app.compose.SdpDemoScreen

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "AppDimens SDP Demo",
    ) {
        AppDimensProvider {
            SdpDemoScreen()
        }
    }
}
