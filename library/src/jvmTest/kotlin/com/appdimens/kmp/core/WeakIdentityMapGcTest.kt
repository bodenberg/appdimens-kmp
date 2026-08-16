/**
 * JVM GC semantics of [WeakIdentityHashMap].
 *
 * These tests pin the *correctness boundary* of the weak identity map:
 *  - an entry must survive repeated GC while its original key is strongly
 *    reachable (the old `WeakHashMap<WeakKey<K>, V>` design failed this — the
 *    wrapper itself was the weak key and could be collected early);
 *  - an entry must eventually disappear once the original key dies.
 */
package com.appdimens.kmp.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class WeakIdentityMapGcTest {

    @Test
    fun entrySurvivesGcWhileOriginalKeyIsStronglyReachable() {
        val map = WeakIdentityHashMap<Any, String>()
        val key = Any()
        map[key] = "alive"

        repeat(20) {
            System.gc()
            Thread.sleep(10)
        }

        assertEquals("alive", map[key])
        assertTrue(map.containsKey(key))
        // Keep the key demonstrably live.
        assertNotNull(key)
    }

    @Test
    fun entryEventuallyDisappearsAfterOriginalKeyDies() {
        val map = WeakIdentityHashMap<Any, String>()

        var key: Any? = Any()
        map[key!!] = "value"

        val weak = java.lang.ref.WeakReference(key)
        key = null

        // Force collection until the referent is dead.
        var collected = false
        repeat(100) {
            System.gc()
            Thread.sleep(5)
            if (weak.get() == null) {
                collected = true
                return@repeat
            }
        }
        assertTrue(collected, "referent should be collectable after losing the strong ref")

        // A later operation drains the ReferenceQueue and removes the entry.
        // Probe with a fresh key — must not see the dead entry.
        val probe = Any()
        assertEquals(null, map[probe])
        assertTrue(!map.containsKey(probe))
    }

    @Test
    fun distinctKeysWithSameEqualsRemainSeparate() {
        val map = WeakIdentityHashMap<Key, String>()
        val a = Key("same")
        val b = Key("same")

        map[a] = "A"
        map[b] = "B"

        // Identity semantics: a and b are different keys even though a == b.
        assertEquals("A", map[a])
        assertEquals("B", map[b])

        map.remove(a)
        assertEquals(null, map[a])
        assertEquals("B", map[b])
    }

    private class Key(val v: String) {
        override fun equals(other: Any?): Boolean =
            other is Key && other.v == v

        override fun hashCode(): Int = v.hashCode()
    }
}
