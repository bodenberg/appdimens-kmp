package com.appdimens.kmp.core

import com.appdimens.kmp.common.DpQualifier
import com.appdimens.kmp.common.Inverter
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndIncrement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.runTest
import kotlin.test.assertTrue
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalAtomicApi::class)
class DimenCacheRaceTest {

    @BeforeTest
    fun setup() {
        DimenCache.clearAll()
        DimenCache.isEnabled.store(true)
    }

    @Test
    fun concurrentWrites_noIncorrectValues() = runTest {
        val threads = 8
        val iterations = 5000
        val wrongCount = AtomicInt(0)

        coroutineScope {
            (0 until threads).map { t ->
                async(Dispatchers.Default) {
                    for (i in 0 until iterations) {
                        val baseValue = (t * iterations + i).toFloat()
                        val key = DimenCache.buildKey(
                            baseValue, false, false, DimenCache.CalcType.FLUID,
                            DpQualifier.SMALL_WIDTH, Inverter.DEFAULT, false, DimenCache.ValueType.DP
                        )
                        val result = DimenCache.getOrPut(key) { baseValue * 2f }
                        if (result != baseValue * 2f) {
                            val peeked = DimenCache.peek(key)
                            if (peeked != null && peeked != baseValue * 2f) {
                                wrongCount.fetchAndIncrement()
                            }
                        }
                    }
                }
            }.awaitAll()
        }

        assertTrue(
            wrongCount.load() == 0,
            "Expected zero wrong values but got ${wrongCount.load()}"
        )
    }

    @Test
    fun concurrentWrites_sameSlotCollision() = runTest {
        val metrics = DimenMetrics.DEFAULT
        val targetKey1 = DimenCache.buildKey(
            100f, false, false, DimenCache.CalcType.FLUID,
            DpQualifier.SMALL_WIDTH, Inverter.DEFAULT, false, DimenCache.ValueType.DP
        )
        val h1 = (targetKey1 xor (targetKey1 ushr 32)).toInt()
        val targetSlot = (h1 xor (h1 ushr 16)) and (2048 / 4 - 1)

        var collidingKey = 0L
        for (bv in 101..2000000) {
            val k = DimenCache.buildKey(
                bv.toFloat(), false, false, DimenCache.CalcType.FLUID,
                DpQualifier.SMALL_WIDTH, Inverter.DEFAULT, false, DimenCache.ValueType.DP
            )
            val h = (k xor (k ushr 32)).toInt()
            val m = h xor (h ushr 16)
            if ((m and (2048 / 4 - 1)) == targetSlot) {
                collidingKey = k
                break
            }
        }

        if (collidingKey == 0L) return@runTest

        val threads = 4
        val iterations = 10000
        val wrongCount = AtomicInt(0)

        val keys = longArrayOf(targetKey1, collidingKey)
        val values = floatArrayOf(200f, 777f)

        coroutineScope {
            (0 until threads).map { t ->
                async(Dispatchers.Default) {
                    val idx = t % 2
                    for (i in 0 until iterations) {
                        val result = DimenCache.getOrPut(keys[idx], metrics) { values[idx] }
                        if (result != values[0] && result != values[1]) {
                            wrongCount.fetchAndIncrement()
                        }
                    }
                }
            }.awaitAll()
        }

        assertTrue(
            wrongCount.load() == 0,
            "Same-slot collision should never produce a value other than the two expected ones, got ${wrongCount.load()} wrong"
        )
    }
}
