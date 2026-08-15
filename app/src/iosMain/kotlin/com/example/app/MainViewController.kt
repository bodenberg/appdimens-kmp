/**
 * EN iOS entry point of the sample app — Compose Multiplatform on UIKit.
 *    Exposed to the Xcode host app as `MainViewControllerKt.MainViewController()`.
 * PT Entry point iOS do app sample — Compose Multiplatform no UIKit.
 *    Exposto ao host app do Xcode como `MainViewControllerKt.MainViewController()`.
 */
package com.example.app

import androidx.compose.ui.window.ComposeUIViewController
import com.appdimens.kmp.core.AppDimensProvider
import com.example.app.compose.SdpDemoScreen

fun MainViewController() = ComposeUIViewController {
    AppDimensProvider {
        SdpDemoScreen()
    }
}
