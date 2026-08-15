package com.appdimens.kmp.core

import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalAtomicApi::class)
class DimenCacheDiskClearOnInvalidateTest {

    @BeforeTest
    fun setup() {
        DimenCache.clearAll()
        DimenCache.isEnabled.store(true)
    }

    private fun config(sw: Int = 400, w: Int = 400, h: Int = 800, dpi: Int = 420): ScreenConfiguration =
        ScreenConfiguration(w, h, sw, dpi, 1f, ScreenConfiguration.ORIENTATION_PORTRAIT, 1)

    private fun mockAppContext(configuration: ScreenConfiguration): FakeAppDimensContext =
        FakeAppDimensContext(
            sw = configuration.smallestScreenWidthDp,
            w = configuration.screenWidthDp,
            h = configuration.screenHeightDp,
            dpi = configuration.densityDpi,
            fontScale = configuration.fontScale,
            orientation = configuration.orientation,
        )

    @Test
    fun clearAll_clearsSnapshotPartitions() {
        val app = mockAppContext(config())
        DimenCache.init(app)
        val key = DimenCache.buildKey(
            42f, false, false, DimenCache.CalcType.FLUID,
            com.appdimens.kmp.common.DpQualifier.SMALL_WIDTH,
            com.appdimens.kmp.common.Inverter.DEFAULT, true, DimenCache.ValueType.DP
        )
        DimenCache.getOrPut(key, app) { 99f }
        DimenCache.clearAll(app)
        assertNull(DimenCache.peek(key, app))
    }

    @Test
    fun invalidate_doesNotClearSnapshotPartitions() {
        val app = mockAppContext(config())
        DimenCache.init(app)
        val key = DimenCache.buildKey(
            42f, false, false, DimenCache.CalcType.FLUID,
            com.appdimens.kmp.common.DpQualifier.SMALL_WIDTH,
            com.appdimens.kmp.common.Inverter.DEFAULT, true, DimenCache.ValueType.DP
        )
        DimenCache.getOrPut(key, app) { 99f }
        DimenCache.invalidateOnConfigChange(config(sw = 300, w = 300, h = 800))
        assertEquals(
            99f,
            DimenCache.peek(key, app)!!,
            "snapshot partitions must survive invalidate (compat hook only)"
        )
    }

    @Test
    fun invalidateBeforeInit_doesNotThrow() {
        DimenCache.invalidateOnConfigChange(config(sw = 500, w = 500, h = 900))
    }
}