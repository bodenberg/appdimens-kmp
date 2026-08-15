package com.appdimens.kmp.core

import com.appdimens.kmp.code.DimenSdp
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.test.Test
import kotlin.time.TimeSource

/**
 * EN End-to-end fast-lane micro-benchmark (JVM/desktop-friendly, no Android APIs).
 *    Measures the real `code` API (`DimenSdp.sdp/sdpa/hdp/wdp`) against the raw
 *    two-multiply baseline (`base * scale * density`). The fast lane should sit
 *    within a small constant factor of the raw math — a big multiple indicates the
 *    port lost the per-window metrics memoization (fast window slot / ThreadLocal)
 *    that the Android original relies on.
 *
 *    Results are printed to stdout for human inspection; this test asserts nothing
 *    about absolute timing (CI machines vary wildly).
 * PT Micro-benchmark ponta-a-ponta do fast lane (JVM/desktop, sem APIs Android).
 *    Mede a API `code` real (`DimenSdp.sdp/sdpa/hdp/wdp`) contra a linha de base
 *    crua de duas multiplicações (`base * scale * density`). O fast lane deve ficar
 *    dentro de um pequeno fator constante da matemática crua — um múltiplo grande
 *    indica que o port perdeu a memoização de métricas por janela (fast window slot /
 *    ThreadLocal) que o Android original usa.
 */
@OptIn(ExperimentalAtomicApi::class)
class DimenFastLaneEndToEndTest {

    private val count = 2_000_000
    private var checksum = 0f

    private val clockOrigin = TimeSource.Monotonic.markNow()
    private fun now(): Long = clockOrigin.elapsedNow().inWholeNanoseconds

    private inline fun bestOf(trials: Int = 7, crossinline block: () -> Unit): Long {
        var best = Long.MAX_VALUE
        repeat(trials) {
            var acc = 0f
            val t = now()
            block()
            val elapsed = now() - t
            if (elapsed < best) best = elapsed
            checksum += acc
        }
        return best
    }

    private fun report(label: String, ns: Long) {
        println("fastlane_e2e_${label}: ${ns / count} ns/op (total ${ns} ns / $count ops)")
    }

    @Test
    fun measureCodeApiFastLane() {
        DimenCache.clearAll()
        val ctx = FakeAppDimensContext(sw = 393, w = 393, h = 842, dpi = 440)
        val metrics = DimenCache.metricsFor(ctx)
        val scale = metrics.scale
        val density = metrics.density

        // Warm the JIT + prime the fast window slot.
        repeat(200_000) { checksum += DimenSdp.sdp(ctx, 1) }
        checksum += DimenSdp.sdpa(ctx, 1) + DimenSdp.hdp(ctx, 1) + DimenSdp.wdp(ctx, 1)

        report("raw_math_2mul", bestOf {
            for (i in 0 until count) checksum += 1f * scale * density
        })

        report("sdp_code_api", bestOf {
            for (i in 0 until count) checksum += DimenSdp.sdp(ctx, 1)
        })

        report("sdpa_code_api", bestOf {
            for (i in 0 until count) checksum += DimenSdp.sdpa(ctx, 1)
        })

        report("hdp_code_api", bestOf {
            for (i in 0 until count) checksum += DimenSdp.hdp(ctx, 1)
        })

        report("wdp_code_api", bestOf {
            for (i in 0 until count) checksum += DimenSdp.wdp(ctx, 1)
        })

        report("mixed_sdp_12vals", bestOf {
            val vals = intArrayOf(1, 4, 8, 10, 12, 16, 20, 24, 32, 48, 64, 100)
            for (i in 0 until count) checksum += DimenSdp.sdp(ctx, vals[i % vals.size])
        })

        println("fastlane_e2e_checksum: $checksum")
    }
}
