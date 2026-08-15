/**
 * Web (wasmJs) Compose actuals: [AppDimensProvider], [currentScreenConfiguration],
 * [rememberPlatformContext].
 */
package com.appdimens.dynamic.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.appdimens.dynamic.common.UiModeType

/**
 * EN Builds the [ScreenConfiguration] for a window from its compose [WindowInfo].
 * PT Constrói o [ScreenConfiguration] de uma janela a partir do [WindowInfo].
 */
internal fun windowInfoToScreenConfiguration(windowInfo: androidx.compose.ui.platform.WindowInfo): ScreenConfiguration {
    val wPx = windowInfo.containerSize.width
    val hPx = windowInfo.containerSize.height
    val wDp = windowInfo.containerDpSize.width.value
    val hDp = windowInfo.containerDpSize.height.value
    val density = if (wDp > 0f) wPx / wDp else 1f
    val widthDp = wDp.toInt()
    val heightDp = hDp.toInt()
    return ScreenConfiguration(
        screenWidthDp = widthDp,
        screenHeightDp = heightDp,
        smallestScreenWidthDp = minOf(widthDp, heightDp),
        densityDpi = (density * 160f).toInt(),
        fontScale = 1f,
        orientation = if (widthDp >= heightDp) ScreenConfiguration.ORIENTATION_LANDSCAPE else ScreenConfiguration.ORIENTATION_PORTRAIT,
        uiMode = 1,
    )
}

/**
 * EN Convenience: reads [LocalWindowInfo] and derives the window snapshot with a
 * `remember` keyed on the exact fields that affect it.
 * PT Conveniência: lê [LocalWindowInfo] e deriva o snapshot da janela.
 */
@Composable
internal fun rememberWindowScreenConfiguration(): ScreenConfiguration {
    val windowInfo = androidx.compose.ui.platform.LocalWindowInfo.current
    return remember(
        windowInfo.containerSize.width,
        windowInfo.containerSize.height,
        windowInfo.containerDpSize.width.value,
        windowInfo.containerDpSize.height.value,
    ) {
        windowInfoToScreenConfiguration(windowInfo)
    }
}

/**
 * EN Provider that computes and provides the [UiModeType], the coherent
 * [DimenMetrics] snapshot and the [AppDimensContext] window handle from the
 * browser viewport.
 *
 * PT Provedor que computa e fornece o [UiModeType], o snapshot coerente de
 * [DimenMetrics] e o handle de janela [AppDimensContext].
 */
/**
 * EN Builds the [AppDimensContext] from a live window [ScreenConfiguration].
 *    The provider keys it with `remember(configuration)`: on a browser resize the
 *    configuration changes → a NEW context identity → the [DimenCache]
 *    fast-window-slot identity check misses → metrics rebuild with fresh values.
 *    Mirrors Android's self-healing fast lane (live `resources.configuration`).
 * PT Constrói o [AppDimensContext] a partir de um [ScreenConfiguration] vivo.
 *    O provider usa `remember(configuration)`: ao redimensionar o navegador a
 *    configuração muda → nova identidade de contexto → o fast-window-slot do
 *    [DimenCache] erra → métricas são reconstruídas. Espelha o fast lane
 *    auto-curável do Android.
 */
internal fun contextFromConfiguration(configuration: ScreenConfiguration): AppDimensContext {
    val density = if (configuration.densityDpi > 0) configuration.densityDpi / 160f else 1f
    return WebAppDimensContext(configuration, density, configuration.densityDpi.toFloat())
}

@Composable
actual fun AppDimensProvider(content: @Composable () -> Unit) {
    val configuration = rememberWindowScreenConfiguration()
    val metrics = remember(configuration) {
        DimenMetrics.from(configuration, isInMultiWindowMode = false)
    }
    // EN Live-config context: a resize produces a new identity, so the code-API
    //    fast lane self-heals exactly like Android. PT Contexto vivo: um resize
    //    produz nova identidade, então o fast lane da code API se auto-cura.
    val appContext = remember(configuration) { contextFromConfiguration(configuration) }

    CompositionLocalProvider(
        LocalUiModeType provides UiModeType.NORMAL,
        LocalDimenMetrics provides metrics,
        LocalAppDimensContext provides appContext,
    ) {
        content()
    }
}

/** EN Current [ScreenConfiguration] for this composition. */
@Composable
actual fun currentScreenConfiguration(): ScreenConfiguration = rememberWindowScreenConfiguration()

/**
 * EN Default window handle for compositions outside [AppDimensProvider]: derived
 *    from the live [LocalWindowInfo] viewport (not a frozen snapshot), so the
 *    code-API fast lane stays resize-aware even without the provider. Falls back
 *    to the remembered viewport handle when no window info is available.
 * PT Handle padrão para composições fora do [AppDimensProvider]: derivado do
 *    viewport vivo ([LocalWindowInfo]), para o fast lane da code API continuar
 *    ciente de resize mesmo sem provider.
 */
@Composable
actual fun rememberPlatformContext(): AppDimensContext? {
    val configuration = rememberWindowScreenConfiguration()
    val live = configuration.takeIf { it.screenWidthDp > 0 && it.screenHeightDp > 0 }
        ?.let { remember(it) { contextFromConfiguration(it) } }
    return live ?: remember { defaultPlatformContext() }
}