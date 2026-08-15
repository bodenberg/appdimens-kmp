/**
 * @author Bodenberg
 *
 * EN Composable 2-way probe (Benchmark A — Compose API, main thread).
 *    Measures AppDimens Dynamic KMP and Lib #2 (`network.chaintech:
 *    sdp-ssp-compose-multiplatform`) TOGETHER inside the same composition,
 *    so both face the same environment (main thread, same JIT state, same
 *    warm-up, same counts):
 *    - identical warm-up (20,000 resolutions of 1dp per library),
 *    - 9 samples per workload, 50,000 iterations per sample,
 *    - order rotation: each library occupies each position equally,
 *    - every timed loop accumulates into an anti-DCE checksum,
 *    - two workloads: constant 1dp and a predetermined mixed-value set.
 *
 *    The work is CHUNKED across recompositions (5,000 ops per frame):
 *    the main thread never blocks for more than a few dozen ms, so the
 *    UI stays responsive and the benchmark environment is not corrupted
 *    by MIUI/thermal throttling that an ANR-style freeze would trigger.
 *    Per-chunk timing excludes inter-chunk gaps; chunk totals are summed
 *    into the per-sample elapsed time, so the methodology is equivalent
 *    to a single uninterrupted loop. Reports the result once via [onResult].
 *
 * PT Sonda composable 2-vias (Benchmark A — API Compose, main thread).
 *    Mede AppDimens Dynamic KMP e Lib #2 (`network.chaintech:
 *    sdp-ssp-compose-multiplatform`) JUNTAS na mesma composição, para que
 *    as duas enfrentem o mesmo ambiente (main thread, mesmo estado de JIT,
 *    mesmo warm-up, mesmas contagens):
 *    - warm-up idêntico (20.000 resoluções de 1dp por biblioteca),
 *    - 9 amostras por workload, 50.000 iterações por amostra,
 *    - rotação de ordem: cada biblioteca ocupa cada posição igualmente,
 *    - todo loop cronometrado acumula um checksum anti-DCE,
 *    - dois workloads: 1dp constante e um conjunto misto predeterminado.
 *
 *    O trabalho é FATIADO entre recomposições (5.000 ops por frame):
 *    a main thread nunca bloqueia por mais de algumas dezenas de ms, então
 *    a UI permanece responsiva e o ambiente de medição não é corrompido
 *    pelo throttling do MIUI/thermal que um freeze estilo ANR causaria.
 *    O timing por fatia exclui os gaps entre fatias; os totais são somados
 *    ao tempo da amostra, então a metodologia equivale a um loop único.
 *    Reporta o resultado uma vez via [onResult].
 */
package com.example.benchlab.benchmark

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.appdimens.dynamic.code.DimenSdp
import com.appdimens.dynamic.core.localAppDimensContext

/** EN Identical warm-up iterations per library. PT Warm-up idêntico por biblioteca. */
internal const val BENCH_WARMUP_COUNT = 20_000

/** EN Iterations per timed sample. PT Iterações por amostra cronometrada. */
internal const val BENCH_MEASURE_COUNT = 50_000

/** EN Number of timed samples per workload. PT Número de amostras cronometradas por workload. */
internal const val BENCH_SAMPLE_COUNT = 9

/**
 * EN Ops executed per composition chunk. Keeps each main-thread block small
 *    (a few dozen ms) so the UI never freezes during the benchmark.
 * PT Ops executadas por fatia de composição. Mantém cada bloco da main
 *    thread pequeno (algumas dezenas de ms) para a UI nunca congelar.
 */
internal const val BENCH_CHUNK_OPS = 5_000

/**
 * EN Predetermined mixed-value set (no RNG inside the measured region).
 *    Mirrors a real screen: various dp values instead of a single hot value.
 * PT Conjunto misto predeterminado (sem RNG dentro da região medida).
 *    Espelha uma tela real: vários valores dp em vez de um único valor quente.
 */
internal val BENCH_MIXED_VALUES = intArrayOf(1, 4, 8, 10, 12, 16, 20, 24, 32, 48, 64, 100)

private const val LIB_DYNAMIC = 0
private const val LIB_CHAINTECH = 1

