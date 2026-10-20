package com.appdimens.kmp.custom

import com.appdimens.kmp.core.DimenCache
import com.appdimens.kmp.core.DimenMetrics
import com.appdimens.kmp.core.ScreenConfiguration
import com.appdimens.kmp.core.from
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Custom configs that mirror a built-in module must reproduce the factor memoized on
 * the core [DimenMetrics] snapshot (bridge: [toScreenSnapshot]).
 */
class CustomParityWithCoreTest {

    private fun metrics(sw: Int, w: Int, h: Int, uiMode: Int = 0x11, orientation: Int = 1): DimenMetrics =
        DimenMetrics.from(ScreenConfiguration(w, h, sw, 420, 1f, orientation, uiMode))

    @Test
    fun builtIns_matchCoreSnapshotFactors() {
        for (m in listOf(metrics(360, 360, 780), metrics(411, 411, 891), metrics(800, 1280, 800, orientation = 2), metrics(240, 240, 320))) {
            assertEquals(m.scale, BuiltInStrategies.SCALED.resolve(m).factor, 1e-6f)
            assertEquals(m.powerScale, BuiltInStrategies.POWER.resolve(m).factor, 1e-6f)
            assertEquals(m.logarithmicScale, BuiltInStrategies.LOGARITHMIC.resolve(m).factor, 1e-5f)
            assertEquals(m.interpolatedScale, BuiltInStrategies.INTERPOLATED.resolve(m).factor, 1e-6f)
            assertEquals(m.diagonalScale, BuiltInStrategies.DIAGONAL.resolve(m).factor, 1e-5f)
            assertEquals(m.perimeterScale, BuiltInStrategies.PERIMETER.resolve(m).factor, 1e-6f)
        }
    }

    @Test
    fun aspectRatio_matchesCoreMultiplier() {
        val m = metrics(411, 411, 891)
        val s = scalingStrategy("arParity", register = false) { curve = power(0.75f); aspectRatio() }
        assertEquals(m.powerScale * m.defaultAspectRatioMultiplier, s.resolve(m).factor, 1e-6f)
    }

    @Test
    fun snapshotBridge_mapsDeviceAndOrientation() {
        val tv = metrics(540, 960, 540, uiMode = 0x14, orientation = 2).toScreenSnapshot()
        assertEquals(DeviceClass.TV, tv.device)
        assertTrue(tv.isLandscape)
        // Desktop targets report generic windows as DESKTOP; Android / iOS / web classify by size.
        assertEquals(platformDeviceClass(0x11, 800f), metrics(800, 800, 1280).toScreenSnapshot().device)
    }

    @Test
    fun cacheKey_flagsOnlyValueDependentStrategies() {
        val plain = scalingStrategy("plainKey", register = false) {}
        val clamped = scalingStrategy("clampedKey", register = false) { clampValue(max = 24) }
        assertEquals(0L, plain.cacheKey(16f, DimenCache.ValueType.DP) and 0x4L)
        assertEquals(0x4L, clamped.cacheKey(16f, DimenCache.ValueType.DP) and 0x4L)
    }
}
