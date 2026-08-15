@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

package com.appdimens.kmp.perimeter

import com.appdimens.kmp.core.AppDimensContext
import com.appdimens.kmp.core.ScreenConfiguration
import com.appdimens.kmp.core.currentScreenConfiguration
import com.appdimens.kmp.core.localAppDimensContext

import com.appdimens.kmp.core.DimenCache

/**
 * EN Default-path scale for this satellite; evaluated when the
 * strategy module is on the classpath; derived from the current window snapshot.
 * PT Escala derivada do snapshot da janela corrente — só existe se o módulo estiver no APK.
 */
internal object PerimeterFactors {
    /** Memoized on the snapshot; one div per DimenMetrics instead of per call. */
    val scale: Float
        get() = DimenCache.currentMetrics.perimeterScale
}
