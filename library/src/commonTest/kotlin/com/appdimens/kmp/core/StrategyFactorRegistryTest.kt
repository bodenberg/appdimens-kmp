package com.appdimens.kmp.core

import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndIncrement
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalAtomicApi::class)
class StrategyFactorRegistryTest {

    @BeforeTest
    fun setUp() {
        StrategyFactorRegistry.resetForTest()
    }

    @Test
    fun register_replaysLastMetrics() {
        val cfg = ScreenConfiguration(400, 800, 400, 420, 1f, ScreenConfiguration.ORIENTATION_PORTRAIT, 1)
        StrategyFactorRegistry.publish(sharedMetricsFrom(cfg))

        var seenSw = -1f
        StrategyFactorRegistry.register { m -> seenSw = m.smallestWidthDp }
        assertEquals(400f, seenSw, 0.01f)
    }

    @Test
    fun publish_notifiesOnlyRegisteredContributors() {
        val hits = AtomicInt(0)
        StrategyFactorRegistry.register { hits.fetchAndIncrement() }
        val cfg = ScreenConfiguration(360, 640, 360, 420, 1f, ScreenConfiguration.ORIENTATION_PORTRAIT, 1)
        StrategyFactorRegistry.publish(sharedMetricsFrom(cfg))
        assertEquals(1, hits.load())
        assertEquals(360f, StrategyFactorRegistry.lastMetricsForTest()!!.smallestWidthDp, 0.01f)
    }

    @Test
    fun reset_clearsState() {
        val cfg = ScreenConfiguration(300, 600, 300, 420, 1f, ScreenConfiguration.ORIENTATION_PORTRAIT, 1)
        StrategyFactorRegistry.publish(sharedMetricsFrom(cfg))
        StrategyFactorRegistry.resetForTest()
        assertNull(StrategyFactorRegistry.lastMetricsForTest())
    }

    @Test
    fun sharedMetrics_matchesCoreScaleFormula() {
        val cfg = ScreenConfiguration(450, 900, 450, 420, 1f, ScreenConfiguration.ORIENTATION_PORTRAIT, 1)
        val m = sharedMetricsFrom(cfg)
        assertEquals(450f * DimenCache.INV_BASE_RATIO, m.scale, 0.0001f)
        assertTrue(m.arMultiplier > 0f)
        assertTrue(m.aspectRatioMul > 0f)
    }
}
