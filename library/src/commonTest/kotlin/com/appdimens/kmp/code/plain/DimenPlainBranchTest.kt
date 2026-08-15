package com.appdimens.kmp.code.plain

import com.appdimens.kmp.common.DpQualifier
import com.appdimens.kmp.common.Orientation
import com.appdimens.kmp.core.FakeAppDimensContext
import com.appdimens.kmp.core.ScreenConfiguration
import kotlin.test.assertEquals
import kotlin.test.Test

class DimenPlainBranchTest {

    @Test
    fun plainRotatePx_landscapePicksBranch() {
        val ctx = contextWithOrientation(ScreenConfiguration.ORIENTATION_LANDSCAPE)
        assertEquals(99f, plainRotatePx(ctx, 10f, 99f, Orientation.LANDSCAPE), 0f)
    }

    @Test
    fun plainRotatePx_landscapeKeepsReceiverInPortrait() {
        val ctx = contextWithOrientation(ScreenConfiguration.ORIENTATION_PORTRAIT)
        assertEquals(10f, plainRotatePx(ctx, 10f, 99f, Orientation.LANDSCAPE), 0f)
    }

    @Test
    fun plainQualifierPx_matchesWhenThresholdMet() {
        val cfg = ScreenConfiguration(600, 800, 600, 420, 1f, ScreenConfiguration.ORIENTATION_PORTRAIT, 1)
        val ctx = mockContext(cfg)
        assertEquals(7f, plainQualifierPx(ctx, 3f, 7f, DpQualifier.SMALL_WIDTH, 400), 0f)
    }

    @Test
    fun plainQualifierPx_keepsReceiverWhenBelowThreshold() {
        val cfg = ScreenConfiguration(320, 480, 320, 420, 1f, ScreenConfiguration.ORIENTATION_PORTRAIT, 1)
        val ctx = mockContext(cfg)
        assertEquals(3f, plainQualifierPx(ctx, 3f, 7f, DpQualifier.SMALL_WIDTH, 400), 0f)
    }

    private fun contextWithOrientation(orientation: Int): FakeAppDimensContext =
        FakeAppDimensContext(orientation = orientation)

    private fun mockContext(configuration: ScreenConfiguration): FakeAppDimensContext =
        FakeAppDimensContext(
            sw = configuration.smallestScreenWidthDp,
            w = configuration.screenWidthDp,
            h = configuration.screenHeightDp,
            dpi = configuration.densityDpi,
            fontScale = configuration.fontScale,
            orientation = configuration.orientation,
        )
}
