/**
 * Android Compose actuals: [AppDimensProvider], [currentScreenConfiguration],
 * [rememberPlatformContext].
 */
package com.appdimens.kmp.core

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import com.appdimens.kmp.common.UiModeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * EN Resolves the [WindowLayoutInfo] flow for [AppDimensProvider]. Always returns a
 * non-null [Flow] so [collectAsState] can be called unconditionally.
 *
 * PT Resolve o Flow de [WindowLayoutInfo]; sempre não-nulo para collectAsState incondicional.
 */
@PublishedApi
internal fun windowLayoutInfoFlowOrEmpty(activity: Activity?): Flow<WindowLayoutInfo> =
    activity?.let { WindowInfoTracker.getOrCreate(it).windowLayoutInfo(it) } ?: emptyFlow()

/**
 * EN Provider that automatically computes and provides the [UiModeType] (including
 * foldables), the coherent [DimenMetrics] snapshot and the [AppDimensContext] window
 * handle to all child components. **Recommended for performance.**
 *
 * PT Provedor que computa e fornece o [UiModeType] (incl. dobráveis), o snapshot
 * coerente de [DimenMetrics] e o handle de janela [AppDimensContext].
 */
@Composable
actual fun AppDimensProvider(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    // Memoize Activity lookup — Context→Activity chain is stable for a given Context.
    val activity = remember(context) { context.findActivity() }

    // Always collect — never call collectAsState behind a null-safe `?.` which would
    // skip the @Composable call when activity is null and resume it later (Compose
    // slot-table / inconsistent composition rule). emptyFlow() never emits, so
    // foldingFeature stays null — same observable behaviour as before.
    val flow = remember(activity) { windowLayoutInfoFlowOrEmpty(activity) }
    val windowLayoutInfo = flow.collectAsState(initial = null)

    val foldingFeature = windowLayoutInfo.value?.displayFeatures
        ?.filterIsInstance<FoldingFeature>()?.firstOrNull()

    // Cached per raw Context so the window-handle identity is stable across
    // recompositions; the folding feature is updated in place when it changes.
    val appContext = remember(context) { AndroidAppDimensContextCache.get(context) }
    remember(
        foldingFeature?.state,
        foldingFeature?.orientation,
        foldingFeature?.isSeparating,
    ) {
        appContext.updateFoldingFeature(foldingFeature)
        Unit
    }

    // Key on fold semantics, not the FoldingFeature instance — WindowLayoutInfo
    // often re-emits a new feature object with identical state/orientation.
    val uiModeType = remember(
        context,
        configuration.uiMode,
        configuration.smallestScreenWidthDp,
        configuration.screenWidthDp,
        configuration.screenHeightDp,
        configuration.densityDpi,
        foldingFeature?.state,
        foldingFeature?.orientation,
        foldingFeature?.isSeparating,
    ) {
        appContext.uiModeType
    }

    val metrics = remember(
        configuration.screenWidthDp,
        configuration.screenHeightDp,
        configuration.smallestScreenWidthDp,
        configuration.densityDpi,
        configuration.fontScale,
        configuration.orientation,
        configuration.uiMode,
        activity?.isInMultiWindowMode,
    ) {
        DimenMetrics.from(configuration, activity?.isInMultiWindowMode == true)
    }

    CompositionLocalProvider(
        LocalUiModeType provides uiModeType,
        LocalDimenMetrics provides metrics,
        LocalAppDimensContext provides appContext,
    ) {
        content()
    }
}

/**
 * EN Current [ScreenConfiguration] for this composition, from `LocalConfiguration`.
 *    Remembered on the exact fields that drive it, so facilitator extensions
 *    (`sdpRotate`/`sdpMode`/… and the non-fast-lane Compose paths) pay **zero
 *    allocation** in steady state instead of building a new [ScreenConfiguration]
 *    data class on every call.
 * PT Snapshot atual de [ScreenConfiguration], a partir de `LocalConfiguration`.
 *    Com `remember` nos campos exatos que o dirigem, as extensões facilitadoras
 *    (`sdpRotate`/`sdpMode`/… e os caminhos Compose fora do fast lane) pagam
 *    **zero alocação** em estado estável em vez de construir um
 *    [ScreenConfiguration] a cada chamada.
 */
@Composable
actual fun currentScreenConfiguration(): ScreenConfiguration {
    val configuration = LocalConfiguration.current
    return remember(
        configuration.screenWidthDp,
        configuration.screenHeightDp,
        configuration.smallestScreenWidthDp,
        configuration.densityDpi,
        configuration.fontScale,
        configuration.orientation,
        configuration.uiMode,
    ) {
        configuration.toScreenConfiguration()
    }
}

/**
 * EN Default window handle for compositions outside [AppDimensProvider] — the local
 * Context wrapped (and cached) as [AppDimensContext].
 * PT Handle padrão para composições fora do [AppDimensProvider].
 */
@Composable
actual fun rememberPlatformContext(): AppDimensContext? =
    AndroidAppDimensContextCache.get(LocalContext.current)