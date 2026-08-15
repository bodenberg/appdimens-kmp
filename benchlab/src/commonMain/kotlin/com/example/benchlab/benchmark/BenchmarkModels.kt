/**
 * @author Bodenberg
 *
 * EN Data models + phases for the 2-way competitor benchmark
 *    (AppDimens Dynamic KMP vs Lib #2 — `network.chaintech:sdp-ssp-compose-multiplatform`,
 *    the only other library in this comparison that ships a KMP build).
 *
 *    NEW methodology (headline):
 *    - Benchmark A (Compose API): both libraries measured together on the
 *      main thread inside composition, identical warm-up, identical operation
 *      count, order rotation, anti-DCE checksum, chunked across frames so
 *      the UI never freezes.
 *    - Benchmark B (Engine): AppDimens KMP only, off main thread
 *      (Dispatchers.Default). Lib #2 has no non-Compose API (its `.sdp`
 *      extension is @Composable), so it cannot be measured outside
 *      composition — N/A, documented as such.
 *    Headline number is the MEDIAN of N samples (steady-state ns/op),
 *    with min/P90/max as spread. All timing stored as Double ns.
 *
 *    LEGACY methodology (kept for continuity with previously published
 *    results): 3 test runs (T1/T2/T3) measuring dp resolution values and
 *    time per single call (Long ns, average of 3 runs) — original code.
 *
 * PT Modelos de dados + fases do benchmark de 2 vias
 *    (AppDimens Dynamic KMP vs Lib #2 — `network.chaintech:sdp-ssp-compose-multiplatform`,
 *    a única outra biblioteca desta comparação com build KMP).
 *
 *    Metodologia NOVA (principal):
 *    - Benchmark A (API Compose): as duas bibliotecas medidas juntas na main
 *      thread dentro da composição, warm-up idêntico, mesma contagem de
 *      operações, rotação de ordem, checksum anti-DCE, fatiado entre frames
 *      para a UI nunca congelar.
 *    - Benchmark B (Motor): apenas AppDimens KMP, fora da main thread
 *      (Dispatchers.Default). A Lib #2 não possui API não-Compose (a extensão
 *      `.sdp` dela é @Composable), então não pode ser medida fora da
 *      composição — N/A, documentado como tal.
 *    O número principal é a MEDIANA de N amostras (ns/op steady-state),
 *    com min/P90/max como dispersão. Todo tempo armazenado como Double ns.
 *
 *    Metodologia LEGADA (mantida por continuidade com resultados já
 *    publicados): 3 execuções de teste (T1/T2/T3) medindo valores de
 *    resolução dp e tempo por chamada única (Long ns, média de 3) — código original.
 */
package com.example.benchlab.benchmark

import kotlin.math.roundToInt

/** EN Phases of the benchmark pipeline. PT Fases do pipeline do benchmark. */
enum class BenchPhase {
    IDLE,
    WARMUP,
    CORE,
    TEST1,
    TEST2,
    TEST3,
    DONE;

    val displayLabel: String
        get() = when (this) {
            IDLE   -> "Idle — toque em Run"
            WARMUP -> "Benchmark A — medindo API Compose (main thread)…"
            CORE   -> "Benchmark B — medindo o motor (Dispatchers.Default)…"
            TEST1  -> "Testes legados 1/3…"
            TEST2  -> "Testes legados 2/3…"
            TEST3  -> "Testes legados 3/3…"
            DONE   -> "Pronto"
        }

    val progressFraction: Float
        get() = when (this) {
            IDLE   -> 0f
            WARMUP -> 0.25f
            CORE   -> 0.45f
            TEST1  -> 0.62f
            TEST2  -> 0.75f
            TEST3  -> 0.88f
            DONE   -> 1f
        }
}

// ═══════════════════════════════════════════════════════════════════════════════
// NEW METHODOLOGY MODELS
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * EN Spread statistics of one workload for one library, in ns/op.
 *    Median is the headline; min/P90/max describe the spread so a
 *    single scheduler/GC interruption does not dominate the report.
 * PT Estatísticas de dispersão de um workload para uma biblioteca, em ns/op.
 *    A mediana é o número principal; min/P90/max descrevem a dispersão para
 *    que uma única interrupção do scheduler/GC não domine o relatório.
 */
data class TimingStats(
    val medianNs: Double,
    val minNs: Double,
    val p90Ns: Double,
    val maxNs: Double,
)

/**
 * EN Per-library timing for both workloads plus anti-DCE checksums.
 * PT Tempo por biblioteca para os dois workloads + checksums anti-DCE.
 */
data class LibraryTiming(
    val constant1dp: TimingStats,
    val mixedValues: TimingStats,
    val constantChecksum: Float,
    val mixedChecksum: Float,
)

