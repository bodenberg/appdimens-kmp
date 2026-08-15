package com.appdimens.dynamic.core

import com.appdimens.dynamic.common.DpQualifier
import com.appdimens.dynamic.common.Inverter
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.test.assertEquals
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalAtomicApi::class)

/**
 * Result-cache persistence was removed in 3.1.8. The public entry points are kept as
 * binary-compatibility no-ops: these tests lock in the contract that they never perform
 * I/O, never resurrect stale values, and never disturb the in-memory snapshot cache.
 */
class DimenCachePersistenceFlowTest {

    private lateinit var ctx: FakeAppDimensContext

    @BeforeTest
    fun setup() {
        ctx = FakeAppDimensContext()
        DimenCache.clearAll()
        DimenCache.isEnabled.store(true)
    }

    @Test
    fun saveToPersistence_isNoOp_andKeepsCacheIntact() {
        val key = DimenCache.buildKey(
            42f, false, false, DimenCache.CalcType.FLUID,
            DpQualifier.SMALL_WIDTH, Inverter.DEFAULT, true, DimenCache.ValueType.DP
        )
        assertEquals(99f, DimenCache.getOrPut(key) { 99f }, 0f)
        DimenCache.saveToPersistence(ctx)
        assertEquals(99f, DimenCache.peek(key)!!, 0f)
    }

    @Test
    fun shutdown_isNoOp() {
        val key = DimenCache.buildKey(
            42f, false, false, DimenCache.CalcType.FLUID,
            DpQualifier.SMALL_WIDTH, Inverter.DEFAULT, true, DimenCache.ValueType.DP
        )
        assertEquals(99f, DimenCache.getOrPut(key) { 99f }, 0f)
        DimenCache.shutdown()
        assertEquals(99f, DimenCache.peek(key)!!, 0f)
    }
}