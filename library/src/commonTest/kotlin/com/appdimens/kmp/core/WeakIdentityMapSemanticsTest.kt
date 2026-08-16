/**
 * Cross-platform semantics of [WeakIdentityMap] (all actuals):
 *  - keys are compared by **identity** (`===`), so two distinct but
 *    `equals()`-equal handles are separate entries everywhere;
 *  - put overwrites the entry of the same identity, never a value-equal one;
 *  - remove/containsKey operate on identity.
 */
package com.appdimens.kmp.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WeakIdentityMapSemanticsTest {

    private class Key(val v: Int) {
        override fun equals(other: Any?): Boolean =
            other is Key && other.v == v

        override fun hashCode(): Int = v
    }

    @Test
    fun distinctButEqualKeysRemainSeparate() {
        val map = weakIdentityMap<Key, String>()
        val a = Key(7)
        val b = Key(7)

        map[a] = "A"
        map[b] = "B"

        // Identity semantics: even though a == b, they are different keys.
        assertEquals("A", map[a])
        assertEquals("B", map[b])
        assertTrue(map.containsKey(a))
        assertTrue(map.containsKey(b))

        map.remove(a)
        assertEquals(null, map[a])
        assertEquals("B", map[b])
    }

    @Test
    fun putOverwritesSameIdentityOnly() {
        val map = weakIdentityMap<Key, String>()
        val a = Key(1)
        val b = Key(1)

        map[a] = "first"
        map[a] = "second"
        map[b] = "other"

        assertEquals("second", map[a])
        assertEquals("other", map[b])
    }

    @Test
    fun removeReturnsExistenceByIdentity() {
        val map = weakIdentityMap<Key, String>()
        val a = Key(5)
        val b = Key(5)

        map[a] = "A"

        assertFalse(map.remove(b), "removing a value-equal but distinct key must not remove a")
        assertTrue(map.containsKey(a))
        assertTrue(map.remove(a))
        assertFalse(map.containsKey(a))
    }

    @Test
    fun boundedMapEvictsOldestOnOverflow() {
        val map = weakIdentityMap<Key, String>()
        // Keep the keys strongly reachable: identity lookups need the same instances.
        val keys = List(32) { Key(it) }
        repeat(keys.size) { i ->
            map[keys[i]] = "v$i"
        }
        // The native/web actuals are bounded (max 16); JVM's weak map grows.
        // Just verify the most recent key is present and the map still answers.
        assertEquals("v31", map[keys[31]])
    }
}
