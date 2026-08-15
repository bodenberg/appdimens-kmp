package com.appdimens.dynamic.code.resize

import com.appdimens.dynamic.common.UiModeType
import com.appdimens.dynamic.core.AppDimensContext
import com.appdimens.dynamic.core.AutoResizePercentBasis
import com.appdimens.dynamic.core.ScreenConfiguration
import kotlin.test.assertEquals
import kotlin.test.Test

/** KMP port of the Android `DimenResizeCodeUnitTest` (no Android / mockito dependencies). */
class DimenResizeCodeUnitTest {

    /** Minimal test double for [AppDimensContext] (density + fontScale only). */
    private class FakeAppDimensContext(
        override val density: Float,
        fontScale: Float = 1f,
    ) : AppDimensContext {
        private val cfg = ScreenConfiguration(
            screenWidthDp = 400,
            screenHeightDp = 800,
            smallestScreenWidthDp = 400,
            densityDpi = (density * 160f).toInt(),
            fontScale = fontScale,
            orientation = ScreenConfiguration.ORIENTATION_PORTRAIT,
            uiMode = 1,
        )

        override val configuration: ScreenConfiguration get() = cfg
        override val xdpi: Float get() = density * 160f
        override val isInMultiWindowMode: Boolean get() = false
        override val uiModeType: UiModeType get() = UiModeType.UNDEFINED
    }

    @Test
    fun innerMaxDimensions_subtractsPadding() {
        val (w, h) = DimenResize.innerMaxDimensionsPx(200f, 100f, 10f, 10f, 20f, 20f)
        assertEquals(180f, w, 0.01f)
        assertEquals(60f, h, 0.01f)
    }

    @Test
    fun innerMaxDimensionsRelative_rtlSwapsStartEnd() {
        // layoutDirection: 0 = LTR, 1 = RTL (LayoutDirection.LTR / RTL).
        val (wLtr, _) = DimenResize.innerMaxDimensionsPxRelative(
            100f, 50f,
            paddingStartPx = 30f,
            paddingEndPx = 10f,
            layoutDirection = 0,
        )
        val (wLtrDirect, _) = DimenResize.innerMaxDimensionsPx(100f, 50f, 30f, 10f, 0f, 0f)
        assertEquals(wLtrDirect, wLtr, 0.01f)

        val (wRtl, _) = DimenResize.innerMaxDimensionsPxRelative(
            100f, 50f,
            paddingStartPx = 30f,
            paddingEndPx = 10f,
            layoutDirection = 1,
        )
        val (wRtlExpected, _) = DimenResize.innerMaxDimensionsPx(100f, 50f, 10f, 30f, 0f, 0f)
        assertEquals(wRtlExpected, wRtl, 0.01f)
    }

    @Test
    fun innerMaxDimensions_coercesAtLeastOnePx() {
        val (w, h) = DimenResize.innerMaxDimensionsPx(10f, 8f, 20f, 20f, 0f, 0f)
        assertEquals(1f, w, 0.01f)
        assertEquals(8f, h, 0.01f)
    }

    @Test
    fun percentOfBoxToFactor_coercesToZeroOne() {
        assertEquals(0f, percentOfBoxToFactor(-10), 0f)
        assertEquals(1f, percentOfBoxToFactor(200), 0f)
        assertEquals(0.25f, percentOfBoxToFactor(25), 0f)
    }

    @Test
    fun rangePxPercentOfInnerBox_matchesWidthBasis() {
        val ctx = FakeAppDimensContext(density = 2f)
        val innerW = 400f
        val innerH = 200f
        val r = DimenResize.rangePxPercentOfInnerBox(
            context = ctx,
            basis = AutoResizePercentBasis.WIDTH,
            minPercent = 10,
            maxPercent = 50,
            stepDp = 2f,
            innerWidthPx = innerW,
            innerHeightPx = innerH,
        )
        assertEquals(40f, r.minPx, 0.01f)
        assertEquals(200f, r.maxPx, 0.01f)
        assertEquals(4f, r.stepPx, 0.01f)
    }

    @Test
    fun rangePxTextSizePercent_usesFontScale() {
        val ctx = FakeAppDimensContext(density = 2f, fontScale = 1.25f)
        val r = DimenResize.rangePxTextSizePercentOfInnerBox(
            context = ctx,
            basis = AutoResizePercentBasis.HEIGHT,
            minPercent = 100,
            maxPercent = 100,
            stepSp = 2f,
            innerWidthPx = 100f,
            innerHeightPx = 300f,
        )
        assertEquals(300f, r.minPx, 0.01f)
        assertEquals(300f, r.maxPx, 0.01f)
        assertEquals(5f, r.stepPx, 0.01f)
    }
}
