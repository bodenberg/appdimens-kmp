package com.appdimens.kmp.core

import com.appdimens.kmp.common.DpQualifier
import com.appdimens.kmp.common.Inverter
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalAtomicApi::class)
class DimenCacheSparseSerializationTest {

    @BeforeTest
    fun setup() {
        DimenCache.clearAll()
        DimenCache.isEnabled.store(true)
    }

    @Test
    fun serializeAndLoad_areNoOps() {
        val key = DimenCache.buildKey(
            42f, false, false, DimenCache.CalcType.FLUID,
            DpQualifier.SMALL_WIDTH, Inverter.DEFAULT, false, DimenCache.ValueType.DP
        )
        DimenCache.getOrPut(key) { 99f }

        val blob = DimenCache.serializeToByteArray()
        assertEquals(4, blob.size)

        DimenCache.clearAll()
        DimenCache.loadFromByteArray(blob)
        assertNull(DimenCache.peek(key))
    }
}
