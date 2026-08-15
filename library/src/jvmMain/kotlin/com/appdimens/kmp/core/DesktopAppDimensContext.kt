/**
 * Desktop (JVM/AWT) platform window handle.
 */
package com.appdimens.kmp.core

import com.appdimens.kmp.common.UiModeType
import java.awt.GraphicsEnvironment
import java.awt.Toolkit

/**
 * EN JVM desktop implementation of [AppDimensContext], backed by the AWT screen.
 * PT Implementação desktop (JVM/AWT) de [AppDimensContext].
 */
internal class DesktopAppDimensContext(
    override val configuration: ScreenConfiguration,
    override val density: Float,
    override val xdpi: Float,
) : AppDimensContext {

    override val isInMultiWindowMode: Boolean get() = false

    override val uiModeType: UiModeType get() = UiModeType.NORMAL
}

/**
 * EN Builds the desktop default window handle from the primary AWT screen.
 * Returns `null` in headless environments.
 *
 * PT Constrói o handle padrão do desktop a partir da tela AWT primária.
 * Retorna `null` em ambientes headless.
 */
actual fun defaultPlatformContext(): AppDimensContext? {
    return try {
        val environment = GraphicsEnvironment.getLocalGraphicsEnvironment()
        if (GraphicsEnvironment.isHeadless()) return null
        val bounds = environment.maximumWindowBounds
        val dpi = Toolkit.getDefaultToolkit().screenResolution.takeIf { it > 0 } ?: 96
        val density = dpi / 160f
        val widthDp = (bounds.width / density).toInt()
        val heightDp = (bounds.height / density).toInt()
        val screen = ScreenConfiguration(
            screenWidthDp = widthDp,
            screenHeightDp = heightDp,
            smallestScreenWidthDp = minOf(widthDp, heightDp),
            densityDpi = dpi,
            fontScale = 1f,
            orientation = if (widthDp >= heightDp) ScreenConfiguration.ORIENTATION_LANDSCAPE else ScreenConfiguration.ORIENTATION_PORTRAIT,
            uiMode = 1,
        )
        DesktopAppDimensContext(screen, density, dpi.toFloat())
    } catch (_: Throwable) {
        null
    }
}