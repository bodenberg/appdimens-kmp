@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

/**
 * Author & Developer: Jean Bodenberg
 * GIT: https://github.com/bodenberg/appdimens-sdps.git
 * Date: 2025-10-04
 *
 * Library: AppDimens
 *
 * Description:
 * The AppDimens library is a dimension management system that automatically
 * adjusts Dp, Sp, and Px values in a responsive and mathematically refined way,
 * ensuring layout consistency across any screen size or ratio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.appdimens.kmp.code.diagonal

import com.appdimens.kmp.core.AppDimensContext
import com.appdimens.kmp.core.ScreenConfiguration

import com.appdimens.kmp.common.DpQualifier
import com.appdimens.kmp.common.Orientation
import com.appdimens.kmp.common.UiModeType
import com.appdimens.kmp.common.Inverter
import com.appdimens.kmp.core.DesignScaleConstants
import com.appdimens.kmp.core.DimenCalculationPlumbing
import com.appdimens.kmp.core.DimenCache
import com.appdimens.kmp.diagonal.DiagonalFactors

// EN Rotation facilitator extensions for non-Compose (Views).
// PT Extensões facilitadoras para rotação em não-Compose (Views).

private const val BASE_RATIO_STEP = 300f

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)**.
 * Uses the base value by default, but when the device is in the specified [orientation],
 * it uses [rotationValue] scaled with the given [finalQualifierResolver].
 */
