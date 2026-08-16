/**
 * Web platform window handle (shared by js(IR) and wasmJs): reads the browser
 * viewport.
 */
package com.appdimens.kmp.core

import com.appdimens.kmp.common.UiModeType
import kotlinx.browser.window

/**
 * EN Web implementation of [AppDimensContext], backed by the browser viewport
 * (`window.innerWidth` / `innerHeight` / `devicePixelRatio`).
 *
 * PT Implementação web de [AppDimensContext], baseada no viewport do navegador.
 */
internal class WebAppDimensContext(
    override val configuration: ScreenConfiguration,
    override val density: Float,
    override val xdpi: Float,
) : AppDimensContext {

    override val isInMultiWindowMode: Boolean get() = false

    override val uiModeType: UiModeType get() = UiModeType.NORMAL
}

/**
 * EN Builds the web default window handle from the current viewport.
 * PT Constrói o handle padrão da web a partir do viewport atual.
 */
actual fun defaultPlatformContext(): AppDimensContext? {
    val widthPx = window.innerWidth.toDouble()
    val heightPx = window.innerHeight.toDouble()
    if (widthPx <= 0.0 || heightPx <= 0.0) return null
    val rawRatio = window.devicePixelRatio.toDouble()
    val ratio = if (rawRatio.isFinite() && rawRatio > 0.0) rawRatio else 1.0
    val widthDp = (widthPx / ratio).toInt()
    val heightDp = (heightPx / ratio).toInt()
    val screen = ScreenConfiguration(
        screenWidthDp = widthDp,
        screenHeightDp = heightDp,
        smallestScreenWidthDp = minOf(widthDp, heightDp),
        densityDpi = (ratio * 160.0).toInt(),
        fontScale = 1f,
        orientation = if (widthDp >= heightDp) ScreenConfiguration.ORIENTATION_LANDSCAPE else ScreenConfiguration.ORIENTATION_PORTRAIT,
        uiMode = 1,
    )
    return WebAppDimensContext(screen, ratio.toFloat(), (ratio * 160.0).toFloat())
}