private const val WARMUP_CHUNKS = BENCH_WARMUP_COUNT / BENCH_CHUNK_OPS
private const val MEASURE_CHUNKS = BENCH_MEASURE_COUNT / BENCH_CHUNK_OPS

/** EN One chunk of the measurement schedule. PT Uma fatia do cronograma de medição. */
private data class ChunkSpec(val sample: Int, val mixed: Boolean, val lib: Int)

/** EN Mutable accumulators/samples that survive recomposition. */
private class ProbeState {
    var warmAcc = 0f
    val dConst = LongArray(BENCH_SAMPLE_COUNT)
    val cConst = LongArray(BENCH_SAMPLE_COUNT)
    val dMixed = LongArray(BENCH_SAMPLE_COUNT)
    val cMixed = LongArray(BENCH_SAMPLE_COUNT)
    var dConstAcc = 0f
    var cConstAcc = 0f
    var dMixedAcc = 0f
    var cMixedAcc = 0f
}

/**
 * EN Measures the two libraries inside composition. Compose only this probe
 *    while [active] is true; it self-guards so the timed chunks run exactly
 *    once per activation, one chunk per frame.
 *
 * @param active EN Whether to run the probe. PT Se a sonda deve rodar.
 * @param onResult EN Callback with the measured result. PT Callback com o resultado medido.
 */
