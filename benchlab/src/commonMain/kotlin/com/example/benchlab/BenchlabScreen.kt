/**
 * @author Bodenberg
 *
 * EN Benchmark dashboard (Compose Multiplatform, commonMain): AppDimens
 *    Dynamic KMP vs Lib #2 (`network.chaintech:sdp-ssp-compose-multiplatform`).
 *    Shows:
 *    - NEW methodology: Benchmark A (Compose API, main thread) and
 *      Benchmark B (Engine, off-main — AppDimens only; Lib #2 has no
 *      non-Compose API) with median/min/P90/max stats, order rotation,
 *      anti-DCE checksums and two workloads;
 *    - LEGACY methodology: original T1/T2/T3 tests (resolution values +
 *      time per single call) kept for continuity with previous reports.
 *    The report export is wired per platform via [reportSaver].
 *
 * PT Dashboard de benchmark (Compose Multiplatform, commonMain): AppDimens
 *    Dynamic KMP vs Lib #2 (`network.chaintech:sdp-ssp-compose-multiplatform`).
 *    Exibe:
 *    - Metodologia NOVA: Benchmark A (API Compose, main thread) e
 *      Benchmark B (Motor, off-main — apenas AppDimens; Lib #2 não possui
 *      API não-Compose) com estatísticas mediana/min/P90/max, rotação de
 *      ordem, checksums anti-DCE e dois workloads;
 *    - Metodologia LEGADA: testes originais T1/T2/T3 (valores de resolução +
 *      tempo por chamada única) mantidos por continuidade com relatórios anteriores.
 *    A exportação do relatório é ligada por plataforma via [reportSaver].
 */
package com.example.benchlab

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appdimens.kmp.core.AppDimensContext
import com.appdimens.kmp.core.currentScreenConfiguration
import com.appdimens.kmp.core.localAppDimensContext
import com.example.benchlab.benchmark.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

// ═══════════════════════════════════════════════════════════════════════════════
// COLOUR PALETTE
// ═══════════════════════════════════════════════════════════════════════════════

private val DarkBg        = Color(0xFF0D0F14)
private val SurfaceCard   = Color(0xFF161B24)
private val SurfaceBorder = Color(0xFF252D3D)
private val AccentCyan    = Color(0xFF00E5FF)
private val AccentGreen   = Color(0xFF69FF47)
private val AccentAmber   = Color(0xFFFFD740)
private val TextPrimary   = Color(0xFFECF0F8)
private val TextSecondary = Color(0xFF8A95A8)
private val AccentRed     = Color(0xFFFF5252)

// Library colors
private val ColorAppDimens = AccentCyan
private val ColorLib2      = Color(0xFFB388FF)

// ═══════════════════════════════════════════════════════════════════════════════
// CONTROLLER
// ═══════════════════════════════════════════════════════════════════════════════

private class BenchlabController(
    private val scope: CoroutineScope,
    private val appContext: AppDimensContext,
) {
    private val _phase = MutableStateFlow(BenchPhase.IDLE)
    val phase: StateFlow<BenchPhase> = _phase.asStateFlow()

    private val _result = MutableStateFlow<CompetitorBenchmarkResult?>(null)
    val result: StateFlow<CompetitorBenchmarkResult?> = _result.asStateFlow()

    private val _probeActive = MutableStateFlow(false)
    val probeActive: StateFlow<Boolean> = _probeActive.asStateFlow()

    private val _legacyProbeActive = MutableStateFlow(false)
    val legacyProbeActive: StateFlow<Boolean> = _legacyProbeActive.asStateFlow()

    private var probeDeferred: CompletableDeferred<ComposeProbeResult>? = null
    private var legacyProbeDeferred: CompletableDeferred<Concorrente2ProbeResult>? = null

    // EN True while a run is in flight. Overlapping runs (e.g. the autoStart
    //    LaunchedEffect re-firing while the window config settles) would replace
    //    probeDeferred mid-flight, making the earlier run wait for the full
    //    timeout and cascade. Guarded so a second run() during an active one is
    //    a no-op.
    // PT Verdadeiro enquanto um run está em andamento. Runs sobrepostos (ex.: o
    //    LaunchedEffect do autoStart re-disparando enquanto a janela ajusta)
    //    substituiriam probeDeferred no meio do voo, fazendo o run anterior
    //    esperar o timeout inteiro e cascatear. Com o guard, um segundo run()
    //    durante um ativo é um no-op.
    private var runInFlight = false

    fun run(onScreenConfig: ScreenConfigSnapshot) {
        if (runInFlight) return
        runInFlight = true
        scope.launch {
            reset()
            try {
                benchLog("run(): started")
                // Benchmark A — Compose API (chunked probe, main thread)
                _phase.value = BenchPhase.WARMUP
                val deferred = CompletableDeferred<ComposeProbeResult>()
                probeDeferred = deferred
                _probeActive.value = true
                val compose = withTimeoutOrNull(120_000) { deferred.await() }
                    ?: throw IllegalStateException("Compose probe timed out")

                // Benchmark B — Engine (off main thread; Lib #2 N/A outside composition)
                val core = runCoreEngineBenchmark(appContext) { _phase.value = it }

                // Legacy T1/T2/T3 tests (original methodology)
                val legacyDeferred = CompletableDeferred<Concorrente2ProbeResult>()
                legacyProbeDeferred = legacyDeferred
                _legacyProbeActive.value = true
                val lib2 = withTimeoutOrNull(15_000) { legacyDeferred.await() }
                    ?: throw IllegalStateException("Legacy probe timed out")
                val legacy = runLegacyBenchmark(appContext, lib2) { _phase.value = it }

                _result.value = assembleResult(compose, core, legacy, onScreenConfig.config, onScreenConfig.density)
                _phase.value = BenchPhase.DONE
            } catch (t: Throwable) {
                benchLog("benchmark failed: $t")
                _phase.value = BenchPhase.DONE
            } finally {
                _probeActive.value = false
                _legacyProbeActive.value = false
                probeDeferred = null
                legacyProbeDeferred = null
                runInFlight = false
            }
        }
    }

    fun onComposeMeasured(r: ComposeProbeResult) {
        probeDeferred?.complete(r)
    }

    fun onLegacyMeasured(r: Concorrente2ProbeResult) {
        legacyProbeDeferred?.complete(r)
    }

    private fun reset() {
        _phase.value = BenchPhase.IDLE
        _result.value = null
        _probeActive.value = false
        _legacyProbeActive.value = false
        probeDeferred = null
        legacyProbeDeferred = null
    }
}

