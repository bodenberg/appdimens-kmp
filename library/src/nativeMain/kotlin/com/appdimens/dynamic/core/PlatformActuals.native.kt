/**
 * Native (iOS/macOS) platform actuals: plain metrics holder (read/written within
 * a single resolution call) + bounded identity map guarded by the caller locks
 * (`kotlinx-atomicfu` `SynchronizedObject`, see `locked()` in common code).
 */
package com.appdimens.dynamic.core

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
 */
internal class SynchronizedIdentityMap<K : Any, V : Any>(
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
    SynchronizedIdentityMap()
