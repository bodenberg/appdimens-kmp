/**
 * EN macOS native entry point of the test lab — Compose Multiplatform window
 *    driven by the AppKit run loop (official JetBrains pattern for native
 *    macOS targets: there is no `application {}` wrapper outside the JVM).
 * PT Entry point macOS nativo do test lab — janela Compose Multiplatform
 *    dirigida pelo run loop do AppKit (padrão oficial JetBrains para targets
 *    nativos macOS: não há wrapper `application {}` fora da JVM).
 */
package com.example.benchlab

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import com.appdimens.kmp.core.AppDimensProvider
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
        title = "AppDimens BenchLab",
        size = DpSize(1100.dp, 800.dp),
    ) {
        AppDimensProvider {
            BenchlabScreen()
        }
    }
    nsApplication.run()
}
