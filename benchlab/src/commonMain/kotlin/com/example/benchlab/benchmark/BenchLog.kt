/**
 * EN Minimal multiplatform logger — prints to stdout (logcat on Android,
 *    console on Wasm, terminal on Desktop). Keeps the benchmark core free of
 *    platform APIs.
 * PT Logger multiplataforma mínimo — imprime em stdout (logcat no Android,
 *    console no Wasm, terminal no Desktop). Mantém o núcleo do benchmark livre
 *    de APIs de plataforma.
 */
package com.example.benchlab.benchmark

import kotlin.time.TimeSource

internal fun benchLog(message: String) {
    println("BENCHLAB: $message")
}

/**
 * EN Monotonic nanosecond clock available on EVERY target (wasmJs/JS and
 *    native have no `java.lang.System`). Backed by `TimeSource.Monotonic`,
 *    which maps to `System.nanoTime()` on JVM/Android and to the platform
 *    monotonic clock on wasmJs/native. Only differences are meaningful, and
 *    the origin is captured once so two readings are comparable.
 * PT Relógio monotônico em nanossegundos disponível em TODOS os targets
 *    (wasmJs/JS e native não têm `java.lang.System`). Suportado por
 *    `TimeSource.Monotonic`, que mapeia para `System.nanoTime()` em
 *    JVM/Android e para o relógio monotônico da plataforma em wasmJs/native.
 *    Apenas diferenças são significativas; a origem é capturada uma vez para
 *    que duas leituras sejam comparáveis.
 */
private val clockOrigin = TimeSource.Monotonic.markNow()

/** EN Current monotonic time in nanoseconds. PT Tempo monotônico atual em nanossegundos. */
internal fun benchNanoTime(): Long = clockOrigin.elapsedNow().inWholeNanoseconds
