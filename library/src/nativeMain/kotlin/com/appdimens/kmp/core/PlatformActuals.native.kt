/**
 * Native (iOS/macOS) platform actuals: per-thread metrics holder + bounded
 * identity map guarded by the caller locks (`kotlinx-atomicfu`
 * `SynchronizedObject`, see `locked()` in common code).
 */
package com.appdimens.kmp.core

import kotlin.native.concurrent.ThreadLocal

/**
 * EN Per-thread metrics scope. `@ThreadLocal` gives every Kotlin/Native worker
 * its own instance, so two workers resolving dimensions concurrently can never
 * observe each other's `current` (the previous plain-object `var` was a global
 * shared mutable slot — the same class of cross-snapshot contamination the
 * [FastPartitionSlot] fix eliminated on the JVM side).
 *
 * PT Escopo de métricas por thread. `@ThreadLocal` dá a cada worker do
 * Kotlin/Native uma instância própria, então dois workers resolvendo dimensões
 * em paralelo nunca observam o `current` um do outro (o `var` de objeto global
 * anterior era um slot mutável compartilhado — a mesma classe de contaminação
 * entre snapshots que a correção [FastPartitionSlot] eliminou no lado JVM).
 */
@ThreadLocal
@PublishedApi
internal actual object MetricsScopeHolder {
    @PublishedApi
    internal actual var current: DimenMetrics? = null
}

/**
 * Bounded identity map. Native runtimes have no weak-reference collections, so
 * the map is capped and evicts the oldest entry on overflow — window handles are
 * few and alternate slowly, so a small bound is lossless in practice while
 * guaranteeing no unbounded retention. All accesses happen under a caller-held
 * `SynchronizedObject`.
 *
 * EN identity semantics: entries are compared with `===`, so two distinct but
 * `equals()`-equal window handles remain separate keys (a plain `LinkedHashMap`
 * collapses them — wrong for a map that promises identity behavior).
 */
internal class SynchronizedIdentityMap<K : Any, V : Any>(
    private val maxSize: Int = 16,
) : WeakIdentityMap<K, V> {

    private class Entry<K : Any, V : Any>(val key: K, var value: V)

    private val entries = ArrayList<Entry<K, V>>(maxSize)

    private fun indexOf(key: K): Int {
        for (i in entries.indices) {
            if (entries[i].key === key) return i
        }
        return -1
    }

    override fun get(key: K): V? {
        val i = indexOf(key)
        return if (i < 0) null else entries[i].value
    }

    override fun set(key: K, value: V) {
        val i = indexOf(key)
        if (i >= 0) {
            entries[i].value = value
            return
        }
        if (entries.size >= maxSize) {
            entries.removeAt(0)
        }
        entries.add(Entry(key, value))
    }

    override fun containsKey(key: K): Boolean = indexOf(key) >= 0

    override fun remove(key: K): Boolean {
        val i = indexOf(key)
        if (i < 0) return false
        entries.removeAt(i)
        return true
    }
}

@PublishedApi
internal actual fun <K : Any, V : Any> weakIdentityMap(): WeakIdentityMap<K, V> =
    SynchronizedIdentityMap()
