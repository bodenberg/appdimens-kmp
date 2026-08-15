/**
 * @author Bodenberg
 *
 * EN Legacy off-main benchmark core (original T1/T2/T3 methodology).
 *    Runs 3 independent test passes, each measuring:
 *    - Dp resolution values for 1dp, 10dp, 100dp in both libraries
 *      (AppDimens KMP + Lib #2 — Lib #2 values come from the composable probe)
 *    - Time per single dp call in AppDimens (Lib #2 measured by probe)
 *    Kept as-is (Long ns averages, System.nanoTime, delay(100) between
 *    warm-up and measurement) for continuity with previously published
 *    results. The NEW methodology lives in ComposeCompetitorProbe + CoreEngineRunner.
 *
 * PT Núcleo off-main legado do benchmark (metodologia original T1/T2/T3).
 *    Executa 3 passes de teste independentes, cada um medindo:
 *    - Valores de resolução dp para 1dp, 10dp, 100dp nas duas bibliotecas
 *      (AppDimens KMP + Lib #2 — valores da Lib #2 vêm da sonda composable)
 *    - Tempo por chamada única de dp em AppDimens (Lib #2 medido pela sonda)
 *    Mantido como estava (médias Long ns, System.nanoTime, delay(100) entre
 *    warm-up e medição) por continuidade com resultados já publicados.
 *    A metodologia NOVA vive em ComposeCompetitorProbe + CoreEngineRunner.
 */
package com.example.benchlab.benchmark

import com.appdimens.dynamic.code.DimenSdp
import com.appdimens.dynamic.core.AppDimensContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TAG = "BENCHLAB"
private const val TEST_RUNS = 3
private const val WARMUP_COUNT = 5_000
private const val MEASURE_COUNT = 50_000

/** DP values to resolve in each test pass. */
private val DP_VALUES = intArrayOf(1, 10, 100)

/**
 * EN Runs the legacy T1/T2/T3 comparison benchmark off the main thread.
 * PT Executa o benchmark comparativo legado T1/T2/T3 fora da thread principal.
 *
 * @param appContext EN AppDimens window handle (from `AppDimensProvider`). PT Handle de janela da AppDimens.
 * @param lib2Result EN Pre-measured Lib #2 data from the legacy composable probe. PT Dados pré-medidos da Lib #2 da sonda composable legada.
 * @param onPhaseChange EN Callback for phase transitions. PT Callback para transições de fase.
 */
suspend fun runLegacyBenchmark(
    appContext: AppDimensContext,
    lib2Result: Concorrente2ProbeResult,
    onPhaseChange: (BenchPhase) -> Unit
): LegacyTestResult = withContext(Dispatchers.Default) {

    val app = DimenSdp

    // ── WARMUP ──────────────────────────────────────────────────────────────
    onPhaseChange(BenchPhase.TEST1)
    repeat(WARMUP_COUNT) {
        for (dp in DP_VALUES) {
            app.sdp(appContext, dp)
            app.hdp(appContext, dp)
            app.wdp(appContext, dp)
        }
    }
    delay(100)

    // ── MEASURE 3 TEST RUNS ─────────────────────────────────────────────────
    val testResults = mutableListOf<DpResolution2>()
    val timeResults = mutableListOf<SingleDpTiming2>()

    for (run in 1..TEST_RUNS) {
        onPhaseChange(
            when (run) {
                1 -> BenchPhase.TEST1
                2 -> BenchPhase.TEST2
                else -> BenchPhase.TEST3
            }
        )

        // ── Dp resolution values: sdp (no AR) ─────────────────────────
        val dp1App  = app.sdp(appContext, 1)
        val dp10App = app.sdp(appContext, 10)
        val dp100App = app.sdp(appContext, 100)

        // ── Dp resolution values: sdpa (with AR) ────────────────────────
        val dp1AppAr  = app.sdpa(appContext, 1)
        val dp10AppAr = app.sdpa(appContext, 10)
        val dp100AppAr = app.sdpa(appContext, 100)

        testResults += DpResolution2(
            dp1AppDimens = dp1App, dp1Lib2 = lib2Result.dp1Px,
            dp10AppDimens = dp10App, dp10Lib2 = lib2Result.dp10Px,
            dp100AppDimens = dp100App, dp100Lib2 = lib2Result.dp100Px,
            dp1AppDimensAr = dp1AppAr,
            dp10AppDimensAr = dp10AppAr,
            dp100AppDimensAr = dp100AppAr,
        )

        // ── Time per single dp call: sdp (no AR) ────────────────────────
        val appSingleNs = runTimedNs { app.sdp(appContext, 1) }

        // ── Time per single dp call: sdpa (with AR) ─────────────────────
        val appSingleArNs = runTimedNs { app.sdpa(appContext, 1) }

        // Lib #2 time is fixed from the probe (already measured once)
        timeResults += SingleDpTiming2(
            appDimensNs = appSingleNs,
            lib2Ns = lib2Result.sdpAvgNs,
            appDimensArNs = appSingleArNs,
        )

        benchLog("Legacy Test $run: dp1=app=$dp1App lib2=${lib2Result.dp1Px} " +
            "dp10=app=$dp10App lib2=${lib2Result.dp10Px} " +
            "dp100=app=$dp100App lib2=${lib2Result.dp100Px} " +
            "ar: dp1=app=$dp1AppAr dp10=app=$dp10AppAr dp100=app=$dp100AppAr " +
            "time: app=${appSingleNs}ns lib2=${lib2Result.sdpAvgNs}ns ar: app=${appSingleArNs}ns")
    }

    val avgApp = timeResults.map { it.appDimensNs }.average().toLong()
    val avgLib2 = timeResults.map { it.lib2Ns }.average().toLong()
    val avgAppAr = timeResults.map { it.appDimensArNs }.average().toLong()

    benchLog("Legacy compare avg: appDimens=${avgApp}ns lib2=${avgLib2}ns appDimensAr=${avgAppAr}ns")

    LegacyTestResult(
        test1 = testResults[0],
        test2 = testResults[1],
        test3 = testResults[2],
        timeTest1 = timeResults[0],
        timeTest2 = timeResults[1],
        timeTest3 = timeResults[2],
        avgAppDimensNs = avgApp,
        avgLib2Ns = avgLib2,
        avgAppDimensArNs = avgAppAr,
    )
}

/** EN Times [MEASURE_COUNT] executions of [block] and returns ns per single call. */
private inline fun runTimedNs(block: () -> Float): Long {
    val start = benchNanoTime()
    var acc = 0f
    repeat(MEASURE_COUNT) { acc += block() }
    val elapsed = benchNanoTime() - start
    // Consume acc so the loop cannot be optimized away (anti-DCE).
    if (acc == Float.NaN) throw IllegalStateException("unreachable")
    return elapsed / MEASURE_COUNT
}
