/**
 * Android platform actuals: weak identity map + thread-local metrics holder.
 */
package com.appdimens.kmp.core

import java.lang.ref.WeakReference

@PublishedApi
internal actual object MetricsScopeHolder {
    private val threadLocal = ThreadLocal<DimenMetrics?>()

    @PublishedApi
    internal actual var current: DimenMetrics?
        get() = threadLocal.get()
        set(value) = threadLocal.set(value)
}

/**
 * Weak-key identity map: values never retain their keys, so a window/context can be
 * collected normally (mirrors the Android `WeakHashMap<Context, …>` semantics).
 */
internal class WeakIdentityHashMap<K : Any, V : Any> : WeakIdentityMap<K, V> {
    private class WeakKey<K : Any>(referent: K) : WeakReference<K>(referent) {
        override fun hashCode(): Int =
            get()?.let { System.identityHashCode(it) } ?: 0

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is WeakKey<*>) return false
            val a = get()
            val b = other.get()
            return a != null && a === b
        }
    }

    private val map = java.util.WeakHashMap<WeakKey<K>, V>()

    override fun get(key: K): V? = map[WeakKey(key)]

    override fun set(key: K, value: V) {
        map[WeakKey(key)] = value
    }

    override fun containsKey(key: K): Boolean = map.containsKey(WeakKey(key))
}

@PublishedApi
internal actual fun <K : Any, V : Any> weakIdentityMap(): WeakIdentityMap<K, V> =
    WeakIdentityHashMap()
