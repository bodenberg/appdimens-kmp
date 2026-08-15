package com.appdimens.kmp.compose.percent

import com.appdimens.kmp.common.DpQualifier
import com.appdimens.kmp.common.Inverter
import com.appdimens.kmp.core.DimenCache
import com.appdimens.kmp.core.ScreenConfiguration
import kotlin.test.assertEquals
import kotlin.test.Test

/** Isolated formula checks for the percent satellite. */
class PercentFormulasTest {

    private fun config(sw: Int, w: Int = sw, h: Int = 800): ScreenConfiguration =
        ScreenConfiguration(w, h, sw, 420, 1f, ScreenConfiguration.ORIENTATION_PORTRAIT, 1)

    @Test
    fun percent_smallWidth_noAr_multipliesByScreenOverBase() {
        val cfg = config(400)
        val out = calculatePercentDpCompose(
            100f, cfg, DpQualifier.SMALL_WIDTH, Inverter.DEFAULT,
            ignoreMultiWindows = false, applyAspectRatio = false, customSensitivityK = null
        )
        assertEquals(100f * 400f * DimenCache.INV_BASE_RATIO, out, 0.001f)
    }

    @Test
    fun percent_fractionalBaseValue_preserved() {
        val cfg = config(400)
        val out = calculatePercentDpCompose(
            15.5f, cfg, DpQualifier.SMALL_WIDTH, Inverter.DEFAULT,
            ignoreMultiWindows = false, applyAspectRatio = false, customSensitivityK = null
        )
        assertEquals(15.5f * 400f * DimenCache.INV_BASE_RATIO, out, 0.001f)
    }
}
