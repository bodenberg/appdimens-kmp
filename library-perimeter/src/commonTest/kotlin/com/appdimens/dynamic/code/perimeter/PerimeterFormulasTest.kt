package com.appdimens.dynamic.code.perimeter

import com.appdimens.dynamic.common.DpQualifier
import com.appdimens.dynamic.common.Inverter
import com.appdimens.dynamic.core.DimenCache
import com.appdimens.dynamic.core.DimenMetrics
import com.appdimens.dynamic.core.ScreenConfiguration
import com.appdimens.dynamic.core.from
import kotlin.test.assertEquals
import kotlin.test.Test

/**
 * Formula checks for the perimeter satellite. The default-SW path must read the
 * memoized [DimenMetrics.perimeterScale] snapshot factor.
 */
class PerimeterFormulasTest {

    private fun config(sw: Int, w: Int = sw, h: Int = 800): ScreenConfiguration =
        ScreenConfiguration(w, h, sw, 420, 1f, ScreenConfiguration.ORIENTATION_PORTRAIT, 1)

    @Test
    fun perimeterFactor_matchesMemoizedSnapshotFactor() {
        val cfg = config(360, 800, 1200)
        val metrics = DimenMetrics.from(cfg)
        DimenCache.invalidateOnConfigChange(cfg)

        val expectedScale = (800f + 1200f) / 833f
        val out = calculatePerimeterDp(
            50f, cfg, DpQualifier.SMALL_WIDTH, Inverter.DEFAULT,
            ignoreMultiWindows = false, applyAspectRatio = false, customSensitivityK = null
        )
        assertEquals(50f * expectedScale, out, 0.001f)
        assertEquals(metrics.perimeterScale, expectedScale, 0f)
    }
}
