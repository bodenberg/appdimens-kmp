@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

/**
 * iOS platform window handle: wraps `UIScreen.mainScreen`.
 */
package com.appdimens.kmp.core

import com.appdimens.kmp.common.UiModeType
import platform.CoreGraphics.CGRectGetHeight
import platform.CoreGraphics.CGRectGetWidth
import platform.UIKit.UIScreen

/**
 * EN iOS implementation of [AppDimensContext], backed by `UIScreen.mainScreen`.
 * Points ≈ dp on iOS; `scale` is the density multiplier.
 *
 * PT Implementação iOS de [AppDimensContext], baseada em `UIScreen.mainScreen`.
 */
internal class IosAppDimensContext(
    override val configuration: ScreenConfiguration,
    override val density: Float,
    override val xdpi: Float,
) : AppDimensContext {

    override val isInMultiWindowMode: Boolean get() = false

    override val uiModeType: UiModeType get() = UiModeType.NORMAL
}

/**
 * EN Builds the iOS default window handle from `UIScreen.mainScreen`.
 * PT Constrói o handle padrão do iOS a partir de `UIScreen.mainScreen`.
 */
actual fun defaultPlatformContext(): AppDimensContext? {
    val screen = UIScreen.mainScreen
    val bounds = screen.bounds
    val widthPt = CGRectGetWidth(bounds)
    val heightPt = CGRectGetHeight(bounds)
    if (widthPt <= 0f || heightPt <= 0f) return null
    val scale = screen.scale.toFloat().takeIf { it > 0f } ?: 1f
    val widthDp = widthPt.toInt()
    val heightDp = heightPt.toInt()
    val screenConfig = ScreenConfiguration(
        screenWidthDp = widthDp,
        screenHeightDp = heightDp,
        smallestScreenWidthDp = minOf(widthDp, heightDp),
        densityDpi = (scale * 160f).toInt(),
        fontScale = 1f,
        orientation = if (widthDp >= heightDp) ScreenConfiguration.ORIENTATION_LANDSCAPE else ScreenConfiguration.ORIENTATION_PORTRAIT,
        uiMode = 1,
    )
    return IosAppDimensContext(screenConfig, scale, scale * 160f)
}