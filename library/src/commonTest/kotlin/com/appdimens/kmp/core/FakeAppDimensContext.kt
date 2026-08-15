package com.appdimens.kmp.core

import com.appdimens.kmp.common.UiModeType

/**
 * EN Test double for [AppDimensContext]: fully configurable screen metrics without
 * any platform dependency (JVM, Android host, wasmJs and native tests alike).
 * PT Dublê de teste para [AppDimensContext]: métricas de tela configuráveis sem
 * dependência de plataforma.
 */
class FakeAppDimensContext(
    var sw: Int = 400,
    var w: Int = 400,
    var h: Int = 800,
    var dpi: Int = 420,
    var fontScale: Float = 1f,
    var orientation: Int = ScreenConfiguration.ORIENTATION_PORTRAIT,
    var uiMode: Int = 1,
    var multiWindow: Boolean = false,
    var densityOverride: Float? = null,
    var xdpiOverride: Float? = null,
) : AppDimensContext {

    override val configuration: ScreenConfiguration
        get() = ScreenConfiguration(
            screenWidthDp = w,
            screenHeightDp = h,
            smallestScreenWidthDp = sw,
            densityDpi = dpi,
            fontScale = fontScale,
            orientation = orientation,
            uiMode = uiMode,
        )

    override val density: Float
        get() = densityOverride ?: dpi / 160f

    override val xdpi: Float
        get() = xdpiOverride ?: density * 160f

    override val isInMultiWindowMode: Boolean
        get() = multiWindow

    override val uiModeType: UiModeType
        get() = UiModeType.fromConfigurationValue(uiMode)

    override fun toString(): String =
        "FakeAppDimensContext(sw=$sw, w=$w, h=$h, dpi=$dpi, fs=$fontScale, o=$orientation)"
}