package com.appdimens.kmp.compose.diagonal

import com.appdimens.kmp.common.DpQualifier
import com.appdimens.kmp.common.Inverter
import com.appdimens.kmp.core.DesignScaleConstants
import com.appdimens.kmp.core.DimenCache
import com.appdimens.kmp.core.ScreenConfiguration
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.math.sqrt

/** Isolated formula checks for the diagonal satellite. */
class DiagonalFormulasTest {

    private fun config(sw: Int, w: Int = sw, h: Int = 800): ScreenConfiguration =
        ScreenConfiguration(w, h, sw, 420, 1f, ScreenConfiguration.ORIENTATION_PORTRAIT, 1)

    @Test
    fun diagonal_usesDesignDiagonalConstant() {
        val cfg = config(300, w = 400, h = 300)
        DimenCache.invalidateOnConfigChange(cfg)
        val sm = 300f
        val lg = 400f
        val diag = sqrt((sm * sm + lg * lg).toDouble()).toFloat()
        val expected = 50f * (diag / DesignScaleConstants.BASE_DIAGONAL_DP)
        val out = calculateDiagonalDpCompose(
            50f, cfg, DpQualifier.SMALL_WIDTH, Inverter.DEFAULT,
            ignoreMultiWindows = false, applyAspectRatio = false, customSensitivityK = null
        )
        assertEquals(expected, out, 0.05f)
    }
}
