/**
 * Web platform actuals, shared by js(IR) and wasmJs: single-threaded runtime —
 * no thread-local holder needed, plain bounded map for window memoization.
 */
package com.appdimens.kmp.core

@PublishedApi
internal actual object MetricsScopeHolder {
    @PublishedApi
    internal actual var current: DimenMetrics? = null
}

/**
 * Single-threaded web runtime: a plain map is safe; no weak references exist.
 * Keyed by identity (`===`) of the window handle, so two distinct but
 * `equals()`-equal handles remain separate keys (a `LinkedHashMap` would
 * collapse them — wrong for a map that promises identity behavior).
 */
internal class WebIdentityMap<K : Any, V : Any>(
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
    WebIdentityMap()
