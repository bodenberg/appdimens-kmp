/**
 * Web (wasmJs) platform actuals: single-threaded runtime — no thread-local
 * holder needed, plain map for window memoization.
 */
package com.appdimens.kmp.core

@PublishedApi
internal actual object MetricsScopeHolder {
    @PublishedApi
    internal actual var current: DimenMetrics? = null
}

/**
 * Single-threaded web runtime: a plain map is safe; no weak references exist.
 * Keyed by identity-equality of the window handle (referentially stable).
 */
internal class WebIdentityMap<K : Any, V : Any>(
    private val maxSize: Int = 16,
) : WeakIdentityMap<K, V> {
    private val map = LinkedHashMap<K, V>()

    override fun get(key: K): V? = map[key]

    override fun set(key: K, value: V) {
        if (map.size >= maxSize && !map.containsKey(key)) {
            map.remove(map.keys.first())
        }
        map[key] = value
    }

    override fun containsKey(key: K): Boolean = map.containsKey(key)
}

@PublishedApi
internal actual fun <K : Any, V : Any> weakIdentityMap(): WeakIdentityMap<K, V> =
    WebIdentityMap()