/**
 * EN Benchmark A — Compose API, main thread, both libraries in the same
 *    composable with identical warm-up/counts and rotated order.
 * PT Benchmark A — API Compose, main thread, as duas bibliotecas no mesmo
 *    composable com warm-up/contagens idênticos e ordem rotacionada.
 */
data class ComposeApiResult(
    val dynamic: LibraryTiming,
    val chaintech: LibraryTiming,
)

/**
 * EN Benchmark B — Engine, off main thread (Dispatchers.Default).
 *    AppDimens KMP only (Lib #2 has no non-Compose API → N/A).
 * PT Benchmark B — Motor, fora da main thread (Dispatchers.Default).
 *    Apenas AppDimens KMP (Lib #2 não possui API não-Compose → N/A).
 */
data class CoreEngineResult(
    val dynamic: LibraryTiming,
)

/**
 * EN Raw result captured by the composable 2-way probe (main thread).
 * PT Resultado bruto capturado pela sonda composable 2-vias (main thread).
 */
data class ComposeProbeResult(
    val composeApi: ComposeApiResult,
)

/**
 * EN Result of the off-main engine benchmark.
 * PT Resultado do benchmark do motor off-main.
 */
data class CoreBenchmarkResult(
    val coreEngine: CoreEngineResult,
)

