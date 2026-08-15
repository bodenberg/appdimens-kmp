/**
 * Android UI-mode detection (port of the original `UiModeType.fromConfiguration`),
 * including foldable detection via Jetpack WindowManager and the hinge sensor.
 */
package com.appdimens.dynamic.core

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.SensorManager
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowMetricsCalculator
import com.appdimens.dynamic.common.UiModeType

internal object UiModeTypeDetector {

    /**
     * EN Returns the [UiModeType] for a raw Android [Context], taking into account
     * physical foldable features using Jetpack WindowManager.
     * PT Retorna o [UiModeType] para um [Context] Android, considerando dobráveis.
     */
    fun detect(context: Context, foldingFeature: FoldingFeature? = null): UiModeType {
        val config = context.resources.configuration

        // EN 1. Try to use Jetpack WindowManager FoldingFeature if provided
        // PT 1. Tenta usar o FoldingFeature do Jetpack WindowManager se fornecido
        if (foldingFeature != null) {
            val isFold = foldingFeature.orientation == FoldingFeature.Orientation.VERTICAL

            return if (isFold) {
                when (foldingFeature.state) {
                    FoldingFeature.State.FLAT -> UiModeType.FOLD_OPEN
                    FoldingFeature.State.HALF_OPENED -> UiModeType.FOLD_HALF_OPENED
                    // Defensive: FoldingFeature is typically absent when closed;
                    // future API versions may add new states.
                    else -> UiModeType.FOLD_CLOSED
                }
            } else {
                when (foldingFeature.state) {
                    FoldingFeature.State.FLAT -> UiModeType.FLIP_OPEN
                    FoldingFeature.State.HALF_OPENED -> UiModeType.FLIP_HALF_OPENED
                    else -> UiModeType.FLIP_CLOSED
                }
            }
        }

        // EN 2. Fallback: Check for hinge sensor to identify foldable
        // PT 2. Fallback: Verifica sensor de dobradiça para identificar dispositivo dobrável
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        // Sensor.TYPE_HINGE_ANGLE is 36
        val hingeSensor = sensorManager?.getDefaultSensor(36)
        val isFoldable = hingeSensor != null

        if (isFoldable) {
            // EN We use Jetpack WindowManager to get the device's maximum window size
            // PT Usamos Jetpack WindowManager para obter o tamanho máximo da janela do dispositivo
            val maxMetrics = WindowMetricsCalculator.getOrCreate().computeMaximumWindowMetrics(context)
            val maxBounds = maxMetrics.bounds
            val density = context.resources.displayMetrics.density

            val maxSwPx = kotlin.math.min(maxBounds.width(), maxBounds.height())
            val maxSwDp = maxSwPx / density

            val isFold = maxSwDp >= 600f
            val currentSwDp = config.smallestScreenWidthDp

            return if (isFold) {
                val unfoldedThreshold = (maxSwDp * 0.85f).toInt()
                when {
                    currentSwDp >= unfoldedThreshold -> UiModeType.FOLD_OPEN
                    currentSwDp >= (unfoldedThreshold * 0.6f).toInt() -> UiModeType.FOLD_HALF_OPENED
                    else -> UiModeType.FOLD_CLOSED
                }
            } else {
                val area = config.screenWidthDp * config.screenHeightDp
                val unfoldedArea = (maxSwDp * maxSwDp * 0.7f).toInt()
                when {
                    area >= unfoldedArea -> UiModeType.FLIP_OPEN
                    area >= (unfoldedArea * 0.5f).toInt() -> UiModeType.FLIP_HALF_OPENED
                    else -> UiModeType.FLIP_CLOSED
                }
            }
        }

        // EN The mask is used to extract only the UI Mode TYPE, ignoring night/other flags.
        // PT A máscara é usada para extrair apenas o TIPO do UI Mode, ignorando flags noturnas/outras.
        val type = config.uiMode and Configuration.UI_MODE_TYPE_MASK

        if (type == Configuration.UI_MODE_TYPE_TELEVISION ||
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
        ) {
            return UiModeType.TELEVISION
        }

        return UiModeType.fromConfigurationValue(type) // NORMAL as default
    }
}