/** EN Immutable snapshot of the window config captured at run time. PT Snapshot da janela na hora do run. */
internal data class ScreenConfigSnapshot(
    val config: com.appdimens.kmp.core.ScreenConfiguration,
    val density: Float,
)

// ═══════════════════════════════════════════════════════════════════════════════
// SCREEN (public entry used by every platform)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * EN Compose Multiplatform dashboard. Compose it inside [AppDimensProvider].
 *    [reportSaver] receives the generated report text and returns a user-facing
 *    status message (Android: MediaStore; Web: download; Desktop: file).
 * PT Dashboard Compose Multiplatform. Compose dentro de [AppDimensProvider].
 *    [reportSaver] recebe o relatório gerado e devolve uma mensagem ao usuário
 *    (Android: MediaStore; Web: download; Desktop: arquivo).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BenchlabScreen(
    autoStart: Boolean = false,
    reportSaver: (suspend (String) -> String)? = null,
) {
    val scope = rememberCoroutineScope()
    // EN AppDimens window handle provided by AppDimensProvider (platform actual).
    // PT Handle de janela da AppDimens fornecido pelo AppDimensProvider.
    val appContext = requireNotNull(localAppDimensContext())
    val controller = remember { BenchlabController(scope, appContext) }

    val phase by controller.phase.collectAsState()
    val result by controller.result.collectAsState()
    val probeActive by controller.probeActive.collectAsState()
    val legacyProbeActive by controller.legacyProbeActive.collectAsState()
    val isRunning = phase != BenchPhase.IDLE && phase != BenchPhase.DONE

    // EN Captured once per composition (recomposes on window/config change).
    // PT Capturado uma vez por composição (recompõe em mudança de janela/config).
    val screenConfig = captureScreenConfig()

    // EN Headless automation: run the benchmark as soon as the screen is composed
    //    WITH a real window size. On desktop/web the first frame's LocalWindowInfo
    //    can still report 0×0 (before layout), which would make the whole run
    //    measure the DEFAULT fallback config (sw=0). Keying on the dp size makes
    //    the effect re-fire once the window has real dimensions.
    // PT Automação headless: executa o benchmark assim que a tela é composta COM
    //    tamanho real de janela. No desktop/web o primeiro frame do
    //    LocalWindowInfo ainda pode reportar 0×0 (antes do layout), o que faria a
    //    execução medir a config DEFAULT de fallback (sw=0). Chavear pelo tamanho
    //    em dp faz o efeito disparar de novo quando a janela tiver dimensões reais.
    LaunchedEffect(screenConfig.config.screenWidthDp, screenConfig.config.screenHeightDp) {
        if (autoStart && screenConfig.config.screenWidthDp > 0 && screenConfig.config.screenHeightDp > 0) {
            controller.run(screenConfig)
        }
    }

    var exportMessage by remember { mutableStateOf<String?>(null) }

    // EN Headless automation: when autoStart finishes the run, auto-export the
    //    report exactly once (the same path as the manual “Export Report” button)
    //    so CI / scripts get the .txt without a human click. Guarded by
    //    [exportMessage] being still null so recompositions cannot re-export.
    // PT Automação headless: quando o autoStart conclui o run, exporta o relatório
    //    automaticamente uma única vez (mesmo caminho do botão “Export Report”)
    //    para CI / scripts receberem o .txt sem clique manual.
    LaunchedEffect(phase, result) {
        if (autoStart && phase == BenchPhase.DONE && result != null && exportMessage == null) {
            val saver = reportSaver
            if (saver != null) {
                exportMessage = try {
                    saver(generateReport(result!!))
                } catch (e: Exception) {
                    benchLog("Auto-export failed: $e")
                    "Erro ao exportar relatório"
                }
            }
        }
    }

    // EN New-methodology 2-way probe (chunked, main thread).
    // PT Sonda 2-vias da metodologia nova (fatiada, main thread).
    ComposeCompetitorProbe(
        active = probeActive,
        onResult = controller::onComposeMeasured,
    )

    // EN Legacy Lib #2 probe (used by the T1/T2/T3 tests).
    // PT Sonda legada da Lib #2 (usada pelos testes T1/T2/T3).
    Concorrente2Probe(
        active = legacyProbeActive,
        onResult = controller::onLegacyMeasured,
    )

    MaterialTheme(
        colorScheme = darkColorScheme().copy(
            background = DarkBg,
            surface = SurfaceCard,
            primary = AccentCyan,
        )
    ) {
        Scaffold(
            containerColor = DarkBg,
            topBar = {
                TopAppBar(
                    title = { Text("BenchLab", fontWeight = FontWeight.Bold, color = TextPrimary) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceCard)
                )
            }
        ) { innerPadding ->
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // ── Action Buttons ─────────────────────────────────────────
                item {
                    ActionPanel(
                        isRunning = isRunning,
                        hasResult = result != null,
                        onRun = { controller.run(screenConfig) },
                        onExportReport = {
                            val r = result ?: return@ActionPanel
                            val saver = reportSaver
                            if (saver == null) {
                                exportMessage = "Exportação não disponível nesta plataforma"
                                return@ActionPanel
                            }
                            scope.launch {
                                try {
                                    exportMessage = saver(generateReport(r))
                                } catch (e: Exception) {
                                    benchLog("Export failed: $e")
                                    exportMessage = "Erro ao exportar relatório"
                                }
                            }
                        }
                    )
                }

                // ── Export status ─────────────────────────────────────────
                exportMessage?.let { msg ->
                    item {
                        DashboardCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                            Text(msg, color = AccentGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                // ── Status ──────────────────────────────────────────────────
                item { StatusPanel(phase = phase, isRunning = isRunning) }

                // ── Methodology explanation ────────────────────────────────
                item { MethodologyCard() }

                // ── NEW: Benchmark A — Compose API ─────────────────────────
                item { ComposeApiSection(result = result) }

                // ── NEW: Benchmark B — Engine ───────────────────────────────
                item { CoreEngineSection(result = result) }

                // ── NEW: Ratio banners (medians) ────────────────────────────
                item { RatioSection(result = result) }

                // ── NEW: Anti-DCE checksums ─────────────────────────────────
                item { ChecksumSection(result = result) }

                // ── LEGACY: group divider ──────────────────────────────────
                item {
                    SectionHeader(
                        icon = "📜",
                        label = "Testes legados T1–T3 (metodologia original)",
                        color = TextSecondary,
                        caption = "Execuções originais preservadas para continuidade com relatórios anteriores (média de 3 testes, tempo por chamada única)."
                    )
                }

                // ── LEGACY: Dp Resolution (sdp — no AR) ───────────────────
                item { LegacyDpResolutionSection(result = result, withAr = false) }

                // ── LEGACY: Dp Resolution (sdpa — with AR) ─────────────────
                item { LegacyDpResolutionSection(result = result, withAr = true) }

                // ── LEGACY: Timing table ────────────────────────────────────
                item { LegacyTimingSection(result = result) }

                // ── LEGACY: Average banner ──────────────────────────────────
                item { LegacyAverageBanner(result = result) }

                // ── Device Info ─────────────────────────────────────────────
                item { DeviceInfo(result = result) }
            }
        }
    }
}

/** EN Captures the current window configuration snapshot for the result header. */
@Composable
private fun captureScreenConfig(): ScreenConfigSnapshot {
    val config = currentScreenConfiguration()
    return ScreenConfigSnapshot(
        config = config,
        density = if (config.densityDpi > 0) config.densityDpi / 160f else 1f,
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// ACTION PANEL
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ActionPanel(
    isRunning: Boolean,
    hasResult: Boolean,
    onRun: () -> Unit,
    onExportReport: () -> Unit,
) {
    DashboardCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            "Comparativo de 2 bibliotecas",
            color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            "Dynamic KMP × Lib #2 (sdp-ssp-compose-multiplatform) — 2 benchmarks + testes legados",
            color = TextSecondary, fontSize = 11.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Button(
            onClick = onRun,
            enabled = !isRunning,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentCyan.copy(alpha = 0.18f),
                disabledContainerColor = AccentCyan.copy(alpha = 0.06f),
                contentColor = AccentCyan,
            ),
            border = BorderStroke(1.dp, AccentCyan.copy(alpha = 0.6f))
        ) {
            Text("▶", fontSize = 16.sp)
            Spacer(Modifier.width(6.dp))
            Text("Rodar benchmark completo", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onExportReport,
            enabled = hasResult,
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = AccentAmber,
                disabledContentColor = AccentAmber.copy(alpha = 0.3f)
            ),
            border = BorderStroke(1.dp, AccentAmber.copy(alpha = if (hasResult) 0.6f else 0.15f))
        ) {
            Text("📄 Exportar relatório", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// STATUS PANEL
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun StatusPanel(phase: BenchPhase, isRunning: Boolean) {
    val progress by animateFloatAsState(
        targetValue = phase.progressFraction,
        animationSpec = tween(durationMillis = 600),
        label = "progress"
    )
    DashboardCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        when {
                            phase == BenchPhase.DONE -> AccentGreen
                            isRunning -> AccentCyan
                            else -> TextSecondary
                        }
                    )
            )
            Spacer(Modifier.width(10.dp))
            AnimatedContent(
                targetState = phase.displayLabel,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                label = "phaseLabel"
            ) { label ->
                Text(label, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = if (phase == BenchPhase.DONE) AccentGreen else AccentCyan,
            trackColor = SurfaceBorder
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// METHODOLOGY EXPLANATION
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun MethodologyCard() {
    DashboardCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text("Como ler estes resultados", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(
            "Benchmark A — API Compose (main thread): as 2 bibliotecas rodam no mesmo composable, " +
                "com warm-up idêntico (20.000), 9 amostras de 50.000 iterações, ordem rotacionada e " +
                "checksum anti-DCE em todos os loops. A medição é fatiada em 5.000 ops/frame para a " +
                "UI não congelar; o tempo por fatia exclui os gaps entre frames.",
            color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Mediana = custo steady-state amortizado (ns/op). Interrupções isoladas do scheduler/GC " +
                "não afetam a mediana — por isso ela é o número principal; min/P90/max mostram a dispersão.",
            color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Benchmark B — Motor (Dispatchers.Default): apenas AppDimens KMP fora da UI. " +
                "Lib #2 não possui API não-Compose (a extensão .sdp é @Composable) → N/A.",
            color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Workloads: constant 1dp (call site quente) e mixed values (12 dimensões pré-definidas, " +
                "espelha uma tela real). Testes legados T1–T3: metodologia original, mantida por continuidade.",
            color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// BENCHMARK A — COMPOSE API (main thread)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ComposeApiSection(result: CompetitorBenchmarkResult?) {
    SectionHeader(
        icon = "🧩",
        label = "Benchmark A — API Compose (main thread)",
        color = AccentCyan,
        caption = "As 2 bibliotecas no mesmo composable · warm-up 20.000 idêntico · 9 amostras × 50.000 · ordem rotacionada · mediana como número principal"
    )

    DashboardCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        AnimatedVisibility(visible = result == null) {
            Text("Medição das 2 bibliotecas no mesmo composable (ordem rotacionada).", color = TextSecondary, fontSize = 12.sp)
        }
        AnimatedVisibility(visible = result != null) {
            result?.let { r ->
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    WorkloadBlock(
                        title = "Constant 1dp — hot",
                        caption = "50.000 resoluções repetidas do mesmo valor (teto absoluto)",
                        items = listOf(
                            Triple("Dynamic KMP", ColorAppDimens, r.composeApi.dynamic.constant1dp),
                            Triple("Lib #2", ColorLib2, r.composeApi.chaintech.constant1dp),
                        )
                    )
                    WorkloadBlock(
                        title = "Mixed values (12 dimensões)",
                        caption = "Valores 1–100dp pré-definidos, espelha uma tela real",
                        items = listOf(
                            Triple("Dynamic KMP", ColorAppDimens, r.composeApi.dynamic.mixedValues),
                            Triple("Lib #2", ColorLib2, r.composeApi.chaintech.mixedValues),
                        )
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// BENCHMARK B — ENGINE (off main thread)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun CoreEngineSection(result: CompetitorBenchmarkResult?) {
    SectionHeader(
        icon = "⚙️",
        label = "Benchmark B — Motor (Dispatchers.Default)",
        color = AccentAmber,
        caption = "AppDimens KMP fora da composição · mesma metodologia (9 × 50.000, anti-DCE) · Lib #2: N/A — API exige Composition"
    )

    DashboardCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        AnimatedVisibility(visible = result == null) {
            Text("Apenas AppDimens KMP fora da composição. Lib #2: N/A (exige Composition).", color = TextSecondary, fontSize = 12.sp)
        }
        AnimatedVisibility(visible = result != null) {
            result?.let { r ->
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    WorkloadBlock(
                        title = "Constant 1dp — hot",
                        caption = "50.000 resoluções repetidas do mesmo valor (teto absoluto)",
                        items = listOf(
                            Triple("Dynamic KMP", ColorAppDimens, r.coreEngine.dynamic.constant1dp),
                        )
                    )
                    WorkloadBlock(
                        title = "Mixed values (12 dimensões)",
                        caption = "Valores 1–100dp pré-definidos, espelha uma tela real",
                        items = listOf(
                            Triple("Dynamic KMP", ColorAppDimens, r.coreEngine.dynamic.mixedValues),
                        )
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// WORKLOAD BLOCKS (stats cards)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun WorkloadBlock(
    title: String,
    caption: String,
    items: List<Triple<String, Color, TimingStats>>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceBorder.copy(alpha = 0.15f))
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(caption, color = TextSecondary, fontSize = 9.sp)
        Spacer(Modifier.height(8.dp))
        items.forEachIndexed { index, (name, color, stats) ->
            if (index > 0) {
                HorizontalDivider(color = SurfaceBorder.copy(alpha = 0.5f))
                Spacer(Modifier.height(6.dp))
            }
            LibStatsCard(name = name, color = color, stats = stats)
        }
    }
}

@Composable
private fun LibStatsCard(name: String, color: Color, stats: TimingStats) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.05f))
            .border(1.dp, color.copy(alpha = 0.18f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(name, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Spacer(Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCell(label = "Mediana", value = stats.medianNs.formatNs(), color = TextPrimary, isHighlight = true, modifier = Modifier.weight(1f))
            StatCell(label = "Min", value = stats.minNs.formatNs(), color = TextSecondary, modifier = Modifier.weight(1f))
            StatCell(label = "P90", value = stats.p90Ns.formatNs(), color = TextSecondary, modifier = Modifier.weight(1f))
            StatCell(label = "Max", value = stats.maxNs.formatNs(), color = TextSecondary, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCell(label: String, value: String, color: Color, modifier: Modifier, isHighlight: Boolean = false) {
    Column(modifier = modifier) {
        Text(label, color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
        Text(value, color = if (isHighlight) AccentGreen else color, fontSize = 11.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
            fontFamily = FontFamily.Monospace)
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// RATIO BANNERS (based on new-methodology medians)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun RatioSection(result: CompetitorBenchmarkResult?) {
    SectionHeader(
        icon = "⚡",
        label = "Comparativo (mediana dos novos benchmarks)",
        color = AccentGreen,
        caption = "Razões calculadas sobre as medianas — não sobre médias"
    )

    result?.let { r ->
        DashboardCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Text("Benchmark A — API Compose", color = AccentCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            DynamicRatioLine(r.composeApi.dynamic.constant1dp.medianNs, r.composeApi.chaintech.constant1dp.medianNs, "Lib #2", "Compose · constant 1dp")
            Spacer(Modifier.height(4.dp))
            DynamicRatioLine(r.composeApi.dynamic.mixedValues.medianNs, r.composeApi.chaintech.mixedValues.medianNs, "Lib #2", "Compose · mixed values")

            Spacer(Modifier.height(8.dp))

            Text("Benchmark B — Motor (apenas AppDimens KMP)", color = AccentAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                "Lib #2 não possui API não-Compose — sem concorrente fora da composição.",
                color = TextSecondary, fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun DynamicRatioLine(dynamicMedian: Double, otherMedian: Double, otherName: String, context: String) {
    if (dynamicMedian <= 0.0 || otherMedian <= 0.0) return
    val faster = dynamicMedian < otherMedian
    val ratio = if (faster) otherMedian / dynamicMedian else dynamicMedian / otherMedian
    val text = if (faster) {
        fmt("Dynamic é ×%.1f mais rápido que %s (%s)", ratio, otherName, context)
    } else {
        fmt("Dynamic é ×%.1f mais lento que %s (%s)", ratio, otherName, context)
    }
    RatioBanner(
        emoji = if (faster) "⚡" else "🐢",
        text = text,
        color = if (faster) AccentGreen else AccentRed
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// ANTI-DCE CHECKSUMS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ChecksumSection(result: CompetitorBenchmarkResult?) {
    SectionHeader(
        icon = "🔢",
        label = "Anti-DCE checksums",
        color = AccentGreen,
        caption = "Soma acumulada das resoluções nos loops cronometrados — prova de que as chamadas foram executadas e consumidas. Dynamic soma px; Lib #2 soma dp (.value)."
    )

    DashboardCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        AnimatedVisibility(visible = result == null) {
            Text("Checksum dos acumuladores (prova que as chamadas foram executadas).", color = TextSecondary, fontSize = 12.sp)
        }
        AnimatedVisibility(visible = result != null) {
            result?.let { r ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ChecksumRow("Compose · Dynamic KMP", r.composeApi.dynamic, ColorAppDimens)
                    ChecksumRow("Compose · Lib #2", r.composeApi.chaintech, ColorLib2)
                    HorizontalDivider(color = SurfaceBorder)
                    ChecksumRow("Motor · Dynamic KMP", r.coreEngine.dynamic, ColorAppDimens)
                }
            }
        }
    }
}

@Composable
private fun ChecksumRow(label: String, lib: LibraryTiming, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.45f))
        Text(fmt("1dp: %.1f", lib.constantChecksum), color = TextSecondary, fontSize = 10.sp,
            fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.28f))
        Text(fmt("mix: %.1f", lib.mixedChecksum), color = TextSecondary, fontSize = 10.sp,
            fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.27f))
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// LEGACY: DP RESOLUTION TABLE (T1/T2/T3)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun LegacyDpResolutionSection(result: CompetitorBenchmarkResult?, withAr: Boolean) {
    val label = if (withAr) "Valores de resolução sdpa (com AR → px)" else "Valores de resolução sdp (sem AR → px)"
    val color = if (withAr) AccentAmber else AccentGreen
    SectionHeader(icon = if (withAr) "📐AR" else "📐", label = label, color = color)

    DashboardCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        AnimatedVisibility(visible = result == null) {
            Text("Clique em Rodar para ver os valores.", color = TextSecondary, fontSize = 12.sp)
        }
        AnimatedVisibility(visible = result != null) {
            result?.let { r ->
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (!withAr) {
                        LibBlock(
                            libName = "Dynamic KMP",
                            libColor = ColorAppDimens,
                            dp1T1 = r.legacy.test1.dp1AppDimens, dp1T2 = r.legacy.test2.dp1AppDimens, dp1T3 = r.legacy.test3.dp1AppDimens,
                            dp10T1 = r.legacy.test1.dp10AppDimens, dp10T2 = r.legacy.test2.dp10AppDimens, dp10T3 = r.legacy.test3.dp10AppDimens,
                            dp100T1 = r.legacy.test1.dp100AppDimens, dp100T2 = r.legacy.test2.dp100AppDimens, dp100T3 = r.legacy.test3.dp100AppDimens,
                        )
                        HorizontalDivider(color = SurfaceBorder)
                        LibBlock(
                            libName = "Lib #2",
                            libColor = ColorLib2,
                            dp1T1 = r.legacy.test1.dp1Lib2, dp1T2 = r.legacy.test2.dp1Lib2, dp1T3 = r.legacy.test3.dp1Lib2,
                            dp10T1 = r.legacy.test1.dp10Lib2, dp10T2 = r.legacy.test2.dp10Lib2, dp10T3 = r.legacy.test3.dp10Lib2,
                            dp100T1 = r.legacy.test1.dp100Lib2, dp100T2 = r.legacy.test2.dp100Lib2, dp100T3 = r.legacy.test3.dp100Lib2,
                        )
                    } else {
                        LibBlock(
                            libName = "Dynamic KMP (AR)",
                            libColor = ColorAppDimens,
                            dp1T1 = r.legacy.test1.dp1AppDimensAr, dp1T2 = r.legacy.test2.dp1AppDimensAr, dp1T3 = r.legacy.test3.dp1AppDimensAr,
                            dp10T1 = r.legacy.test1.dp10AppDimensAr, dp10T2 = r.legacy.test2.dp10AppDimensAr, dp10T3 = r.legacy.test3.dp10AppDimensAr,
                            dp100T1 = r.legacy.test1.dp100AppDimensAr, dp100T2 = r.legacy.test2.dp100AppDimensAr, dp100T3 = r.legacy.test3.dp100AppDimensAr,
                        )
                        Text(
                            "Lib #2 não suporta sdpa (aspect ratio).",
                            color = TextSecondary, fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LibBlock(
    libName: String,
    libColor: Color,
    dp1T1: Float, dp1T2: Float, dp1T3: Float,
    dp10T1: Float, dp10T2: Float, dp10T3: Float,
    dp100T1: Float, dp100T2: Float, dp100T3: Float,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(libColor.copy(alpha = 0.05f))
            .border(1.dp, libColor.copy(alpha = 0.18f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            libName,
            color = libColor, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(Modifier.height(6.dp))
        DpRow3("1dp", dp1T1, dp1T2, dp1T3, libColor)
        Spacer(Modifier.height(4.dp))
        DpRow3("10dp", dp10T1, dp10T2, dp10T3, libColor)
        Spacer(Modifier.height(4.dp))
        DpRow3("100dp", dp100T1, dp100T2, dp100T3, libColor)
    }
}

@Composable
private fun DpRow3(
    dpLabel: String,
    t1: Float, t2: Float, t3: Float,
    libColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            dpLabel,
            color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(0.13f)
        )
        Column(modifier = Modifier.weight(0.29f)) {
            Text("T1", color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
            Text(fmt("%.4f px", t1), color = libColor, fontSize = 10.sp,
                fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace)
        }
        Column(modifier = Modifier.weight(0.29f)) {
            Text("T2", color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
            Text(fmt("%.4f px", t2), color = libColor, fontSize = 10.sp,
                fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace)
        }
        Column(modifier = Modifier.weight(0.29f)) {
            Text("T3", color = TextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
            Text(fmt("%.4f px", t3), color = libColor, fontSize = 10.sp,
                fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// LEGACY: TIMING TABLE (T1/T2/T3 + Média)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun LegacyTimingSection(result: CompetitorBenchmarkResult?) {
    SectionHeader(icon = "⏱️", label = "Tempo por chamada de 1dp (legado — sdp + sdpa/AR)", color = AccentAmber)

    DashboardCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        AnimatedVisibility(visible = result == null) {
            Text("Tempo por chamada aparece após o teste.", color = TextSecondary, fontSize = 12.sp)
        }
        AnimatedVisibility(visible = result != null) {
            result?.let { r ->
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("sdp (sem AR)", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    TableHeader(columns = listOf("Teste" to 0.2f, "Dynamic" to 0.4f, "Lib #2" to 0.4f))
                    HorizontalDivider(color = SurfaceBorder)
                    LegacyTimingRow("T1", r.legacy.timeTest1.appDimensNs, r.legacy.timeTest1.lib2Ns, AccentCyan.copy(alpha = 0.04f))
                    HorizontalDivider(color = SurfaceBorder.copy(alpha = 0.5f))
                    LegacyTimingRow("T2", r.legacy.timeTest2.appDimensNs, r.legacy.timeTest2.lib2Ns, Color.Transparent)
                    HorizontalDivider(color = SurfaceBorder.copy(alpha = 0.5f))
                    LegacyTimingRow("T3", r.legacy.timeTest3.appDimensNs, r.legacy.timeTest3.lib2Ns, Color.Transparent)
                    HorizontalDivider(color = SurfaceBorder)
                    LegacyTimingRow("Média", r.legacy.avgAppDimensNs, r.legacy.avgLib2Ns, Color.Transparent, isHighlight = true)

                    Spacer(Modifier.height(8.dp))

                    Text("sdpa (com AR — apenas Dynamic)", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    TableHeader(columns = listOf("Teste" to 0.2f, "Dynamic" to 0.8f))
                    HorizontalDivider(color = SurfaceBorder)
                    LegacyTimingRow2("T1", r.legacy.timeTest1.appDimensArNs, AccentAmber.copy(alpha = 0.04f))
                    HorizontalDivider(color = SurfaceBorder.copy(alpha = 0.5f))
                    LegacyTimingRow2("T2", r.legacy.timeTest2.appDimensArNs, Color.Transparent)
                    HorizontalDivider(color = SurfaceBorder.copy(alpha = 0.5f))
                    LegacyTimingRow2("T3", r.legacy.timeTest3.appDimensArNs, Color.Transparent)
                    HorizontalDivider(color = SurfaceBorder)
                    LegacyTimingRow2("Média", r.legacy.avgAppDimensArNs, Color.Transparent, isHighlight = true)
                }
            }
        }
    }
}

@Composable
private fun LegacyTimingRow(
    label: String,
    appNs: Long, lib2Ns: Long,
    rowColor: Color,
    isHighlight: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowColor)
            .padding(horizontal = 10.dp, vertical = if (isHighlight) 6.dp else 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = if (isHighlight) AccentGreen else TextSecondary,
            fontSize = if (isHighlight) 11.sp else 10.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
            fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.2f))
        Text(appNs.formatNs(), color = ColorAppDimens, fontSize = 11.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
            fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.4f))
        Text(lib2Ns.formatNs(), color = ColorLib2, fontSize = 11.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
            fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.4f))
    }
}

@Composable
private fun LegacyTimingRow2(
    label: String,
    appNs: Long,
    rowColor: Color,
    isHighlight: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowColor)
            .padding(horizontal = 10.dp, vertical = if (isHighlight) 6.dp else 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = if (isHighlight) AccentGreen else TextSecondary,
            fontSize = if (isHighlight) 11.sp else 10.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
            fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.2f))
        Text(appNs.formatNs(), color = ColorAppDimens, fontSize = 11.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium,
            fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.8f))
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// LEGACY: AVERAGE BANNER (based on legacy averages)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun LegacyAverageBanner(result: CompetitorBenchmarkResult?) {
    result?.let { r ->
        DashboardCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Text("Comparativo legado (média T1–T3)", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))

            Text("sdp (sem AR)", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))

            val ratioVsLib2 = if (r.legacy.avgAppDimensNs > 0) r.legacy.avgLib2Ns.toFloat() / r.legacy.avgAppDimensNs.toFloat() else 1f
            val fasterVsLib2 = r.legacy.avgAppDimensNs < r.legacy.avgLib2Ns
            RatioBanner(
                emoji = if (fasterVsLib2) "🚀" else "🐢",
                text = if (fasterVsLib2) fmt("Dynamic é ×%.1f mais rápido que Lib #2", ratioVsLib2)
                       else fmt("Dynamic é ×%.1f mais lento que Lib #2", 1f / ratioVsLib2),
                color = if (fasterVsLib2) AccentGreen else AccentRed
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// DEVICE INFO
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun DeviceInfo(result: CompetitorBenchmarkResult?) {
    result?.let { r ->
        DashboardCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            Text(
                fmt("sw=${r.windowSw}dp  w=${r.windowW}dp  h=${r.windowH}dp  density=%.2f", r.density),
                color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SHARED COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SectionHeader(icon: String, label: String, color: Color, caption: String? = null) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 16.sp)
            Spacer(Modifier.width(8.dp))
            Text(label, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        if (caption != null) {
            Spacer(Modifier.height(3.dp))
            Text(caption, color = TextSecondary, fontSize = 10.sp, lineHeight = 14.sp)
        }
    }
}

@Composable
private fun DashboardCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun TableHeader(columns: List<Pair<String, Float>>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        columns.forEach { (text, weight) ->
            Text(
                text, color = TextSecondary, fontSize = 10.sp,
                fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(weight)
            )
        }
    }
}

@Composable
private fun RatioBanner(emoji: String, text: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.10f))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 12.sp)
        Spacer(Modifier.width(6.dp))
        Text(
            text, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// REPORT GENERATION
// ═══════════════════════════════════════════════════════════════════════════════

internal fun generateReport(r: CompetitorBenchmarkResult): String {
    data class RatioLine(val label: String, val dynamicNs: Double, val otherNs: Double, val otherName: String)

    val sb = StringBuilder()
    sb.appendLine("═══════════════════════════════════════════════════════")
    sb.appendLine("  BenchLab — Relatório de Benchmark")
    sb.appendLine("  Dynamic KMP × Lib #2 (sdp-ssp-compose-multiplatform)")
    sb.appendLine("  Benchmark A (Compose API) + Benchmark B (Motor) + Testes legados T1-T3")
    sb.appendLine("═══════════════════════════════════════════════════════")
    sb.appendLine()
    sb.appendLine("Dispositivo: sw=${r.windowSw}dp w=${r.windowW}dp h=${r.windowH}dp density=${fmt("%.2f", r.density)}")
    sb.appendLine()
    sb.appendLine("Metodologia (novos benchmarks):")
    sb.appendLine("  Benchmark A — API Compose (main thread): as 2 bibliotecas no mesmo composable")
    sb.appendLine("  Benchmark B — Motor (Dispatchers.Default): apenas AppDimens KMP (Lib #2 N/A — API exige Composition)")
    sb.appendLine("  Warm-up idêntico: ${fmt("%,d", BENCH_WARMUP_COUNT).replace(',', '.')} resoluções de 1dp por biblioteca")
    sb.appendLine("  Amostras: $BENCH_SAMPLE_COUNT por workload")
    sb.appendLine("  Iterações/amostra: ${fmt("%,d", BENCH_MEASURE_COUNT).replace(',', '.')}")
    sb.appendLine("  Fatiamento: ${fmt("%,d", BENCH_CHUNK_OPS).replace(',', '.')} ops/frame (UI responsiva; tempo por fatia exclui gaps)")
    sb.appendLine("  Anti-DCE: acumulador de checksum em todos os loops cronometrados")
    sb.appendLine("  Rotação de ordem: Dynamic → Lib #2 / Lib #2 → Dynamic")
    sb.appendLine("  Número principal: mediana (ns/op steady-state amortizado); min/P90/max = dispersão")
    sb.appendLine("  Workloads: constant 1dp (call site quente) + mixed values (12 dimensões pré-definidas)")
    sb.appendLine("  Testes legados T1-T3: metodologia original (média de 3 execuções), por continuidade")
    sb.appendLine()

    sb.appendLine("── Benchmark A — API Compose (main thread) ──")
    sb.appendLine()
    for ((name, lib) in listOf("Dynamic KMP" to r.composeApi.dynamic, "Lib #2" to r.composeApi.chaintech)) {
        sb.appendLine("  $name:")
        sb.appendLine("    Constant 1dp: mediana=${lib.constant1dp.medianNs.formatNs()}  min=${lib.constant1dp.minNs.formatNs()}  P90=${lib.constant1dp.p90Ns.formatNs()}  max=${lib.constant1dp.maxNs.formatNs()}")
        sb.appendLine("    Mixed values: mediana=${lib.mixedValues.medianNs.formatNs()}  min=${lib.mixedValues.minNs.formatNs()}  P90=${lib.mixedValues.p90Ns.formatNs()}  max=${lib.mixedValues.maxNs.formatNs()}")
        sb.appendLine()
    }

    sb.appendLine("── Benchmark B — Motor (Dispatchers.Default) ──")
    sb.appendLine("  Lib #2: N/A — API exige Composition")
    sb.appendLine()
    val dyn = r.coreEngine.dynamic
    sb.appendLine("  Dynamic KMP:")
    sb.appendLine("    Constant 1dp: mediana=${dyn.constant1dp.medianNs.formatNs()}  min=${dyn.constant1dp.minNs.formatNs()}  P90=${dyn.constant1dp.p90Ns.formatNs()}  max=${dyn.constant1dp.maxNs.formatNs()}")
    sb.appendLine("    Mixed values: mediana=${dyn.mixedValues.medianNs.formatNs()}  min=${dyn.mixedValues.minNs.formatNs()}  P90=${dyn.mixedValues.p90Ns.formatNs()}  max=${dyn.mixedValues.maxNs.formatNs()}")
    sb.appendLine()

    sb.appendLine("── Anti-DCE checksums ──")
    sb.appendLine("  Compose:  Dynamic=${fmt("%.1f", r.composeApi.dynamic.constantChecksum)}/${fmt("%.1f", r.composeApi.dynamic.mixedChecksum)}  Lib #2=${fmt("%.1f", r.composeApi.chaintech.constantChecksum)}/${fmt("%.1f", r.composeApi.chaintech.mixedChecksum)}")
    sb.appendLine("  Motor:    Dynamic=${fmt("%.1f", dyn.constantChecksum)}/${fmt("%.1f", dyn.mixedChecksum)}")
    sb.appendLine()

    sb.appendLine("── Comparativo (mediana dos novos benchmarks) ──")
    val comparisons = listOf(
        RatioLine("Compose · constant 1dp", r.composeApi.dynamic.constant1dp.medianNs, r.composeApi.chaintech.constant1dp.medianNs, "Lib #2"),
        RatioLine("Compose · mixed values", r.composeApi.dynamic.mixedValues.medianNs, r.composeApi.chaintech.mixedValues.medianNs, "Lib #2"),
    )
    for ((label, dynNs, other, name) in comparisons) {
        if (dynNs > 0.0 && other > 0.0) {
            val ratio = if (dynNs < other) other / dynNs else dynNs / other
            val verdict = if (dynNs < other) "mais rápido" else "mais lento"
            sb.appendLine("  Dynamic vs $name ($label): ×${fmt("%.1f", ratio)} $verdict")
        }
    }
    sb.appendLine()

    sb.appendLine("── Testes legados T1–T3 (metodologia original) ──")
    sb.appendLine()
    sb.appendLine("  Valores de resolução (sdp):")
    val sdpGetters = listOf(
        "Dynamic KMP" to Triple(
            { t: DpResolution2 -> t.dp1AppDimens }, { t: DpResolution2 -> t.dp10AppDimens }, { t: DpResolution2 -> t.dp100AppDimens }),
        "Lib #2" to Triple(
            { t: DpResolution2 -> t.dp1Lib2 }, { t: DpResolution2 -> t.dp10Lib2 }, { t: DpResolution2 -> t.dp100Lib2 }),
    )
    for ((name, getters) in sdpGetters) {
        val (get1, get10, get100) = getters
        sb.appendLine(fmt("    $name: 1dp T1=%.4f T2=%.4f T3=%.4f | 10dp T1=%.4f T2=%.4f T3=%.4f | 100dp T1=%.4f T2=%.4f T3=%.4f",
            get1(r.legacy.test1), get1(r.legacy.test2), get1(r.legacy.test3),
            get10(r.legacy.test1), get10(r.legacy.test2), get10(r.legacy.test3),
            get100(r.legacy.test1), get100(r.legacy.test2), get100(r.legacy.test3)))
    }
    sb.appendLine()
    sb.appendLine("  Valores de resolução (sdpa/AR — Lib #2 não suporta):")
    sb.appendLine(fmt("    Dynamic KMP: 1dp T1=%.4f T2=%.4f T3=%.4f | 10dp T1=%.4f T2=%.4f T3=%.4f | 100dp T1=%.4f T2=%.4f T3=%.4f",
        r.legacy.test1.dp1AppDimensAr, r.legacy.test2.dp1AppDimensAr, r.legacy.test3.dp1AppDimensAr,
        r.legacy.test1.dp10AppDimensAr, r.legacy.test2.dp10AppDimensAr, r.legacy.test3.dp10AppDimensAr,
        r.legacy.test1.dp100AppDimensAr, r.legacy.test2.dp100AppDimensAr, r.legacy.test3.dp100AppDimensAr))
    sb.appendLine()
    sb.appendLine("  Tempo por chamada de 1dp (sdp):")
    sb.appendLine("    Dynamic:  T1=${r.legacy.timeTest1.appDimensNs.formatNs()}  T2=${r.legacy.timeTest2.appDimensNs.formatNs()}  T3=${r.legacy.timeTest3.appDimensNs.formatNs()}  Média=${r.legacy.avgAppDimensNs.formatNs()}")
    sb.appendLine("    Lib #2: T1=${r.legacy.timeTest1.lib2Ns.formatNs()}  T2=${r.legacy.timeTest2.lib2Ns.formatNs()}  T3=${r.legacy.timeTest3.lib2Ns.formatNs()}  Média=${r.legacy.avgLib2Ns.formatNs()}")
    sb.appendLine()
    sb.appendLine("  Tempo por chamada de 1dp (sdpa/AR):")
    sb.appendLine("    Dynamic:  T1=${r.legacy.timeTest1.appDimensArNs.formatNs()}  T2=${r.legacy.timeTest2.appDimensArNs.formatNs()}  T3=${r.legacy.timeTest3.appDimensArNs.formatNs()}  Média=${r.legacy.avgAppDimensArNs.formatNs()}")
    sb.appendLine()
    sb.appendLine("  Comparativo legado (média T1–T3):")
    if (r.legacy.avgAppDimensNs > 0 && r.legacy.avgLib2Ns > 0) {
        val ratioChain = r.legacy.avgLib2Ns.toFloat() / r.legacy.avgAppDimensNs.toFloat()
        sb.appendLine("    Dynamic vs Lib #2 (sdp): ×${fmt("%.1f", ratioChain)} ${if (ratioChain > 1) "mais rápido" else "mais lento"}")
    }

    sb.appendLine()
    sb.appendLine("═══════════════════════════════════════════════════════")
    sb.appendLine("  Gerado por BenchLab (Compose Multiplatform)")
    sb.appendLine("═══════════════════════════════════════════════════════")

    return sb.toString()
}