// ═══════════════════════════════════════════════════════════════════════════════
// LEGACY METHODOLOGY MODELS (original T1/T2/T3 tests, kept for continuity)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * EN Dp resolution values from both libraries for a single legacy test run.
 *    Includes sdp (no AR) values for both libraries plus sdpa (with aspect
 *    ratio) values for AppDimens only (Lib #2 doesn't support sdpa).
 * PT Valores de resolução dp de ambas as bibliotecas em um teste legado.
 *    Inclui valores sdp (sem AR) das duas bibliotecas e valores sdpa (com
 *    aspect ratio) apenas da AppDimens (Lib #2 não suporta sdpa).
 */
data class DpResolution2(
    // sdp — no AR
    val dp1AppDimens: Float, val dp1Lib2: Float,
    val dp10AppDimens: Float, val dp10Lib2: Float,
    val dp100AppDimens: Float, val dp100Lib2: Float,
    // sdpa — with AR (Lib #2 doesn't support sdpa)
    val dp1AppDimensAr: Float,
    val dp10AppDimensAr: Float,
    val dp100AppDimensAr: Float,
)

/**
 * EN Time per single dp call from both libraries for a single legacy test run.
 * PT Tempo por chamada única de dp das duas bibliotecas em um teste legado.
 */
data class SingleDpTiming2(
    // sdp — no AR
    val appDimensNs: Long,
    val lib2Ns: Long,
    // sdpa — with AR (Lib #2 doesn't have sdpa)
    val appDimensArNs: Long,
)

/**
 * EN Full legacy 3-run result (original methodology, Long ns averages).
 * PT Resultado legado completo de 3 execuções (metodologia original, médias Long ns).
 */
data class LegacyTestResult(
    val test1: DpResolution2,
    val test2: DpResolution2,
    val test3: DpResolution2,
    val timeTest1: SingleDpTiming2,
    val timeTest2: SingleDpTiming2,
    val timeTest3: SingleDpTiming2,
    val avgAppDimensNs: Long,
    val avgLib2Ns: Long,
    val avgAppDimensArNs: Long,
)

/**
 * EN Raw measurements captured by the legacy composable Lib #2 probe (main thread).
 * PT Medições brutas capturadas pela sonda composable legada da Lib #2 (main thread).
 */
data class Concorrente2ProbeResult(
    val sdpAvgNs: Long,
    val dp1Px: Float,
    val dp10Px: Float,
    val dp100Px: Float,
    val checksum: Float,
)

// ═══════════════════════════════════════════════════════════════════════════════
// AGGREGATE RESULT
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * EN Full benchmark result: new methodology (Compose API + Engine) +
 *    legacy tests (T1/T2/T3) + device info.
 * PT Resultado completo: metodologia nova (API Compose + Motor) +
 *    testes legados (T1/T2/T3) + informações do device.
 */
data class CompetitorBenchmarkResult(
    val composeApi: ComposeApiResult,
    val coreEngine: CoreEngineResult,
    val legacy: LegacyTestResult,
    val windowSw: Int,
    val windowW: Int,
    val windowH: Int,
    val density: Float,
)

// ─── Statistics helpers ───────────────────────────────────────────────────────

/**
 * EN Computes median/min/P90/max as ns/op from raw per-sample ns timings.
 *    Each sample covers [opsPerSample] iterations, so every statistic is
 *    divided by it (a sample of 50k iterations totals ~36ns x 50k = ~1.8ms).
 *    Median is robust against single scheduler/GC interruptions.
 * PT Calcula mediana/min/P90/max em ns/op a partir dos ns brutos por amostra.
 *    Cada amostra cobre [opsPerSample] iterações, então toda estatística é
 *    dividida por ele (uma amostra de 50k iterações totaliza ~36ns x 50k = ~1.8ms).
 *    A mediana é robusta contra interrupções isoladas do scheduler/GC.
 */
internal fun statsOf(samples: LongArray, opsPerSample: Long): TimingStats {
    val sorted = samples.sortedArray()
    val n = sorted.size
    val mid = n / 2
    val median = if (n % 2 == 1) sorted[mid].toDouble() else (sorted[mid - 1] + sorted[mid]) / 2.0
    return TimingStats(
        medianNs = median / opsPerSample,
        minNs = sorted.first().toDouble() / opsPerSample,
        p90Ns = sorted[((n - 1) * 0.9).roundToInt()].toDouble() / opsPerSample,
        maxNs = sorted.last().toDouble() / opsPerSample,
    )
}

// ─── Formatting helpers ────────────────────────────────────────────────────────

/**
 * EN Minimal multiplatform printf-style formatter (JVM-only `String.format`
 *    is not available on wasmJs/JS/native). Supports `%s`, `%d`, `%,d` and
 *    `%.Nf` — the only specifiers used in this project. Extra args are ignored.
 * PT Formatador printf-style multiplataforma mínimo (o `String.format`, só
 *    existente no JVM, não está disponível em wasmJs/JS/native). Suporta
 *    `%s`, `%d`, `%,d` e `%.Nf` — os únicos especificadores usados aqui.
 *    Args extras são ignorados.
 */
internal fun fmt(format: String, vararg args: Any?): String {
    val sb = StringBuilder()
    var argIndex = 0
    var i = 0
    while (i < format.length) {
        val c = format[i]
        if (c == '%' && i + 1 < format.length && format[i + 1] != '%') {
            var j = i + 1
            var grouped = false
            if (j < format.length && format[j] == ',') {
                grouped = true
                j++
            }
            var precision = -1
            if (j < format.length && format[j] == '.') {
                var k = j + 1
                val start = k
                while (k < format.length && format[k].isDigit()) k++
                precision = if (k > start) format.substring(start, k).toInt() else 0
                j = k
            }
            if (j < format.length && (format[j] == 's' || format[j] == 'd' || format[j] == 'f')) {
                val type = format[j]
                val arg = if (argIndex < args.size) args[argIndex] else null
                argIndex++
                when (type) {
                    's' -> sb.append(arg?.toString() ?: "null")
                    'd' -> {
                        val v = (arg as? Number)?.toLong() ?: 0L
                        sb.append(if (grouped) groupThousands(v.toString()) else v.toString())
                    }
                    else -> {
                        val v = (arg as? Number)?.toDouble() ?: 0.0
                        sb.append(formatFixed(v, if (precision >= 0) precision else 0))
                    }
                }
                i = j + 1
                continue
            }
        }
        sb.append(c)
        i++
    }
    return sb.toString()
}

/** EN Rounds [value] to [precision] decimal places (half-up) and formats it. */
private fun formatFixed(value: Double, precision: Int): String {
    var factor = 1L
    repeat(precision) { factor *= 10 }
    val scaled = kotlin.math.round(value * factor).toLong()
    val negative = scaled < 0
    val abs = kotlin.math.abs(scaled)
    val whole = (abs / factor).toString()
    val frac = (abs % factor).toString().padStart(precision, '0')
    return (if (negative) "-" else "") + whole + "." + frac
}

/** EN Inserts thousands separators (',') into a decimal integer string. */
private fun groupThousands(s: String): String {
    val sb = StringBuilder()
    var count = 0
    for (i in s.length - 1 downTo 0) {
        sb.append(s[i])
        count++
        if (count % 3 == 0 && i > 0) sb.append(',')
    }
    return sb.reverse().toString()
}

/** EN Formats a Double ns/op value into a readable string with appropriate unit. */
fun Double.formatNs(): String {
    return when {
        this < 1_000.0   -> fmt("%.2f ns", this)
        this < 1_000_000.0 -> fmt("%.1f µs", this / 1_000.0)
        else              -> fmt("%.2f ms", this / 1_000_000.0)
    }
}

/** EN Formats a Long ns value into a readable string with appropriate unit (legacy). */
fun Long.formatNs(): String {
    return when {
        this < 1_000L     -> "$this ns"
        this < 1_000_000L -> fmt("%.1f", this / 1_000.0) + " µs"
        else              -> fmt("%.2f", this / 1_000_000.0) + " ms"
    }
}
