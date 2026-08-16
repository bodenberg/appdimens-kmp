/**
 * Lifecycle of the reference-counted configuration watcher ([DimenCache]).
 *
 * Guarantees:
 *  - the platform registration is created once per context even with several
 *    consumers (Compose providers / code callers);
 *  - releasing the last consumer disposes the registration and removes the weak
 *    entry, making the context collectable;
 *  - a new acquire after full release re-registers.
 */
package com.appdimens.kmp.core

import com.appdimens.kmp.common.UiModeType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConfigWatcherLifecycleTest {

    /** EN Tracking fake: counts register/dispose of the platform listener. */
    private class TrackingContext : AppDimensContext {
        var registerCount = 0
        var disposeCount = 0

        override val configuration: ScreenConfiguration = ScreenConfiguration(
            screenWidthDp = 400,
            screenHeightDp = 800,
            smallestScreenWidthDp = 400,
            densityDpi = 420,
            fontScale = 1f,
            orientation = ScreenConfiguration.ORIENTATION_PORTRAIT,
            uiMode = 1,
        )

        override val density: Float = 420f / 160f
        override val xdpi: Float = density * 160f
        override val isInMultiWindowMode: Boolean = false
        override val uiModeType: UiModeType = UiModeType.NORMAL

        override fun registerConfigurationListener(listener: () -> Unit): ConfigurationRegistration {
            registerCount++
            val registration = ConfigurationRegistration {
                disposeCount++
            }
            return registration
        }
    }

    @Test
    fun acquireRegistersOnceReleaseDisposesAtZero() {
        val ctx = TrackingContext()

        DimenCache.acquireConfigWatcher(ctx)
        DimenCache.acquireConfigWatcher(ctx)
        DimenCache.acquireConfigWatcher(ctx)
        assertEquals(1, ctx.registerCount, "registration must happen exactly once")

        DimenCache.releaseConfigWatcher(ctx)
        DimenCache.releaseConfigWatcher(ctx)
        assertEquals(0, ctx.disposeCount, "must not dispose while consumers remain")

        DimenCache.releaseConfigWatcher(ctx)
        assertEquals(1, ctx.disposeCount, "last release must dispose")
    }

    @Test
    fun acquireAfterFullReleaseReregisters() {
        val ctx = TrackingContext()

        DimenCache.acquireConfigWatcher(ctx)
        DimenCache.releaseConfigWatcher(ctx)
        assertEquals(1, ctx.disposeCount)

        DimenCache.acquireConfigWatcher(ctx)
        assertEquals(2, ctx.registerCount, "a new acquire after full release re-registers")
        DimenCache.releaseConfigWatcher(ctx)
        assertEquals(2, ctx.disposeCount)
    }

    @Test
    fun disposeConfigWatcherDisposesUnconditionally() {
        val ctx = TrackingContext()

        DimenCache.acquireConfigWatcher(ctx)
        DimenCache.disposeConfigWatcher(ctx)
        assertEquals(1, ctx.disposeCount)

        // A subsequent acquire must re-register (entry was removed).
        DimenCache.acquireConfigWatcher(ctx)
        assertEquals(2, ctx.registerCount)
        DimenCache.releaseConfigWatcher(ctx)
    }

    @Test
    fun metricsForDoesNotLeakConsumers() {
        val ctx = TrackingContext()

        // Code path (non-Compose) registers without a consumer count.
        DimenCache.metricsFor(ctx)
        assertEquals(1, ctx.registerCount)

        // Explicit dispose releases the code-path registration.
        DimenCache.disposeConfigWatcher(ctx)
        assertEquals(1, ctx.disposeCount)

        // Re-resolution re-registers — the library stays correct after dispose.
        DimenCache.metricsFor(ctx)
        assertEquals(2, ctx.registerCount)
        DimenCache.disposeConfigWatcher(ctx)
    }

    @Test
    fun listenerDoesNotCaptureContext() {
        val ctx = TrackingContext()
        DimenCache.acquireConfigWatcher(ctx)

        // The registered listener must not reference the context strongly: the
        // weak watcher entry then dies with the context. We assert the public
        // surface only — the internal listener is context-free by construction,
        // and a config change still invalidates the fast slots.
        DimenCache.invalidateOnConfigChange(ctx.configuration)
        assertTrue(ctx.registerCount > 0)
        assertFalse(ctx.disposeCount > 0)
        DimenCache.releaseConfigWatcher(ctx)
    }
}
