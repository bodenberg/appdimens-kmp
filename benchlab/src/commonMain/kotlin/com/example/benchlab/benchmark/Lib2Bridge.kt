/**
 * EN Bridge isolating the Lib #2 dependency (`network.chaintech:
 *    sdp-ssp-compose-multiplatform`). The artifact publishes Android, JVM,
 *    iOS (arm64/sim/x64), js and wasmJs — but NOT macOS. The `actual` for
 *    macOS replicates Lib #2's exact scaling math (min(w,h)/300 × value) so
 *    the benchlab compiles and runs on every Compose Multiplatform target;
 *    the macOS lane measures the same operation shape (a @Composable read of
 *    the window configuration + one multiply) as the real library.
 * PT Bridge que isola a dependência da Lib #2 (`network.chaintech:
 *    sdp-ssp-compose-multiplatform`). O artefato publica Android, JVM, iOS
 *    (arm64/sim/x64), js e wasmJs — mas NÃO macOS. O `actual` do macOS
 *    replica a matemática exata de escala da Lib #2 (min(w,h)/300 × valor)
 *    para o benchlab compilar e rodar em todos os targets do Compose
 *    Multiplatform; o lane do macOS mede o mesmo formato de operação (leitura
 *    @Composable da configuração da janela + uma multiplicação) da lib real.
 */
package com.example.benchlab.benchmark

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

/**
 * EN Lib #2 `value.sdp` — resolves the scalable Dp inside composition.
 *    Android/JVM/iOS/wasmJs delegate to the real chaintech extension; macOS
 *    replicates its formula (`min(w,h)/300 × value`).
 * PT O `value.sdp` da Lib #2 — resolve o Dp escalável dentro da composição.
 *    Android/JVM/iOS/wasmJs delegam à extensão chaintech real; o macOS
 *    replica a fórmula (`min(w,h)/300 × valor`).
 */
@Composable
internal expect fun lib2SdpDp(value: Int): Dp
