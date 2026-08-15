package com.appdimens.kmp.code.interpolated

import com.appdimens.kmp.common.DpQualifier
import com.appdimens.kmp.common.Inverter
import com.appdimens.kmp.core.DimenCache
import com.appdimens.kmp.core.DimenMetrics
import com.appdimens.kmp.core.ScreenConfiguration
import com.appdimens.kmp.core.from
import kotlin.test.assertEquals
import kotlin.test.Test

/**
 * Formula checks for the interpolated satellite. The default-SW path must read the
 * memoized [DimenMetrics.interpolatedScale] snapshot factor.
 */
class InterpolatedFormulasTest {

    private fun config(sw: Int, w: Int = sw, h: Int = 800): ScreenConfiguration =
        ScreenConfiguration(w, h, sw, 420, 1f, ScreenConfiguration.ORIENTATION_PORTRAIT, 1)

    @Test
    fun interpolatedFactor_matchesMemoizedSnapshotFactor() {
        val cfg = config(420, 800, 1200)
        val metrics = DimenMetrics.from(cfg)
        DimenCache.invalidateOnConfigChange(cfg)

        val expectedScale = 1f + (420f * DimenCache.INV_BASE_RATIO - 1f) * 0.5f
        val out = calculateInterpolatedDp(
            50f, cfg, DpQualifier.SMALL_WIDTH, Inverter.DEFAULT,
            ignoreMultiWindows = false, applyAspectRatio = false, customSensitivityK = null
        )
        assertEquals(50f * expectedScale, out, 0.001f)
        assertEquals(metrics.interpolatedScale, expectedScale, 0f)
    }
}