@Composable
fun ComposeCompetitorProbe(
    active: Boolean,
    onResult: (ComposeProbeResult) -> Unit,
) {
    // EN Dynamic is measured through the non-Compose `code` API (same call shape the
    //    Android original used for its headline numbers — a plain function, inlined
    //    fast lane, ~ns). Lib #2 has no code API (its `.sdp` is @Composable), so it is
    //    measured through its extension. The AppDimens `Number.sdp` @Composable
    //    extension is intentionally NOT imported here: importing both `sdp` symbols
    //    would make `1.sdp` resolve to Lib #2's more-specific `Int.sdp`, silently
    //    measuring the wrong library.
    // PT Dynamic é medido pela API `code` não-Compose (mesma forma de chamada do
    //    Android original — função plana, fast lane inline, ~ns). A Lib #2 não tem
    //    API code (o `.sdp` dela é @Composable), então é medida pela extensão dela.
    //    A extensão @Composable `Number.sdp` da AppDimens é intencionalmente NÃO
    //    importada aqui: importar os dois símbolos `sdp` faria `1.sdp` resolver para
    //    o `Int.sdp` mais específico da Lib #2, medindo silenciosamente a lib errada.
    // EN The probe only runs inside AppDimensProvider (all benchlab entries wrap it),
    //    so the window handle is always present; `!!` is a safe non-null assertion here.
    // PT A sonda só roda dentro de AppDimensProvider (todos os entries do benchlab
    //    envolvem com ele), então o handle de janela está sempre presente; `!!` é
    //    uma asserção não-nula segura aqui.
    val appContext = localAppDimensContext()!!
    val dynamic = DimenSdp
    val state = remember(active) { ProbeState() }
    var measured by remember(active) { mutableStateOf(false) }
    var step by remember(active) { mutableStateOf(0) }

    val schedule = remember(active) {
        val rotations = listOf(
            listOf(LIB_DYNAMIC, LIB_CHAINTECH),
            listOf(LIB_CHAINTECH, LIB_DYNAMIC),
        )
        buildList {
            for (sample in 0 until BENCH_SAMPLE_COUNT) {
                val order = rotations[sample % 2]
                for (mixed in listOf(false, true)) {
                    for (lib in order) {
                        for (c in 0 until MEASURE_CHUNKS) {
                            add(ChunkSpec(sample, mixed, lib))
                        }
                    }
                }
            }
        }
    }

    if (active && !measured) {
        when {
            // ── Identical warm-up: same loop body, same count, per library ──
            step < WARMUP_CHUNKS -> {
                repeat(BENCH_CHUNK_OPS) {
                    state.warmAcc += dynamic.sdp(appContext, 1)
                    state.warmAcc += lib2SdpDp(1).value
                }
            }

            // ── Measurement chunks ──
            step < WARMUP_CHUNKS + schedule.size -> {
                val spec = schedule[step - WARMUP_CHUNKS]
                when {
                    !spec.mixed && spec.lib == LIB_DYNAMIC -> {
                        val t = benchNanoTime()
                        repeat(BENCH_CHUNK_OPS) { state.dConstAcc += dynamic.sdp(appContext, 1) }
                        state.dConst[spec.sample] += benchNanoTime() - t
                    }

                    !spec.mixed && spec.lib == LIB_CHAINTECH -> {
                        val t = benchNanoTime()
                        repeat(BENCH_CHUNK_OPS) { state.cConstAcc += lib2SdpDp(1).value }
                        state.cConst[spec.sample] += benchNanoTime() - t
                    }

                    spec.mixed && spec.lib == LIB_DYNAMIC -> {
                        val t = benchNanoTime()
                        repeat(BENCH_CHUNK_OPS) { i ->
                            state.dMixedAcc += dynamic.sdp(appContext, BENCH_MIXED_VALUES[i % BENCH_MIXED_VALUES.size])
                        }
                        state.dMixed[spec.sample] += benchNanoTime() - t
                    }

                    else -> {
                        val t = benchNanoTime()
                        repeat(BENCH_CHUNK_OPS) { i ->
                            state.cMixedAcc += lib2SdpDp(BENCH_MIXED_VALUES[i % BENCH_MIXED_VALUES.size]).value
                        }
                        state.cMixed[spec.sample] += benchNanoTime() - t
                    }
                }
            }

            // ── Finish: report ──
            else -> {
                measured = true
                SideEffect {
                    val dConst = statsOf(state.dConst, BENCH_MEASURE_COUNT.toLong())
                    val dMixed = statsOf(state.dMixed, BENCH_MEASURE_COUNT.toLong())
                    val cConst = statsOf(state.cConst, BENCH_MEASURE_COUNT.toLong())
                    val cMixed = statsOf(state.cMixed, BENCH_MEASURE_COUNT.toLong())
                    benchLog("Probe A: dynamic.const=${dConst.medianNs} lib2.const=${cConst.medianNs} dynamic.mixed=${dMixed.medianNs} lib2.mixed=${cMixed.medianNs}")
                    benchLog("Probe A stats: dynamic.const=[min=${dConst.minNs},p90=${dConst.p90Ns},max=${dConst.maxNs}] dynamic.mixed=[min=${dMixed.minNs},p90=${dMixed.p90Ns},max=${dMixed.maxNs}]")
                    benchLog("Probe A stats: lib2.const=[min=${cConst.minNs},p90=${cConst.p90Ns},max=${cConst.maxNs}] lib2.mixed=[min=${cMixed.minNs},p90=${cMixed.p90Ns},max=${cMixed.maxNs}]")
                    benchLog("Probe A finished: ${schedule.size} chunks measured")
                    onResult(
                        ComposeProbeResult(
                            composeApi = ComposeApiResult(
                                dynamic = LibraryTiming(
                                    constant1dp = dConst,
                                    mixedValues = dMixed,
                                    constantChecksum = state.dConstAcc + state.warmAcc,
                                    mixedChecksum = state.dMixedAcc + state.warmAcc,
                                ),
                                chaintech = LibraryTiming(
                                    constant1dp = cConst,
                                    mixedValues = cMixed,
                                    constantChecksum = state.cConstAcc + state.warmAcc,
                                    mixedChecksum = state.cMixedAcc + state.warmAcc,
                                ),
                            )
                        )
                    )
                }
            }
        }
    }

    // EN Advance one chunk per frame. The step++ MUST happen outside the
    //    composition pass (LaunchedEffect): writing to state during composition
    //    does not reliably schedule the next recomposition, which left the
    //    probe stalled until the 120s controller timeout.
    // PT Avança uma fatia por frame. O step++ PRECISA ocorrer fora do passe de
    //    composição (LaunchedEffect): escrever em estado durante a composição
    //    não agenda confiavelmente a próxima recomposição, o que deixava a
    //    sonda travada até o timeout de 120s do controller.
    LaunchedEffect(active, step) {
        if (active && !measured) {
            step++
        }
    }
}
