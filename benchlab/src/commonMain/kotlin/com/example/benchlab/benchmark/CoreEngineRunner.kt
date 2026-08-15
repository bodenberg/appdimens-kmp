/**
 * @author Bodenberg
 *
 * EN Off-main benchmark core (Benchmark B — Engine, Dispatchers.Default).
 *    Measures AppDimens Dynamic KMP only: Lib #2 has no non-Compose API
 *    (its `.sdp` extension is @Composable and reads LocalConfiguration), so
 *    it is N/A outside composition and excluded here instead of being
 *    measured under a different methodology.
 *    Same methodology as the Compose probe: identical warm-up, 9 samples,
 *    50,000 iterations per sample, anti-DCE checksums, constant 1dp +
 *    mixed-value workloads, medians as headline.
 *    Also captures sdpa (aspect ratio) resolution values.
 *
 * PT Núcleo off-main do benchmark (Benchmark B — Motor, Dispatchers.Default).
 *    Mede apenas AppDimens Dynamic KMP: a Lib #2 não possui API não-Compose
 *    (a extensão `.sdp` dela é @Composable e lê LocalConfiguration), então é
 *    N/A fora da composição e excluída aqui em vez de ser medida com
 *    metodologia diferente.
 *    Mesma metodologia da sonda Compose: warm-up idêntico, 9 amostras,
 *    50.000 iterações por amostra, checksums anti-DCE, workloads 1dp
 *    constante + valores mistos, medianas como principal.
 *    Também captura valores de resolução sdpa (aspect ratio).
 */
package com.example.benchlab.benchmark

import com.appdimens.kmp.code.DimenSdp
import com.appdimens.kmp.core.AppDimensContext
import com.appdimens.kmp.core.ScreenConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "BENCHLAB"

/**
 * EN Runs the off-main engine benchmark (AppDimens KMP; Lib #2 is N/A outside
 *    composition because its API is @Composable).
 * PT Executa o benchmark do motor off-main (AppDimens KMP; Lib #2 é N/A fora
 *    da composição porque a API dela é @Composable).
 *
 * @param appContext EN AppDimens window handle (from `AppDimensProvider`). PT Handle de janela da AppDimens.
 * @param onPhaseChange EN Callback for phase transitions. PT Callback para transições de fase.
 */
suspend fun runCoreEngineBenchmark(
    appContext: AppDimensContext,
    onPhaseChange: (BenchPhase) -> Unit,
): CoreBenchmarkResult = withContext(Dispatchers.Default) {

    onPhaseChange(BenchPhase.CORE)

    val dynamic = DimenSdp

    // ── Identical warm-up ──
    var warmAcc = 0f
    repeat(BENCH_WARMUP_COUNT) {
        warmAcc += dynamic.sdp(appContext, 1)
    }

    val dConst = LongArray(BENCH_SAMPLE_COUNT)
    val dMixed = LongArray(BENCH_SAMPLE_COUNT)

    var dConstAcc = warmAcc
    var dMixedAcc = warmAcc

    // ── Workload 1: constant 1dp ──
    for (sample in 0 until BENCH_SAMPLE_COUNT) {
        val t1 = benchNanoTime()
        repeat(BENCH_MEASURE_COUNT) { dConstAcc += dynamic.sdp(appContext, 1) }
        dConst[sample] = benchNanoTime() - t1
    }

    // ── Workload 2: mixed values ──
    for (sample in 0 until BENCH_SAMPLE_COUNT) {
        val t1 = benchNanoTime()
        repeat(BENCH_MEASURE_COUNT) { i ->
            dMixedAcc += dynamic.sdp(appContext, BENCH_MIXED_VALUES[i % BENCH_MIXED_VALUES.size])
        }
        dMixed[sample] = benchNanoTime() - t1
    }

    // ── sdpa resolution values (AR — not available in Lib #2) ──
    benchLog("Engine: dynamic.const=" + statsOf(dConst, BENCH_MEASURE_COUNT.toLong()).medianNs +
        " dynamic.mixed=" + statsOf(dMixed, BENCH_MEASURE_COUNT.toLong()).medianNs)

    CoreBenchmarkResult(
        coreEngine = CoreEngineResult(
            dynamic = LibraryTiming(
                constant1dp = statsOf(dConst, BENCH_MEASURE_COUNT.toLong()),
                mixedValues = statsOf(dMixed, BENCH_MEASURE_COUNT.toLong()),
                constantChecksum = dConstAcc + warmAcc,
                mixedChecksum = dMixedAcc + warmAcc,
            ),
        ),
    )
}

/**
 * EN Combines the Compose probe result with the engine result, the legacy
 *    T1/T2/T3 tests and device info.
 * PT Combina o resultado da sonda Compose com o do motor, os testes legados
 *    T1/T2/T3 e as informações do device.
 */
fun assembleResult(
    probe: ComposeProbeResult,
    core: CoreBenchmarkResult,
    legacy: LegacyTestResult,
    config: ScreenConfiguration,
    density: Float,
): CompetitorBenchmarkResult {
    val sw = config.smallestScreenWidthDp.takeIf { it > 0 }
        ?: minOf(config.screenWidthDp, config.screenHeightDp).coerceAtLeast(0)

    return CompetitorBenchmarkResult(
        composeApi = probe.composeApi,
        coreEngine = core.coreEngine,
        legacy = legacy,
        windowSw = sw,
        windowW = config.screenWidthDp.coerceAtLeast(0),
        windowH = config.screenHeightDp.coerceAtLeast(0),
        density = density,
    ).also { r ->
        benchLog("Device: sw=${r.windowSw}dp w=${r.windowW}dp h=${r.windowH}dp density=${fmt("%.2f", r.density)}")
    }
}
