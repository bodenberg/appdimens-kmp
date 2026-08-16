/**
 * Android platform window handle: wraps `android.content.Context` as [AppDimensContext].
 */
package com.appdimens.kmp.core

import android.app.Activity
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.util.DisplayMetrics
import androidx.window.layout.FoldingFeature
import com.appdimens.kmp.common.UiModeType

/**
 * EN Android implementation of [AppDimensContext]. Wraps a raw `android.content.Context`;
 * instances are cached per raw Context (see [AndroidAppDimensContextCache]) so the
 * identity checks used by [DimenCache] fast lanes are stable across recompositions.
 *
 * PT Implementação Android de [AppDimensContext]. Envolve um `android.content.Context`
 * bruto; instâncias são cacheadas por Context (ver [AndroidAppDimensContextCache]).
 */
internal class AndroidAppDimensContext(
    internal val androidContext: Context,
    foldingFeature: FoldingFeature? = null,
) : AppDimensContext {

    @Volatile
    internal var foldingFeature: FoldingFeature? = foldingFeature

    override val configuration: ScreenConfiguration
        get() = androidContext.resources.configuration.toScreenConfiguration()

    override val density: Float
        get() = androidContext.resources.displayMetrics.density

    override val xdpi: Float
        get() = androidContext.resources.displayMetrics.xdpi

    override val isInMultiWindowMode: Boolean
        get() = androidContext.findActivityInternal()?.isInMultiWindowMode == true

    override val uiModeType: UiModeType
        get() = UiModeTypeDetector.detect(androidContext, foldingFeature)

    override fun registerConfigurationListener(listener: () -> Unit): ConfigurationRegistration {
        return AppConfigListenerRegistry.register(androidContext, listener)
    }

    internal fun updateFoldingFeature(feature: FoldingFeature?) {
        foldingFeature = feature
    }
}

/**
 * EN Per-Application `ComponentCallbacks2` registry — one process-wide watcher per
 * Application, dispatching to per-window listeners. On any real configuration change
 * the [DimenCache] fast slots are invalidated synchronously.
 *
 * PT Registry por Application de `ComponentCallbacks2` — um watcher por processo,
 * despachando para listeners por janela.
 */
internal object AppConfigListenerRegistry {
    private val listenersByApp =
        java.util.Collections.synchronizedMap(java.util.WeakHashMap<Context, MutableSet<() -> Unit>>())

    private val watcher = object : ComponentCallbacks2 {
        override fun onConfigurationChanged(newConfig: Configuration) {
            val listeners = listenersByApp.values.toList()
            for (set in listeners) {
                set.toList().forEach { it() }
            }
        }

        override fun onLowMemory() = Unit
        override fun onTrimMemory(level: Int) = Unit
    }

    fun register(context: Context, listener: () -> Unit): ConfigurationRegistration {
        val app = context.applicationContext ?: return ConfigurationRegistration.NoOp
        synchronized(listenersByApp) {
            val set = listenersByApp.getOrPut(app) { mutableSetOf() }
            if (set.add(listener) && set.size == 1) {
                app.registerComponentCallbacks(watcher)
            }
        }
        return ConfigurationRegistration {
            synchronized(listenersByApp) {
                val listeners = listenersByApp[app]
                if (listeners != null) {
                    listeners.remove(listener)
                    if (listeners.isEmpty()) {
                        listenersByApp.remove(app)
                        app.unregisterComponentCallbacks(watcher)
                    }
                }
            }
        }
    }
}

/**
 * EN Caches [AndroidAppDimensContext] per raw Context (weak) so the window-handle
 * identity is stable for the whole process — the raw Context always outlives the
 * compositions that read it.
 *
 * PT Cacheia [AndroidAppDimensContext] por Context bruto (fracamente) para que a
 * identidade do handle seja estável — o Context bruto sobrevive às composições.
 */
internal object AndroidAppDimensContextCache {
    // CRITICAL FIX: Use WeakReference as value to break the key → value → key cycle.
    // Without this, WeakHashMap<Context, AndroidAppDimensContext> never collects the
    // key because the value holds a strong reference to the same Context, creating
    // a retention chain: global cache → value → androidContext → same key Context.
    private val cache =
        java.util.Collections.synchronizedMap(java.util.WeakHashMap<Context, java.lang.ref.WeakReference<AndroidAppDimensContext>>())

    fun get(context: Context): AndroidAppDimensContext {
        synchronized(cache) {
            cache[context]?.get()?.let { return it }
            return AndroidAppDimensContext(context).also {
                cache[context] = java.lang.ref.WeakReference(it)
            }
        }
    }
}

/** EN Builds a [ScreenConfiguration] snapshot from an Android [Configuration]. */
internal fun Configuration.toScreenConfiguration(): ScreenConfiguration = ScreenConfiguration(
    screenWidthDp = screenWidthDp,
    screenHeightDp = screenHeightDp,
    smallestScreenWidthDp = smallestScreenWidthDp,
    densityDpi = densityDpi,
    fontScale = fontScale,
    orientation = orientation,
    uiMode = uiMode,
)

/** EN Android convenience overload. */
fun DimenMetrics.Companion.from(
    configuration: Configuration,
    isInMultiWindowMode: Boolean,
): DimenMetrics = from(configuration.toScreenConfiguration(), isInMultiWindowMode)

/**
 * EN Walks [ContextWrapper] chain to find the hosting [Activity], if any.
 * PT Percorre a cadeia de [ContextWrapper] para encontrar a [Activity] hospedeira, se existir.
 */
fun Context.findActivity(): Activity? = findActivityInternal()

internal fun Context.findActivityInternal(): Activity? {
    var ctx: Context = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        val base = ctx.baseContext
        // Defensive guard for malformed custom ContextWrappers.
        if (base === ctx) return null
        ctx = base
    }
    return null
}

/** EN Android platform default: no global context exists — the provider always supplies it. */
actual fun defaultPlatformContext(): AppDimensContext? = null