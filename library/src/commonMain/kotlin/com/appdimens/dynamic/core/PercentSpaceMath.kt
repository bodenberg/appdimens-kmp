/**
 * Literal percentage of screen or reference length (e.g. 10 → 10%).
 * Used by percent `space*` APIs; separate from [calculatePercentDp] sdp-style scaling.
 *
 * KMP: operates on [ScreenConfiguration] snapshots.
 */
package com.appdimens.dynamic.core

import com.appdimens.dynamic.common.DpQualifier

fun literalPercentOfScreenDp(
    percent: Float,
    qualifier: DpQualifier,
    configuration: ScreenConfiguration,
    ignoreMultiWindows: Boolean,
): Float {
    if (!percent.isFinite()) return 0f
    if (ignoreMultiWindows && DimenCalculationPlumbing.isMultiWindowConstrained(configuration, true, null)) return percent
    val dim = DimenCalculationPlumbing.readScreenDp(configuration, qualifier)
    return (percent / 100f) * dim
}

fun literalPercentOfReferenceDp(
    percent: Float,
    referenceDp: Float,
    configuration: ScreenConfiguration,
    ignoreMultiWindows: Boolean,
): Float {
    if (!percent.isFinite()) return 0f
    return (percent / 100f) * referenceDp
}