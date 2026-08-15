package com.appdimens.dynamic.code.logarithmic

import com.appdimens.dynamic.common.DpQualifier
import com.appdimens.dynamic.common.Inverter
import com.appdimens.dynamic.core.DimenCache
import com.appdimens.dynamic.core.DimenMetrics
import com.appdimens.dynamic.core.ScreenConfiguration
import com.appdimens.dynamic.core.from
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.math.ln

/**
 * Formula checks for the logarithmic satellite. The default-SW path must read the
 * memoized [DimenMetrics.logarithmicScale] — not recompute `ln` per call.
 */
class LogarithmicFormulasTest {

    private fun config(sw: Int, w: Int = sw, h: Int = 800): ScreenConfiguration =
        ScreenConfiguration(w, h, sw, 420, 1f, ScreenConfiguration.ORIENTATION_PORTRAIT, 1)

    @Test
    fun logarithmicFactor_matchesMemoizedSnapshotFactor() {
        val cfg = config(360, 800, 1200)
        val metrics = DimenMetrics.from(cfg)
        DimenCache.invalidateOnConfigChange(cfg)

        val expectedScale = 1f + 0.4f * ln(360f / 300f)
        val out = calculateLogarithmicDp(
            50f, cfg, DpQualifier.SMALL_WIDTH, Inverter.DEFAULT,
            ignoreMultiWindows = false, applyAspectRatio = false, customSensitivityK = null
        )
        assertEquals(50f * expectedScale, out, 0.001f)
        assertEquals(metrics.logarithmicScale, expectedScale, 0f, "snapshot memo must match the when-chain")
    }

    @Test
    fun logarithmic_belowBaseWidth_diminishes() {
        val cfg = config(240, 240, 800)
        val metrics = DimenMetrics.from(cfg)
        DimenCache.invalidateOnConfigChange(cfg)

        val expectedScale = 1f - 0.4f * ln(300f / 240f)
        val out = calculateLogarithmicDp(
            50f, cfg, DpQualifier.SMALL_WIDTH, Inverter.DEFAULT,
            ignoreMultiWindows = false, applyAspectRatio = false, customSensitivityK = null
        )
        assertEquals(50f * expectedScale, out, 0.001f)
        assertEquals(metrics.logarithmicScale, expectedScale, 0f)
    }
}
