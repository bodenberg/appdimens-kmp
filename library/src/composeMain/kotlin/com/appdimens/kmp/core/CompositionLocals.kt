/**
 * Author & Developer: Jean Bodenberg
 * GIT: https://github.com/bodenberg/appdimens-sdps.git
 * Date: 2025-10-04 | KMP port: 2026-08
 *
 * Library: AppDimens — CompositionLocals (KMP common)
 */
package com.appdimens.kmp.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.appdimens.kmp.common.UiModeType

/**
 * EN CompositionLocal for the current UiModeType.
 * PT CompositionLocal para o UiModeType atual.
 */
val LocalUiModeType = compositionLocalOf { UiModeType.UNDEFINED }

/**
 * Per-composition window snapshot. Consumers may use it to avoid repeatedly reading the
 * platform configuration state and to keep every dimension in a composition on the same
 * coherent window snapshot.
 */
val LocalDimenMetrics = compositionLocalOf<DimenMetrics?> { null }

/**
 * EN CompositionLocal for the current platform window handle ([AppDimensContext]).
 * Provided by [AppDimensProvider]; falls back to a platform default per composition.
 *
 * PT CompositionLocal para o handle de janela atual ([AppDimensContext]).
 */
val LocalAppDimensContext = compositionLocalOf<AppDimensContext?> { null }

/**
 * EN Provider that automatically computes and provides the [UiModeType] (including
 * foldables) and the coherent [DimenMetrics] snapshot to all child components.
 * **Recommended for performance:** without it, [getCurrentUiModeType] falls back to
 * resolving the UI mode on every `*Mode` / `*Screen` facilitator call.
 *
 * PT Provedor que computa e fornece o [UiModeType] (incl. dobráveis) e o snapshot
 * coerente de [DimenMetrics]. **Recomendado para desempenho.**
 */
@Composable
expect fun AppDimensProvider(content: @Composable () -> Unit)

/**
 * EN Current [ScreenConfiguration] snapshot for this composition — platform actuals:
 * Android reads `LocalConfiguration`; desktop derives it from the window;
 * iOS derives it from `UIScreen`.
 *
 * PT Snapshot atual de [ScreenConfiguration] para esta composição.
 */
@Composable
expect fun currentScreenConfiguration(): ScreenConfiguration

/**
 * EN Platform window handle for this composition: the value provided by
 * [AppDimensProvider], or a platform default (Android: the local Context wrapped as
 * [AppDimensContext]; desktop/iOS: a screen-derived singleton).
 *
 * PT Handle de janela para esta composição.
 */
@Composable
fun localAppDimensContext(): AppDimensContext? =
    LocalAppDimensContext.current ?: rememberPlatformContext()

/**
 * EN Platform default window handle for compositions outside [AppDimensProvider].
 * PT Handle de janela padrão para composições fora do [AppDimensProvider].
 */
@Composable
expect fun rememberPlatformContext(): AppDimensContext?

/**
 * EN Internal helper to get the UiModeType, falling back to computing it if not provided.
 * PT Auxiliar interno para obter o UiModeType, recalculando se não for fornecido.
 */
@Composable
fun getCurrentUiModeType(): UiModeType {
    val provided = LocalUiModeType.current
    if (provided != UiModeType.UNDEFINED) return provided
    val context = localAppDimensContext()
    val configuration = currentScreenConfiguration()
    // Track only fields that affect UiMode — same fingerprint idea as DimenCache.
    return remember(
        configuration.uiMode,
        configuration.smallestScreenWidthDp,
        configuration.screenWidthDp,
        configuration.screenHeightDp,
        configuration.densityDpi,
    ) {
        context?.uiModeType ?: UiModeType.UNDEFINED
    }
}