/**
 * EN iOS entry point of the test lab — Compose Multiplatform on UIKit.
 *    Exposed to the Xcode host app as `MainViewControllerKt.MainViewController()`.
 * PT Entry point iOS do test lab — Compose Multiplatform no UIKit.
 *    Exposto ao host app do Xcode como `MainViewControllerKt.MainViewController()`.
 */
package com.example.benchlab

import androidx.compose.ui.window.ComposeUIViewController
import com.appdimens.dynamic.core.AppDimensProvider
import com.example.benchlab.BenchlabScreen

fun MainViewController() = ComposeUIViewController {
    AppDimensProvider {
        BenchlabScreen()
    }
}
