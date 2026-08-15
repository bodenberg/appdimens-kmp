@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

/**
 * macOS platform window handle: wraps `NSScreen.mainScreen`.
 */
package com.appdimens.kmp.core

import com.appdimens.kmp.common.UiModeType
import platform.AppKit.NSScreen
import platform.CoreGraphics.CGRectGetHeight
import platform.CoreGraphics.CGRectGetWidth

/**
 * EN macOS implementation of [AppDimensContext], backed by `NSScreen.mainScreen`.
 * PT Implementação macOS de [AppDimensContext], baseada em `NSScreen.mainScreen`.
 */
internal class MacosAppDimensContext(
    override val configuration: ScreenConfiguration,
    override val density: Float,
    override val xdpi: Float,
) : AppDimensContext {

    override val isInMultiWindowMode: Boolean get() = false

    override val uiModeType: UiModeType get() = UiModeType.NORMAL
}

/**
 * EN Builds the macOS default window handle from `NSScreen.mainScreen`.
 * PT Constrói o handle padrão do macOS a partir de `NSScreen.mainScreen`.
 */
actual fun defaultPlatformContext(): AppDimensContext? {
    val screen = NSScreen.mainScreen ?: return null
    val frame = screen.frame
    val widthPt = CGRectGetWidth(frame)
    val heightPt = CGRectGetHeight(frame)
    if (widthPt <= 0f || heightPt <= 0f) return null
    val scale = screen.backingScaleFactor.toFloat().takeIf { it > 0f } ?: 1f
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
    return MacosAppDimensContext(screenConfig, scale, scale * 160f)
}