fun Number.dgsdpRotate(
    context: AppDimensContext,
    rotationValue: Number,
    finalQualifierResolver: DpQualifier = DpQualifier.SMALL_WIDTH,
    orientation: Orientation = Orientation.LANDSCAPE,
    ignoreMultiWindows: Boolean = false,
    applyAspectRatio: Boolean = false,
    customSensitivityK: Float? = null
): Float {
    val configuration = context.configuration
    val isTargetOrientation = when (orientation) {
        Orientation.LANDSCAPE -> configuration.orientation == ScreenConfiguration.ORIENTATION_LANDSCAPE
        Orientation.PORTRAIT -> configuration.orientation == ScreenConfiguration.ORIENTATION_PORTRAIT
        else -> false
    }
    return if (isTargetOrientation) {
        rotationValue.toDynamicDiagonalPx(context, finalQualifierResolver, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

/**
 * EN
 * Extension for Int with dynamic scaling based on **Screen Height (hDP)**.
 */
fun Number.dghdpRotate(
    context: AppDimensContext,
    rotationValue: Number,
    finalQualifierResolver: DpQualifier = DpQualifier.HEIGHT,
    orientation: Orientation = Orientation.LANDSCAPE,
    ignoreMultiWindows: Boolean = false,
    applyAspectRatio: Boolean = false,
    customSensitivityK: Float? = null
): Float {
    val configuration = context.configuration
    val isTargetOrientation = when (orientation) {
        Orientation.LANDSCAPE -> configuration.orientation == ScreenConfiguration.ORIENTATION_LANDSCAPE
        Orientation.PORTRAIT -> configuration.orientation == ScreenConfiguration.ORIENTATION_PORTRAIT
        else -> false
    }
    return if (isTargetOrientation) {
        rotationValue.toDynamicDiagonalPx(context, finalQualifierResolver, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDiagonalPx(context, DpQualifier.HEIGHT, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

/**
 * EN
 * Extension for Int with dynamic scaling based on **Screen Width (wDP)**.
 */
fun Number.dgwdpRotate(
    context: AppDimensContext,
    rotationValue: Number,
    finalQualifierResolver: DpQualifier = DpQualifier.WIDTH,
    orientation: Orientation = Orientation.LANDSCAPE,
    ignoreMultiWindows: Boolean = false,
    applyAspectRatio: Boolean = false,
    customSensitivityK: Float? = null
): Float {
    val configuration = context.configuration
    val isTargetOrientation = when (orientation) {
        Orientation.LANDSCAPE -> configuration.orientation == ScreenConfiguration.ORIENTATION_LANDSCAPE
        Orientation.PORTRAIT -> configuration.orientation == ScreenConfiguration.ORIENTATION_PORTRAIT
        else -> false
    }
    return if (isTargetOrientation) {
        rotationValue.toDynamicDiagonalPx(context, finalQualifierResolver, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDiagonalPx(context, DpQualifier.WIDTH, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

// EN UiModeType facilitator extensions for non-Compose.
// PT Extensões facilitadoras para UiModeType em não-Compose.

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)**.
 * Uses the base value by default, but when the device matches the specified [uiModeType],
 * it uses [modeValue] instead.
 */
fun Number.dgsdpMode(
    context: AppDimensContext,
    modeValue: Number,
    uiModeType: UiModeType,
    finalQualifierResolver: DpQualifier? = null,
    ignoreMultiWindows: Boolean = false,
    applyAspectRatio: Boolean = false,
    customSensitivityK: Float? = null
): Float {
    val currentUiModeType = DimenCache.getCachedUiModeType(context) // In non-Compose we could try to find activity but usually context is enough
    return if (currentUiModeType == uiModeType) {
        modeValue.toDynamicDiagonalPx(context, finalQualifierResolver ?: DpQualifier.SMALL_WIDTH, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

/**
 * EN
 * Extension for Int with dynamic scaling based on **Screen Height (hDP)**.
 */
fun Number.dghdpMode(
    context: AppDimensContext,
    modeValue: Number,
    uiModeType: UiModeType,
    finalQualifierResolver: DpQualifier? = null,
    ignoreMultiWindows: Boolean = false,
    applyAspectRatio: Boolean = false,
    customSensitivityK: Float? = null
): Float {
    val currentUiModeType = DimenCache.getCachedUiModeType(context)
    return if (currentUiModeType == uiModeType) {
        modeValue.toDynamicDiagonalPx(context, finalQualifierResolver ?: DpQualifier.HEIGHT, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDiagonalPx(context, DpQualifier.HEIGHT, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

/**
 * EN
 * Extension for Int with dynamic scaling based on **Screen Width (wDP)**.
 */
fun Number.dgwdpMode(
    context: AppDimensContext,
    modeValue: Number,
    uiModeType: UiModeType,
    finalQualifierResolver: DpQualifier? = null,
    ignoreMultiWindows: Boolean = false,
    applyAspectRatio: Boolean = false,
    customSensitivityK: Float? = null
): Float {
    val currentUiModeType = DimenCache.getCachedUiModeType(context)
    return if (currentUiModeType == uiModeType) {
        modeValue.toDynamicDiagonalPx(context, finalQualifierResolver ?: DpQualifier.WIDTH, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDiagonalPx(context, DpQualifier.WIDTH, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}


/**
 * EN
 * Gets the actual value from the ScreenConfiguration for the given DpQualifier.
 *
 * PT
 * Obtém o valor real da configuração (ScreenConfiguration) para o DpQualifier dado.
 *
 * @param qualifier The type of qualifier (SMALL_WIDTH, HEIGHT, WIDTH).
 * @param configuration The current resource configuration.
 * @return The numeric value (in Dp) of the screen metric.
 */
internal fun getQualifierValue(qualifier: DpQualifier, configuration: ScreenConfiguration): Float {
    return when (qualifier) {
        DpQualifier.SMALL_WIDTH -> configuration.smallestScreenWidthDp.toFloat()
        DpQualifier.HEIGHT -> configuration.screenHeightDp.toFloat()
        DpQualifier.WIDTH -> configuration.screenWidthDp.toFloat()
    }
}

// EN Standard Android extensions for quick dynamic scaling (View-based).
// PT Extensões Android padrão para dimensionamento dinâmico rápido (baseado em Views).

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Smallest Width (swDP)**.
 * Usage example: `16.sdp(context)`.
 *
 * PT
 * Extensão para Int com dimensionamento dinâmico baseado na **Smallest Width (swDP)**.
 * Exemplo de uso: `16.sdp(context)`.
 */
private fun Number.fastDiagonalPx(context: AppDimensContext, qualifier: DpQualifier, applyAspectRatio: Boolean): Float {
    if (DimenCache.isScalingEnabled() && (qualifier == DpQualifier.SMALL_WIDTH || !applyAspectRatio)) {
        val m = DimenCache.coherentMetrics(context)
        val out = this.toFloat() * m.diagonalScale
        return if (applyAspectRatio) out * m.defaultAspectRatioMultiplier * m.density else out * m.density
    }
    return this.toDynamicDiagonalPx(context, qualifier, applyAspectRatio = applyAspectRatio)
}

fun Number.dgsdp(context: AppDimensContext): Float = fastDiagonalPx(context, DpQualifier.SMALL_WIDTH, applyAspectRatio = false)
fun Number.dgsdpa(context: AppDimensContext): Float = fastDiagonalPx(context, DpQualifier.SMALL_WIDTH, applyAspectRatio = true)
fun Number.dgsdpi(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, ignoreMultiWindows = true)
fun Number.dgsdpia(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)**, but
 * in portrait orientation it acts as **Screen Height (hDP)**.
 * Usage example: `32.sdpPh(context)`.
 *
 * PT
 * Extensão para Int com dimensionamento dinâmico baseado na **Smallest Width (swDP)**, mas
 * na orientação retrato atua como **Altura da Tela (hDP)**.
 * Exemplo de uso: `32.sdpPh(context)`.
 */
fun Number.dgsdpPh(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, Inverter.SW_TO_PH)
fun Number.dgsdpPha(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, Inverter.SW_TO_PH, applyAspectRatio = true)
fun Number.dgsdpPhi(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, Inverter.SW_TO_PH, ignoreMultiWindows = true)
fun Number.dgsdpPhia(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, Inverter.SW_TO_PH, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)**, but
 * in landscape orientation it acts as **Screen Height (hDP)**.
 * Usage example: `32.sdpLh(context)`.
 */
fun Number.dgsdpLh(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, Inverter.SW_TO_LH)
fun Number.dgsdpLha(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, Inverter.SW_TO_LH, applyAspectRatio = true)
fun Number.dgsdpLhi(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, Inverter.SW_TO_LH, ignoreMultiWindows = true)
fun Number.dgsdpLhia(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, Inverter.SW_TO_LH, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)**, but
 * in portrait orientation it acts as **Screen Width (wDP)**.
 * Usage example: `32.sdpPw(context)`.
 */
fun Number.dgsdpPw(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, Inverter.SW_TO_PW)
fun Number.dgsdpPwa(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, Inverter.SW_TO_PW, applyAspectRatio = true)
fun Number.dgsdpPwi(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, Inverter.SW_TO_PW, ignoreMultiWindows = true)
fun Number.dgsdpPwia(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, Inverter.SW_TO_PW, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)**, but
 * in landscape orientation it acts as **Screen Width (wDP)**.
 * Usage example: `32.sdpLw(context)`.
 */
fun Number.dgsdpLw(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, Inverter.SW_TO_LW)
fun Number.dgsdpLwa(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, Inverter.SW_TO_LW, applyAspectRatio = true)
fun Number.dgsdpLwi(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, Inverter.SW_TO_LW, ignoreMultiWindows = true)
fun Number.dgsdpLwia(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, Inverter.SW_TO_LW, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Height (hDP)**.
 * Usage example: `32.hdp(context)`.
 */
fun Number.dghdp(context: AppDimensContext): Float = fastDiagonalPx(context, DpQualifier.HEIGHT, applyAspectRatio = false)
fun Number.dghdpa(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.HEIGHT, applyAspectRatio = true)
fun Number.dghdpi(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.HEIGHT, ignoreMultiWindows = true)
fun Number.dghdpia(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.HEIGHT, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Height (hDP)**, but
 * in landscape orientation it acts as **Screen Width (wDP)**.
 * Usage example: `32.hdpLw(context)`.
 */
fun Number.dghdpLw(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.HEIGHT, Inverter.PH_TO_LW)
fun Number.dghdpLwa(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.HEIGHT, Inverter.PH_TO_LW, applyAspectRatio = true)
fun Number.dghdpLwi(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.HEIGHT, Inverter.PH_TO_LW, ignoreMultiWindows = true)
fun Number.dghdpLwia(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.HEIGHT, Inverter.PH_TO_LW, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Height (hDP)**, but
 * in portrait orientation it acts as **Screen Width (wDP)**.
 * Usage example: `32.hdpPw(context)`.
 */
fun Number.dghdpPw(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.HEIGHT, Inverter.LH_TO_PW)
fun Number.dghdpPwa(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.HEIGHT, Inverter.LH_TO_PW, applyAspectRatio = true)
fun Number.dghdpPwi(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.HEIGHT, Inverter.LH_TO_PW, ignoreMultiWindows = true)
fun Number.dghdpPwia(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.HEIGHT, Inverter.LH_TO_PW, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Width (wDP)**.
 * Usage example: `100.wdp(context)`.
 */
fun Number.dgwdp(context: AppDimensContext): Float = fastDiagonalPx(context, DpQualifier.WIDTH, applyAspectRatio = false)
fun Number.dgwdpa(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.WIDTH, applyAspectRatio = true)
fun Number.dgwdpi(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.WIDTH, ignoreMultiWindows = true)
fun Number.dgwdpia(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.WIDTH, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Width (wDP)**, but
 * in landscape orientation it acts as **Screen Height (hDP)**.
 * Usage example: `100.wdpLh(context)`.
 */
fun Number.dgwdpLh(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.WIDTH, Inverter.PW_TO_LH)
fun Number.dgwdpLha(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.WIDTH, Inverter.PW_TO_LH, applyAspectRatio = true)
fun Number.dgwdpLhi(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.WIDTH, Inverter.PW_TO_LH, ignoreMultiWindows = true)
fun Number.dgwdpLhia(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.WIDTH, Inverter.PW_TO_LH, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Width (wDP)**, but
 * in portrait orientation it acts as **Screen Height (hDP)**.
 * Usage example: `100.wdpPh(context)`.
 */
fun Number.dgwdpPh(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.WIDTH, Inverter.LW_TO_PH)
fun Number.dgwdpPha(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.WIDTH, Inverter.LW_TO_PH, applyAspectRatio = true)
fun Number.dgwdpPhi(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.WIDTH, Inverter.LW_TO_PH, ignoreMultiWindows = true)
fun Number.dgwdpPhia(context: AppDimensContext): Float = this.toDynamicDiagonalPx(context, DpQualifier.WIDTH, Inverter.LW_TO_PH, ignoreMultiWindows = true, applyAspectRatio = true)

// EN Qualifier-based conditional dynamic scaling.
// PT Escalonamento condicional baseado em qualificador.

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)**.
 * Uses the base value by default, but when the screen metric for [qualifierType]
 * is >= [qualifierValue], it uses [qualifiedValue] instead.
 *
 * PT
 * Extensão para Int com dimensionamento dinâmico baseado na **Smallest Width (swDP)**.
 * Usa o valor base por padrão, mas quando a métrica de tela para [qualifierType]
 * é >= [qualifierValue], usa [qualifiedValue] no lugar.
 */
fun Number.dgsdpQualifier(context: AppDimensContext, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float {
    val configuration = context.configuration
    val qualifierMatch = getQualifierValue(qualifierType, configuration) >= qualifierValue.toFloat()
    return if (qualifierMatch) {
        qualifiedValue.toDynamicDiagonalPx(context, finalQualifierResolver ?: DpQualifier.SMALL_WIDTH, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

/**
 * EN
 * Extension for Int with dynamic scaling based on **Screen Height (hDP)**.
 * Uses the base value by default, but when the screen metric for [qualifierType]
 * is >= [qualifierValue], it uses [qualifiedValue] instead.
 *
 * PT
 * Extensão para Int com dimensionamento dinâmico baseado na **Altura da Tela (hDP)**.
 * Usa o valor base por padrão, mas quando a métrica de tela para [qualifierType]
 * é >= [qualifierValue], usa [qualifiedValue] no lugar.
 */
fun Number.dghdpQualifier(context: AppDimensContext, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float {
    val configuration = context.configuration
    val qualifierMatch = getQualifierValue(qualifierType, configuration) >= qualifierValue.toFloat()
    return if (qualifierMatch) {
        qualifiedValue.toDynamicDiagonalPx(context, finalQualifierResolver ?: DpQualifier.HEIGHT, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDiagonalPx(context, DpQualifier.HEIGHT, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

/**
 * EN
 * Extension for Int with dynamic scaling based on **Screen Width (wDP)**.
 * Uses the base value by default, but when the screen metric for [qualifierType]
 * is >= [qualifierValue], it uses [qualifiedValue] instead.
 *
 * PT
 * Extensão para Int com dimensionamento dinâmico baseado na **Largura da Tela (wDP)**.
 * Usa o valor base por padrão, mas quando a métrica de tela para [qualifierType]
 * é >= [qualifierValue], usa [qualifiedValue] no lugar.
 */
fun Number.dgwdpQualifier(context: AppDimensContext, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float {
    val configuration = context.configuration
    val qualifierMatch = getQualifierValue(qualifierType, configuration) >= qualifierValue.toFloat()
    return if (qualifierMatch) {
        qualifiedValue.toDynamicDiagonalPx(context, finalQualifierResolver ?: DpQualifier.WIDTH, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDiagonalPx(context, DpQualifier.WIDTH, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

// EN UiModeType + DpQualifier combined facilitator extensions.
// PT Extensões facilitadoras combinadas UiModeType + DpQualifier.

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)**.
 * Uses the base value by default, but when the device matches [uiModeType] AND
 * the screen metric for [qualifierType] is >= [qualifierValue], it uses [screenValue] instead.
 *
 * PT
 * Extensão para Int com dimensionamento dinâmico baseado na **Smallest Width (swDP)**.
 * Usa o valor base por padrão, mas quando o dispositivo corresponde ao [uiModeType] E
 * a métrica de tela para [qualifierType] é >= [qualifierValue], usa [screenValue] no lugar.
 */
fun Number.dgsdpScreen(context: AppDimensContext, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float {
    val configuration = context.configuration
    val currentUiModeType = DimenCache.getCachedUiModeType(context)
    val uiModeMatch = currentUiModeType == uiModeType
    val qualifierMatch = getQualifierValue(qualifierType, configuration) >= qualifierValue.toFloat()
    return if (uiModeMatch && qualifierMatch) {
        screenValue.toDynamicDiagonalPx(context, finalQualifierResolver ?: DpQualifier.SMALL_WIDTH, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDiagonalPx(context, DpQualifier.SMALL_WIDTH, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

/**
 * EN
 * Extension for Int with dynamic scaling based on **Screen Height (hDP)**.
 * Uses the base value by default, but when the device matches [uiModeType] AND
 * the screen metric for [qualifierType] is >= [qualifierValue], it uses [screenValue] instead.
 *
 * PT
 * Extensão para Int com dimensionamento dinâmico baseado na **Altura da Tela (hDP)**.
 * Usa o valor base por padrão, mas quando o dispositivo corresponde ao [uiModeType] E
 * a métrica de tela para [qualifierType] é >= [qualifierValue], usa [screenValue] no lugar.
 */
fun Number.dghdpScreen(context: AppDimensContext, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float {
    val configuration = context.configuration
    val currentUiModeType = DimenCache.getCachedUiModeType(context)
    val uiModeMatch = currentUiModeType == uiModeType
    val qualifierMatch = getQualifierValue(qualifierType, configuration) >= qualifierValue.toFloat()
    return if (uiModeMatch && qualifierMatch) {
        screenValue.toDynamicDiagonalPx(context, finalQualifierResolver ?: DpQualifier.HEIGHT, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDiagonalPx(context, DpQualifier.HEIGHT, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

/**
 * EN
 * Extension for Int with dynamic scaling based on **Screen Width (wDP)**.
 * Uses the base value by default, but when the device matches [uiModeType] AND
 * the screen metric for [qualifierType] is >= [qualifierValue], it uses [screenValue] instead.
 *
 * PT
 * Extensão para Int com dimensionamento dinâmico baseado na **Largura da Tela (wDP)**.
 * Usa o valor base por padrão, mas quando o dispositivo corresponde ao [uiModeType] E
 * a métrica de tela para [qualifierType] é >= [qualifierValue], usa [screenValue] no lugar.
 */
fun Number.dgwdpScreen(context: AppDimensContext, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float {
    val configuration = context.configuration
    val currentUiModeType = DimenCache.getCachedUiModeType(context)
    val uiModeMatch = currentUiModeType == uiModeType
    val qualifierMatch = getQualifierValue(qualifierType, configuration) >= qualifierValue.toFloat()
    return if (uiModeMatch && qualifierMatch) {
        screenValue.toDynamicDiagonalPx(context, finalQualifierResolver ?: DpQualifier.WIDTH, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDiagonalPx(context, DpQualifier.WIDTH, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

// EN Dynamic scaling functions (Resource-based).
// PT Funções de dimensionamento dinâmico (baseadas em recursos).

/**
 * EN
 * Converts a [Number] (base Dp value) into a dynamically scaled pixel [Float] for View-based (non-Compose) code.
 *
 * The scaling logic:
 * 1. Builds a 64-bit packed cache key from all dimension parameters.
 * 2. **If [enableCache] is `true`** (default): checks [DimenCache] first. On a hit, returns the
 *    cached pixel value immediately. On a miss, calls [calculateDiagonalDp] and converts Dp→px via
 *    `scaledDp * displayMetrics.density` (equivalent to [android.util.TypedValue.applyDimension]
 *    for `COMPLEX_UNIT_DIP`), then stores the result.
 * 3. **If [enableCache] is `false`**: computes directly via [calculateDiagonalDp], bypassing cache.
 *
 * > ⚠️ **Bypass note**: when [applyAspectRatio] is `false` and [qualifier] is `SMALL_WIDTH`
 * > with `DEFAULT` inverter, the [DimenCache.getOrPut] call internally bypasses the hash lookup
 * > because a raw multiply (~2 ns) is faster than the cache access (~5 ns). Calls with these
 * > parameters measure raw math performance, NOT cache throughput.
 *
 * **Bulk resolution:** for many keys in one pass, prefer building [LongArray] keys with
 * [DimenCache.buildKey] and [DimenCache.getBatch]. **Early init:** call [DimenSdp.warmupCache]
 * (or [DimenSsp.warmupCache]) once with your [AppDimensContext] so initialization
 * work does not land on the first hot-frame call.
 *
 * PT
 * Converte um [Number] (valor Dp base) em um [Float] em pixels dinamicamente escalado para código View-based.
 *
 * A lógica de escalonamento:
 * 1. Constrói uma chave de cache de 64 bits a partir de todos os parâmetros da dimensão.
 * 2. **Se [enableCache] for `true`** (padrão): consulta o [DimenCache] primeiro. No acerto,
 *    retorna o valor em pixels cacheado; no miss, calcula via [calculateDiagonalDp] e armazena.
 * 3. **Se [enableCache] for `false`**: calcula diretamente via [calculateDiagonalDp].
 *
 * @param context            Android [AppDimensContext] for configuration and density access.
 * @param qualifier          Screen dimension qualifier: [com.appdimens.kmp.common.DpQualifier.SMALL_WIDTH],
 *                           [com.appdimens.kmp.common.DpQualifier.HEIGHT], or [com.appdimens.kmp.common.DpQualifier.WIDTH].
 * @param inverter           Orientation-based dimension swap rule (default: [Inverter.DEFAULT]).
 * @param ignoreMultiWindows If `true`, returns the base value in pixels unscaled when in split-screen.
 * @param applyAspectRatio   If `true`, applies the aspect-ratio multiplier.
 * @param customSensitivityK Override for the AR sensitivity constant (null = library default).
 * @return Dynamically scaled pixel value as [Float].
 */
fun Number.toDynamicDiagonalPx(
    context: AppDimensContext,
    qualifier: DpQualifier,
    inverter: Inverter = Inverter.DEFAULT,
    ignoreMultiWindows: Boolean = false,
    applyAspectRatio: Boolean = false,
    customSensitivityK: Float? = null
): Float {
    val base = this.toFloat()
    val configuration = context.configuration

    val density = context.density

    val cacheKey = DimenCache.buildKey(
        baseValue = base,
        isLandscape = configuration.orientation == ScreenConfiguration.ORIENTATION_LANDSCAPE,
        ignoreMultiWindows = ignoreMultiWindows,
        calcType = DimenCache.CalcType.DIAGONAL,
        qualifier = qualifier,
        inverter = inverter,
        applyAspectRatio = applyAspectRatio,
        valueType = DimenCache.ValueType.PX,
        customSensitivityK = customSensitivityK
    )

    return DimenCache.getOrPut(cacheKey, context) {
        val scaledDp = calculateDiagonalDp(base, configuration, qualifier, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK, context)
        scaledDp * density
    }
}

/**
 * EN
 * Shared pure-math scaling kernel used by [toDynamicDiagonalPx] and [toDynamicDiagonalDp].
 *
 * Algorithm summary:
 * 1. Applies [Inverter] rules to swap the effective [DpQualifier] based on screen orientation.
 * 2. If [ignoreMultiWindows] is `true`, detects split-screen mode via layout flags; if active,
 *    returns [baseValue] unchanged so the UI does not over-scale inside a small window.
 * 3. For the common path (`SMALL_WIDTH` + `DEFAULT` inverter + no custom sensitivity),
 *    delegates to [DimenCache.calculateRawScaling] which reads pre-computed factors from
 *    [DimenCache.ScreenFactors] — a single float multiply, zero extra allocations.
 * 4. For other qualifiers or a custom sensitivity constant, reads the screen dimension from
 *    [res.ScreenConfiguration] and performs the scaling formula inline.
 *
 * > **Performance**: Simple paths without Aspect Ratio complete in ~2 ns (single multiply).
 * > Paths with Aspect Ratio require ~41 ns on Snapdragon 888 (includes ln() fallback).
 * > Results are memoized by the [DimenCache] shared across code and compose packages.
 *
 * > **Note**: Both `code/` and `compose/` packages intentionally maintain separate copies of this
 * > function because the `code/` variant operates on [res.ScreenConfiguration] directly
 * > (no Compose runtime), while `compose/` reads it from [androidx.compose.ui.platform.LocalConfiguration].
 * > The math is identical; only the AppDimensContext acquisition path differs.
 *
 * PT
 * Núcleo de escalonamento puro compartilhado por [toDynamicDiagonalPx] e [toDynamicDiagonalDp].
 *
 * Resumo do algoritmo:
 * 1. Aplica as regras de [Inverter] para trocar o [DpQualifier] efetivo conforme a orientação.
 * 2. Se [ignoreMultiWindows] for `true`, detecta split-screen via flags de layout;
 *    se ativo, retorna [baseValue] sem escalar.
 * 3. Para o caminho comum (SMALL_WIDTH + DEFAULT + sem sensibilidade customizada),
 *    delega para [DimenCache.calculateRawScaling] com os fatores pré-calculados.
 * 4. Para outros qualificadores ou sensibilidade customizada, lê a dimensão da tela
 *    da [res.ScreenConfiguration] e executa a fórmula de escalonamento inline.
 *
 * > **Nota**: Os pacotes `code/` e `compose/` mantêm cópias separadas intencionalmente.
 * > A versão `code/` opera sobre [res.ScreenConfiguration] diretamente,
 * > enquanto a versão `compose/` usa [androidx.compose.ui.platform.LocalConfiguration].
 * > A matemática é idêntica; apenas a obtenção do contexto difere.
 *
 * @param baseValue          Raw Dp value to scale (e.g. `16f` for 16 dp).
 * @param configuration      Current [res.ScreenConfiguration] from the context.
 * @param qualifier          Original screen qualifier before inversion.
 * @param inverter           Orientation-swap rule.
 * @param ignoreMultiWindows Whether to suppress scaling in multi-window mode.
 * @param applyAspectRatio   Whether to apply the AR multiplier.
 * @param customSensitivityK Custom AR sensitivity constant, or `null` for the library default.
 * @return Scaled Dp value as a raw [Float].
 */
internal fun calculateDiagonalDp(
    baseValue: Float,
    configuration: ScreenConfiguration,
    qualifier: DpQualifier,
    inverter: Inverter,
    ignoreMultiWindows: Boolean,
    applyAspectRatio: Boolean,
    customSensitivityK: Float?,
    context: AppDimensContext? = null
): Float {
    if (DimenCalculationPlumbing.isMultiWindowConstrained(configuration, ignoreMultiWindows, context)) return baseValue
    var out = baseValue * DiagonalFactors.scale
    if (applyAspectRatio) {
        out *= if (customSensitivityK == null) {
            DimenCache.currentAspectRatioMul
        } else {
            1f + customSensitivityK * DimenCache.currentLogNormalizedAr
        }
    }
    return out
}

/**
 * EN
 * Converts a [Number] (base Dp value) into a dynamically scaled Dp [Float] for View-based (non-Compose) code.
 *
 * Unlike [toDynamicDiagonalPx], the result is returned in Dp units — no density conversion is applied.
 * This is useful for APIs that accept logical Dp values directly (e.g. `View.setPadding` with a
 * custom Dp-aware layout engine).
 *
 * Same caching, validation, and bypass semantics as [toDynamicDiagonalPx].
 *
 * PT
 * Converte um [Number] (valor Dp base) em um [Float] em Dp dinamicamente escalado para código View-based.
 *
 * Ao contrário de [toDynamicDiagonalPx], o resultado é retornado em unidades Dp — sem conversão de densidade.
 * Útil para APIs que aceitam valores Dp lógicos diretamente.
 *
 * Mesma semântica de cache, validação e bypass de [toDynamicDiagonalPx].
 *
 * @param context            Android [AppDimensContext] for configuration access.
 * @param qualifier          Screen dimension qualifier.
 * @param inverter           Orientation-based dimension swap rule (default: [Inverter.DEFAULT]).
 * @param ignoreMultiWindows If `true`, returns the base Dp value unscaled when in split-screen.
 * @param applyAspectRatio   If `true`, applies the aspect-ratio multiplier.
 * @param customSensitivityK Override for the AR sensitivity constant (null = library default).
 * @return Dynamically scaled Dp value as [Float].
 */
fun Number.toDynamicDiagonalDp(
    context: AppDimensContext,
    qualifier: DpQualifier,
    inverter: Inverter = Inverter.DEFAULT,
    ignoreMultiWindows: Boolean = false,
    applyAspectRatio: Boolean = false,
    customSensitivityK: Float? = null
): Float {
    val base = this.toFloat()
    val configuration = context.configuration

    val cacheKey = DimenCache.buildKey(
        baseValue = base,
        isLandscape = configuration.orientation == ScreenConfiguration.ORIENTATION_LANDSCAPE,
        ignoreMultiWindows = ignoreMultiWindows,
        calcType = DimenCache.CalcType.DIAGONAL,
        qualifier = qualifier,
        inverter = inverter,
        applyAspectRatio = applyAspectRatio,
        valueType = DimenCache.ValueType.DP,
        customSensitivityK = customSensitivityK
    )

    return DimenCache.getOrPut(cacheKey, context) {
        calculateDiagonalDp(base, configuration, qualifier, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK, context)
    }
}