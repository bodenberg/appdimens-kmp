/**
 * Native thread-locality of [MetricsScopeHolder].
 *
 * [MetricsScopeHolder] is `@ThreadLocal` on Kotlin/Native: every worker gets its
 * own instance, so two workers resolving dimensions concurrently can never
 * observe each other's `current`. This test pins that property with a
 * yield-heavy ping-pong between workers — without thread-locality the shared
 * slot would cross values with high probability.
 */
package com.appdimens.kmp.core

import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.TransferMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MetricsScopeHolderNativeTest {

    private fun metricsFor(sw: Int): DimenMetrics =
        DimenMetrics.from(
            ScreenConfiguration(
                screenWidthDp = sw,
                screenHeightDp = 800,
                smallestScreenWidthDp = sw,
                densityDpi = 420,
                fontScale = 1f,
                orientation = ScreenConfiguration.ORIENTATION_PORTRAIT,
                uiMode = 1,
            ),
        )

    @Test
    fun twoWorkersNeverCrossCurrent() = runNativeWorkers()

    private fun runNativeWorkers() {
        val a = metricsFor(320)
        val b = metricsFor(480)

        val workerA = Worker.start()
        val workerB = Worker.start()

        try {
            val futureA = workerA.execute(TransferMode.SAFE, { a }) { mine ->
                // Heavy alternating workload: two OS threads naturally preempt each
                // other on shared CPUs, so each worker would observe the other's
                // `current` if the holder were a single shared mutable slot.
                var ok = true
                repeat(5000) {
                    MetricsScopeHolder.current = mine
                    var acc = 0L
                    repeat(200) { acc += it }
                    if (MetricsScopeHolder.current !== mine) {
                        ok = false
                        return@execute false
                    }
                }
                ok
            }
            val futureB = workerB.execute(TransferMode.SAFE, { b }) { mine ->
                var ok = true
                repeat(5000) {
                    MetricsScopeHolder.current = mine
                    var acc = 0L
                    repeat(200) { acc += it }
                    if (MetricsScopeHolder.current !== mine) {
                        ok = false
                        return@execute false
                    }
                }
                ok
            }

            assertEquals(true, futureA.consume { it }, "worker A must always read its own metrics")
            assertEquals(true, futureB.consume { it }, "worker B must always read its own metrics")
        } finally {
            workerA.requestTermination().consume { }
            workerB.requestTermination().consume { }
        }
    }

    @Test
    fun nestedWithMetricsRestoresPreviousOnSameWorker() {
        val outer = metricsFor(320)
        val inner = metricsFor(480)

        val result = DimenCache.withMetrics(outer) {
            MetricsScopeHolder.current = inner
            val mid = MetricsScopeHolder.current
            val restored = DimenCache.withMetrics(outer) { MetricsScopeHolder.current }
            mid === inner && restored === outer
        }
        assertTrue(result, "nested withMetrics must restore the outer value")
    }
}
