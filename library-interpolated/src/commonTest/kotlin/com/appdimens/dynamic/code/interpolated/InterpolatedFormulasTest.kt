package com.appdimens.dynamic.code.interpolated

import com.appdimens.dynamic.common.DpQualifier
import com.appdimens.dynamic.common.Inverter
import com.appdimens.dynamic.core.DimenCache
import com.appdimens.dynamic.core.DimenMetrics
import com.appdimens.dynamic.core.ScreenConfiguration
import com.appdimens.dynamic.core.from
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
