@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

package com.appdimens.kmp.interpolated

import com.appdimens.kmp.core.AppDimensContext
import com.appdimens.kmp.core.ScreenConfiguration

import com.appdimens.kmp.core.DimenCache

/**
 * EN Default-path scale for this satellite; evaluated when the
 * strategy module is on the classpath; derived from the current window snapshot.
 * PT Escala derivada do snapshot da janela corrente — só existe se o módulo estiver no APK.
 */
internal object InterpolatedFactors {
    /** Memoized on the snapshot; identical math, computed once per DimenMetrics. */
    val scale: Float
        get() = DimenCache.currentMetrics.interpolatedScale
}
