/**
 * Platform-neutral window handle used by every non-Compose (`code`) API.
 *
 * On Android it wraps the platform [Context] (see `androidMain`); on JVM desktop
 * it wraps the AWT screen / window; on iOS it wraps `UIScreen`. All resolution
 * logic lives in common code and only reads this interface — no platform API
 * leaks into the strategy modules.
 */
package com.appdimens.dynamic.core

import com.appdimens.dynamic.common.UiModeType

/**
 * EN A window/screen handle exposing everything the library needs to resolve a
 * dynamic dimension. Implementations are provided per platform:
 * - Android: wraps `android.content.Context` (`AndroidAppDimensContext`)
 * - JVM desktop: wraps the AWT screen / `java.awt.Window` (`DesktopAppDimensContext`)
 * - iOS: wraps `UIScreen.mainScreen` (`IosAppDimensContext`)
 *
 * PT Um handle de janela/tela expondo tudo que a biblioteca precisa para
 * resolver uma dimensão dinâmica. Implementações por plataforma.
 */
interface AppDimensContext {
    /** EN Current window snapshot. PT Snapshot atual da janela. */
    val configuration: ScreenConfiguration

    /** EN Logical display density (`densityDpi / 160f`). PT Densidade lógica do display. */
    val density: Float

    /** EN Physical horizontal dots-per-inch of the display (used by physical-unit conversions). */
    val xdpi: Float

    /** EN True when the window is in a multi-window / split-screen mode. */
    val isInMultiWindowMode: Boolean

    /** EN Resolved UI mode type (foldables detection lives in platform actuals). */
    val uiModeType: UiModeType

    /**
     * EN Registers a listener invoked synchronously whenever this window's
     * configuration changes (rotation, resize, density, font scale…).
     * Default is a no-op; Android overrides with `ComponentCallbacks2`.
     * PT Registra um listener chamado sincronamente em mudança de configuração.
     */
    fun registerConfigurationListener(listener: () -> Unit) = Unit

    /** EN Convenience: builds the coherent [DimenMetrics] snapshot for this window. */
    fun toMetrics(): DimenMetrics = DimenMetrics.from(configuration, isInMultiWindowMode)
}

/**
 * EN Default platform context used by Compose helpers when no [AppDimensProvider]
 * is present. Android returns `null` (a Context is always required); desktop/iOS
 * return a screen-derived singleton.
 */
expect fun defaultPlatformContext(): AppDimensContext?

/** EN Builds [DimenMetrics] straight from a [ScreenConfiguration]. */
fun DimenMetrics.Companion.from(
    screen: ScreenConfiguration,
    isInMultiWindowMode: Boolean = false,
): DimenMetrics = DimenMetrics(
    screenWidthDp = screen.screenWidthDp,
    screenHeightDp = screen.screenHeightDp,
    smallestScreenWidthDp = screen.smallestScreenWidthDp,
    densityDpi = screen.densityDpi,
    fontScaleBits = screen.fontScale.toRawBits(),
    orientation = screen.orientation,
    uiMode = screen.uiMode,
    isInMultiWindowMode = isInMultiWindowMode,
)