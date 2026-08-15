/**
 * Strategy-agnostic screen plumbing: inverter resolution, multi-window detection, dp reads.
 * Each strategy module applies its own formula on top of this.
 *
 * KMP: platform-neutral — operates on [ScreenConfiguration] snapshots; multi-window
 * detection is delegated to [AppDimensContext] (Android resolves the hosting Activity).
 */
package com.appdimens.kmp.core

import com.appdimens.kmp.common.DpQualifier
import com.appdimens.kmp.common.Inverter
import kotlin.math.ln

object DimenCalculationPlumbing {

    fun effectiveQualifier(
        qualifier: DpQualifier,
        inverter: Inverter,
        isLandscape: Boolean,
        isPortrait: Boolean,
    ): DpQualifier {
        var actual = qualifier
        when (inverter) {
            Inverter.PH_TO_LW -> if (isLandscape && qualifier == DpQualifier.HEIGHT) actual = DpQualifier.WIDTH
            Inverter.PW_TO_LH -> if (isLandscape && qualifier == DpQualifier.WIDTH) actual = DpQualifier.HEIGHT
            Inverter.LH_TO_PW -> if (isPortrait && qualifier == DpQualifier.HEIGHT) actual = DpQualifier.WIDTH
            Inverter.LW_TO_PH -> if (isPortrait && qualifier == DpQualifier.WIDTH) actual = DpQualifier.HEIGHT
            Inverter.SW_TO_LH -> if (isLandscape && qualifier == DpQualifier.SMALL_WIDTH) actual = DpQualifier.HEIGHT
            Inverter.SW_TO_LW -> if (isLandscape && qualifier == DpQualifier.SMALL_WIDTH) actual = DpQualifier.WIDTH
            Inverter.SW_TO_PH -> if (isPortrait && qualifier == DpQualifier.SMALL_WIDTH) actual = DpQualifier.HEIGHT
            Inverter.SW_TO_PW -> if (isPortrait && qualifier == DpQualifier.SMALL_WIDTH) actual = DpQualifier.WIDTH
            Inverter.DEFAULT -> Unit
        }
        return actual
    }

    /**
     * Returns `true` when the app is in a multi-window mode (split-screen, freeform, PiP)
     * **and** the caller opted into suppressing scaling via [ignoreMultiWindows].
     *
     * Primary detection is delegated to [AppDimensContext.isInMultiWindowMode]
     * (on Android: `Activity.isInMultiWindowMode`, available since API 24, which matches
     * the library's minSdk). When no context is supplied, a heuristic based on
     * [ScreenConfiguration] dimensions is used as a best-effort fallback.
     */
    fun isMultiWindowConstrained(
        configuration: ScreenConfiguration,
        ignoreMultiWindows: Boolean,
        context: AppDimensContext? = null,
    ): Boolean {
        if (!ignoreMultiWindows) return false
        val multiWindow = context?.isInMultiWindowMode
        if (multiWindow != null) return multiWindow
        val swDp = configuration.smallestScreenWidthDp.toFloat()
        if (swDp <= 0f) return false
        val cwDp = configuration.screenWidthDp.toFloat()
        return (swDp - cwDp) >= (swDp * 0.1f)
    }

    /** Returns the real window mode when the context knows it, without retaining it. */
    fun isInMultiWindowMode(context: AppDimensContext?): Boolean =
        context?.isInMultiWindowMode == true

    /**
     * Kept as a source-compatible test hook. There is no longer a Context→Activity cache:
     * a weak key paired with the same Activity as value would retain the key indirectly.
     */
    internal fun clearActivityCacheForTest() = Unit

    fun readScreenDp(configuration: ScreenConfiguration, actualQualifier: DpQualifier): Float =
        when (actualQualifier) {
            DpQualifier.HEIGHT -> configuration.screenHeightDp.toFloat()
            DpQualifier.WIDTH -> configuration.screenWidthDp.toFloat()
            DpQualifier.SMALL_WIDTH -> configuration.smallestScreenWidthDp.toFloat()
        }

    fun smallestSideDp(configuration: ScreenConfiguration): Float =
        minOf(configuration.screenWidthDp.toFloat(), configuration.screenHeightDp.toFloat())

    fun largestSideDp(configuration: ScreenConfiguration): Float =
        maxOf(configuration.screenWidthDp.toFloat(), configuration.screenHeightDp.toFloat())

    /**
     * Multiplicative factor for optional aspect-ratio correction (perceptual / power-style paths).
     */
    fun aspectRatioMultiplier(configuration: ScreenConfiguration, sensitivity: Float): Float {
        val sm = smallestSideDp(configuration)
        val lg = largestSideDp(configuration)
        if (sm <= 0f) return 1f
        val ar = lg / sm
        if (!ar.isFinite()) return 1f
        return 1f + sensitivity * ln(ar * DesignScaleConstants.INV_REFERENCE_ASPECT_RATIO)
    }
}