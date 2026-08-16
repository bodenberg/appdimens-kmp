/**
 * Android platform actuals: weak identity map + thread-local metrics holder.
 */
package com.appdimens.kmp.core

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.HashMap

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
 *
 * Implementation note (correctness boundary, not a micro-optimization): the map
 * is a **strong** `HashMap` whose keys are [IdentityWeakReference] wrappers
 * registered in a [ReferenceQueue]. A `WeakHashMap<WeakKey<K>, V>` variant is
 * subtly broken: the wrapper itself becomes the WeakHashMap's weak key, and
 * nothing strongly references the wrapper after insertion — so the GC may
 * collect the *wrapper* while the original key is still strongly reachable,
 * silently dropping live entries (memoization loss, re-registration of
 * listeners, lost dispose handles). Keeping the wrapper strongly in a HashMap
 * means the entry lives exactly as long as its referent — no premature drops.
 */
internal class WeakIdentityHashMap<K : Any, V : Any> : WeakIdentityMap<K, V> {

    /**
     * Weak wrapper with identity (`===`) semantics. The single class serves both
     * stored keys (with queue) and transient lookups (without queue), so lookups
     * and stored keys compare equal when — and only when — they refer to the same
     * live referent.
     */
    private class IdentityWeakReference<K : Any> : WeakReference<K> {
        private val identityHash: Int

        constructor(referent: K, queue: ReferenceQueue<K>) : super(referent, queue) {
            identityHash = System.identityHashCode(referent)
        }

        private constructor(referent: K) : super(referent) {
            identityHash = System.identityHashCode(referent)
        }

        override fun hashCode(): Int = identityHash

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is IdentityWeakReference<*>) return false
            val mine = get()
            val theirs = other.get()
            return mine != null && theirs != null && mine === theirs
        }

        companion object {
            /** Transient lookup wrapper — same identity semantics, no queue. */
            fun <K : Any> lookup(key: K): IdentityWeakReference<K> =
                IdentityWeakReference(key)
        }
    }

    private val queue = ReferenceQueue<K>()
    private val map = HashMap<IdentityWeakReference<K>, V>()

    /** Drains dead referents into the [ReferenceQueue] and removes their entries. */
    private fun drainQueue() {
        while (true) {
            @Suppress("UNCHECKED_CAST")
            val dead = queue.poll() as IdentityWeakReference<K>? ?: break
            map.remove(dead)
        }
    }

    override fun get(key: K): V? {
        drainQueue()
        return map[IdentityWeakReference.lookup(key)]
    }

    override fun set(key: K, value: V) {
        drainQueue()
        map[IdentityWeakReference(key, queue)] = value
    }

    override fun containsKey(key: K): Boolean {
        drainQueue()
        return map.containsKey(IdentityWeakReference.lookup(key))
    }

    override fun remove(key: K): Boolean {
        drainQueue()
        return map.remove(IdentityWeakReference.lookup(key)) != null
    }
}

@PublishedApi
internal actual fun <K : Any, V : Any> weakIdentityMap(): WeakIdentityMap<K, V> =
    WeakIdentityHashMap()
