/**
 * EN macOS native entry point of the sample app — Compose Multiplatform window
 *    driven by the AppKit run loop (official JetBrains pattern for native
 *    macOS targets: there is no `application {}` wrapper outside the JVM).
 * PT Entry point macOS nativo do app sample — janela Compose Multiplatform
 *    dirigida pelo run loop do AppKit (padrão oficial JetBrains para targets
 *    nativos macOS: não há wrapper `application {}` fora da JVM).
 */
package com.example.app

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import com.appdimens.dynamic.core.AppDimensProvider
import com.example.app.compose.SdpDemoScreen
import platform.AppKit.NSApplication
import platform.AppKit.NSApplicationActivationPolicy
import platform.AppKit.NSApplicationDelegateProtocol
import platform.darwin.NSObject

fun main() {
    val nsApplication = NSApplication.sharedApplication()
    nsApplication.setActivationPolicy(NSApplicationActivationPolicy.NSApplicationActivationPolicyRegular)
    nsApplication.delegate = object : NSObject(), NSApplicationDelegateProtocol {
        override fun applicationShouldTerminateAfterLastWindowClosed(sender: NSApplication): Boolean = true
    }
    Window(
        title = "AppDimens SDP Demo",
        size = DpSize(900.dp, 700.dp),
    ) {
        AppDimensProvider {
            SdpDemoScreen()
        }
    }
    nsApplication.run()
}
