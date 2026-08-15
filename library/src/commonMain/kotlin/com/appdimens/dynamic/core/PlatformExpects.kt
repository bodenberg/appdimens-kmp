/**
 * KMP common platform-abstraction declarations.
 */
package com.appdimens.dynamic.core

import kotlinx.atomicfu.locks.SynchronizedObject

/**
 * EN Thread-local (JVM) / thread-safe (native) holder for the resolution-scoped
 * [DimenMetrics]. The Compose lane uses it so nested strategy calls inherit the
 * enclosing snapshot; code (non-Compose) lanes skip it entirely.
 *
 * PT Holder thread-local (JVM) / thread-safe (native) das métricas do escopo de
 * resolução corrente.
 */
@PublishedApi
internal expect object MetricsScopeHolder {
    @PublishedApi
    internal var current: DimenMetrics?
}

/**
 * EN Weak-key map by identity (JVM/Android: `WeakHashMap` semantics; native:
 * bounded synchronized map). Used for per-window memoization so windows/contexts
 * can be collected normally.
 *
 * PT Mapa de chaves fracas por identidade (JVM/Android: semântica de `WeakHashMap`;
 * native: mapa sincronizado com limite). Usado para memorização por janela.
 */
@PublishedApi
internal interface WeakIdentityMap<K : Any, V : Any> {
    operator fun get(key: K): V?
    operator fun set(key: K, value: V)
    fun containsKey(key: K): Boolean
}

@PublishedApi
internal expect fun <K : Any, V : Any> weakIdentityMap(): WeakIdentityMap<K, V>

/**
 * EN Mutual-exclusion helper for shared mutable state. Implemented with
 * `kotlinx-atomicfu` locks, which compile to `synchronized` on JVM/native and a
 * no-op-ish monitor on wasmJs (single-threaded).
 *
 * PT Auxiliar de exclusão mútua para estado compartilhado. Implementado com
 * `kotlinx-atomicfu`, que compila para `synchronized` no JVM/native e é
 * praticamente no-op no wasmJs (single-threaded).
 */
@PublishedApi
internal inline fun <R> locked(lock: SynchronizedObject, block: () -> R): R =
    kotlinx.atomicfu.locks.synchronized(lock, block)