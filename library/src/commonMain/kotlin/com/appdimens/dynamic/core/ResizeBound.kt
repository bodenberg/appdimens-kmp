@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

/**
 * EN Bounds for auto-resize (TextView auto-size–style): fixed dp/sp or literal % of screen axis.
 * PT Limites para auto-resize: dp/sp fixos ou % literal de um eixo da tela.
 */
package com.appdimens.dynamic.core

import com.appdimens.dynamic.core.AppDimensContext
import com.appdimens.dynamic.core.ScreenConfiguration
import com.appdimens.dynamic.core.currentScreenConfiguration
import com.appdimens.dynamic.core.localAppDimensContext

import com.appdimens.dynamic.common.DpQualifier

/**
 * EN Screen metric used to resolve [Percent] bounds (matches sdp / wdp / hdp axes).
 */
typealias ResizeAxisQualifier = DpQualifier

/**
 * EN One end of a resize range or the step granularity.
 *
 * - [FixedDp]: logical dp already chosen by the caller (e.g. `16.sdp` → pass `16f` or `dp.value`).
 * - [FixedSp]: sp value; converts to px with [fontScale] (like `COMPLEX_UNIT_SP`).
 * - [Percent]: `value` is 0–100 of [axis] using [ScreenConfiguration] (same idea as `spaceW` / `spaceSw` / `spaceH`).
 */
sealed class ResizeBound {
    data class FixedDp(val dp: Float) : ResizeBound()
    data class FixedSp(val sp: Float) : ResizeBound()
    data class Percent(val value: Float, val axis: ResizeAxisQualifier) : ResizeBound()
}

fun resizeFixedDp(dp: Float): ResizeBound = ResizeBound.FixedDp(dp)
fun resizeFixedSp(sp: Float): ResizeBound = ResizeBound.FixedSp(sp)

/** EN % of [ScreenConfiguration.smallestScreenWidthDp]. PT % do menor lado (sw). */
fun resizePercentSw(percent: Float): ResizeBound =
    ResizeBound.Percent(percent, DpQualifier.SMALL_WIDTH)

/** EN % of [ScreenConfiguration.screenWidthDp]. */
fun resizePercentW(percent: Float): ResizeBound =
    ResizeBound.Percent(percent, DpQualifier.WIDTH)

/** EN % of [ScreenConfiguration.screenHeightDp]. */
fun resizePercentH(percent: Float): ResizeBound =
    ResizeBound.Percent(percent, DpQualifier.HEIGHT)

/**
 * EN Converts [bound] to **px** for layout/measure (density + font scale for sp).
 */
fun ResizeBound.resolveToPx(
    configuration: ScreenConfiguration,
    density: Float,
    fontScale: Float,
): Float {
    require(density > 0f) { "density must be positive, was $density" }
    val fs = if (fontScale > 0f) fontScale else 1f
    return when (this) {
        is ResizeBound.FixedDp -> dp.coerceAtLeast(0f) * density
        is ResizeBound.FixedSp -> sp.coerceAtLeast(0f) * density * fs
        is ResizeBound.Percent -> {
            val axisDp = DimenCalculationPlumbing.readScreenDp(configuration, axis)
            (value.coerceIn(0f, 100f) / 100f) * axisDp * density
        }
    }
}
