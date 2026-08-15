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
package com.appdimens.kmp.code.density

import com.appdimens.kmp.core.AppDimensContext
import com.appdimens.kmp.core.ScreenConfiguration
import com.appdimens.kmp.core.currentScreenConfiguration
import com.appdimens.kmp.core.localAppDimensContext

import com.appdimens.kmp.common.DpQualifier
import com.appdimens.kmp.common.Orientation
import com.appdimens.kmp.common.UiModeType
import com.appdimens.kmp.common.Inverter
import com.appdimens.kmp.core.DimenCache
import kotlin.math.max
import kotlin.math.min

// EN Rotation facilitator extensions for Sp (non-Compose).
// PT Extensões facilitadoras para rotação (Sp) (non-Compose).

private const val BASE_RATIO_STEP = 300f

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)**.
 * Uses the base value by default, but when the device is in the specified [orientation],
 * it uses [rotationValue] scaled with the given [finalQualifierResolver].
 */
fun Number.dsspRotate(
    context: AppDimensContext,
    rotationValue: Number,
    finalQualifierResolver: DpQualifier = DpQualifier.SMALL_WIDTH,
    orientation: Orientation = Orientation.LANDSCAPE,
    fontScale: Boolean = true,
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
        rotationValue.toDynamicDensitySpPx(context, finalQualifierResolver, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

/**
 * EN
 * Extension for Int with dynamic scaling based on **Screen Height (hDP)**.
 */
fun Number.dhspRotate(
    context: AppDimensContext,
    rotationValue: Number,
    finalQualifierResolver: DpQualifier = DpQualifier.HEIGHT,
    orientation: Orientation = Orientation.LANDSCAPE,
    fontScale: Boolean = true,
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
        rotationValue.toDynamicDensitySpPx(context, finalQualifierResolver, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

/**
 * EN
 * Extension for Int with dynamic scaling based on **Screen Width (wDP)**.
 */
fun Number.dwspRotate(
    context: AppDimensContext,
    rotationValue: Number,
    finalQualifierResolver: DpQualifier = DpQualifier.WIDTH,
    orientation: Orientation = Orientation.LANDSCAPE,
    fontScale: Boolean = true,
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
        rotationValue.toDynamicDensitySpPx(context, finalQualifierResolver, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

// EN UiModeType facilitator extensions for Sp (non-Compose).
// PT Extensões facilitadoras para UiModeType (Sp) (non-Compose).

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)**.
 */
fun Number.dsspMode(
    context: AppDimensContext,
    modeValue: Number,
    uiModeType: UiModeType,
    finalQualifierResolver: DpQualifier? = null,
    fontScale: Boolean = true,
    ignoreMultiWindows: Boolean = false,
    applyAspectRatio: Boolean = false,
    customSensitivityK: Float? = null
): Float {
    val currentUiModeType = DimenCache.getCachedUiModeType(context)
    return if (currentUiModeType == uiModeType) {
        modeValue.toDynamicDensitySpPx(context, finalQualifierResolver ?: DpQualifier.SMALL_WIDTH, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

/**
 * EN
 * Extension for Int with dynamic scaling based on **Screen Height (hDP)**.
 */
fun Number.dhspMode(
    context: AppDimensContext,
    modeValue: Number,
    uiModeType: UiModeType,
    finalQualifierResolver: DpQualifier? = null,
    fontScale: Boolean = true,
    ignoreMultiWindows: Boolean = false,
    applyAspectRatio: Boolean = false,
    customSensitivityK: Float? = null
): Float {
    val currentUiModeType = DimenCache.getCachedUiModeType(context)
    return if (currentUiModeType == uiModeType) {
        modeValue.toDynamicDensitySpPx(context, finalQualifierResolver ?: DpQualifier.HEIGHT, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

/**
 * EN
 * Extension for Int with dynamic scaling based on **Screen Width (wDP)**.
 */
fun Number.dwspMode(
    context: AppDimensContext,
    modeValue: Number,
    uiModeType: UiModeType,
    finalQualifierResolver: DpQualifier? = null,
    fontScale: Boolean = true,
    ignoreMultiWindows: Boolean = false,
    applyAspectRatio: Boolean = false,
    customSensitivityK: Float? = null
): Float {
    val currentUiModeType = DimenCache.getCachedUiModeType(context)
    return if (currentUiModeType == uiModeType) {
        modeValue.toDynamicDensitySpPx(context, finalQualifierResolver ?: DpQualifier.WIDTH, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}


// EN Standard Android extensions for quick dynamic text scaling (Sp) (View-based).
// PT Extensões Android padrão para escalonamento dinâmico rápido de texto (Sp) (baseado em Views).

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Smallest Width (swDP)**.
 * Usage example: `16.dssp(context)`.
 */
fun Number.dssp(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true)
fun Number.dsspa(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, applyAspectRatio = true)
fun Number.dsspi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, ignoreMultiWindows = true)
fun Number.dsspia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)**, but
 * in portrait orientation it acts as **Screen Height (hDP)**.
 * Usage example: `32.dsspPh(context)`.
 */
fun Number.dsspPh(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, inverter = Inverter.SW_TO_PH)
fun Number.dsspPha(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, inverter = Inverter.SW_TO_PH, applyAspectRatio = true)
fun Number.dsspPhi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, inverter = Inverter.SW_TO_PH, ignoreMultiWindows = true)
fun Number.dsspPhia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, inverter = Inverter.SW_TO_PH, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)**, but
 * in landscape orientation it acts as **Screen Height (hDP)**.
 * Usage example: `32.dsspLh(context)`.
 */
fun Number.dsspLh(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, inverter = Inverter.SW_TO_LH)
fun Number.dsspLha(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, inverter = Inverter.SW_TO_LH, applyAspectRatio = true)
fun Number.dsspLhi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, inverter = Inverter.SW_TO_LH, ignoreMultiWindows = true)
fun Number.dsspLhia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, inverter = Inverter.SW_TO_LH, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)**, but
 * in portrait orientation it acts as **Screen Width (wDP)**.
 * Usage example: `32.dsspPw(context)`.
 */
fun Number.dsspPw(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, inverter = Inverter.SW_TO_PW)
fun Number.dsspPwa(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, inverter = Inverter.SW_TO_PW, applyAspectRatio = true)
fun Number.dsspPwi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, inverter = Inverter.SW_TO_PW, ignoreMultiWindows = true)
fun Number.dsspPwia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, inverter = Inverter.SW_TO_PW, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)**, but
 * in landscape orientation it acts as **Screen Width (wDP)**.
 * Usage example: `32.dsspLw(context)`.
 */
fun Number.dsspLw(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, inverter = Inverter.SW_TO_LW)
fun Number.dsspLwa(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, inverter = Inverter.SW_TO_LW, applyAspectRatio = true)
fun Number.dsspLwi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, inverter = Inverter.SW_TO_LW, ignoreMultiWindows = true)
fun Number.dsspLwia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = true, inverter = Inverter.SW_TO_LW, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Height (hDP)**.
 * Usage example: `32.dhsp(context)`.
 */
fun Number.dhsp(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = true)
fun Number.dhspa(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = true, applyAspectRatio = true)
fun Number.dhspi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = true, ignoreMultiWindows = true)
fun Number.dhspia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = true, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Height (hDP)**, but
 * in landscape orientation it acts as **Screen Width (wDP)**.
 * Usage example: `32.dhspLw(context)`.
 */
fun Number.dhspLw(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = true, inverter = Inverter.PH_TO_LW)
fun Number.dhspLwa(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = true, inverter = Inverter.PH_TO_LW, applyAspectRatio = true)
fun Number.dhspLwi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = true, inverter = Inverter.PH_TO_LW, ignoreMultiWindows = true)
fun Number.dhspLwia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = true, inverter = Inverter.PH_TO_LW, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Height (hDP)**, but
 * in portrait orientation it acts as **Screen Width (wDP)**.
 * Usage example: `32.dhspPw(context)`.
 */
fun Number.dhspPw(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = true, inverter = Inverter.LH_TO_PW)
fun Number.dhspPwa(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = true, inverter = Inverter.LH_TO_PW, applyAspectRatio = true)
fun Number.dhspPwi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = true, inverter = Inverter.LH_TO_PW, ignoreMultiWindows = true)
fun Number.dhspPwia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = true, inverter = Inverter.LH_TO_PW, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Width (wDP)**.
 * Usage example: `100.dwsp(context)`.
 */
fun Number.dwsp(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = true)
fun Number.dwspa(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = true, applyAspectRatio = true)
fun Number.dwspi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = true, ignoreMultiWindows = true)
fun Number.dwspia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = true, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Width (wDP)**, but
 * in landscape orientation it acts as **Screen Height (hDP)**.
 * Usage example: `100.dwspLh(context)`.
 */
fun Number.dwspLh(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = true, inverter = Inverter.PW_TO_LH)
fun Number.dwspLha(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = true, inverter = Inverter.PW_TO_LH, applyAspectRatio = true)
fun Number.dwspLhi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = true, inverter = Inverter.PW_TO_LH, ignoreMultiWindows = true)
fun Number.dwspLhia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = true, inverter = Inverter.PW_TO_LH, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Width (wDP)**, but
 * in portrait orientation it acts as **Screen Height (hDP)**.
 * Usage example: `100.dwspPh(context)`.
 */
fun Number.dwspPh(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = true, inverter = Inverter.LW_TO_PH)
fun Number.dwspPha(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = true, inverter = Inverter.LW_TO_PH, applyAspectRatio = true)
fun Number.dwspPhi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = true, inverter = Inverter.LW_TO_PH, ignoreMultiWindows = true)
fun Number.dwspPhia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = true, inverter = Inverter.LW_TO_PH, ignoreMultiWindows = true, applyAspectRatio = true)

// EN WITHOUT FONT SCALE variants (dsem escala de fonte)
// PT Variantes SEM ESCALA DE FONTE

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)** (WITHOUT FONT SCALE).
 * Usage example: `16.sei(context)`.
 */
fun Number.sei(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false)
fun Number.seia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, applyAspectRatio = true)
fun Number.seii(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, ignoreMultiWindows = true)
fun Number.seiia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)** (WITHOUT FONT SCALE), but
 * in portrait orientation it acts as **Screen Height (hDP)**.
 * Usage example: `32.semPh(context)`.
 */
fun Number.semPh(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, inverter = Inverter.SW_TO_PH)
fun Number.semPha(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, inverter = Inverter.SW_TO_PH, applyAspectRatio = true)
fun Number.semPhi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, inverter = Inverter.SW_TO_PH, ignoreMultiWindows = true)
fun Number.semPhia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, inverter = Inverter.SW_TO_PH, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)** (WITHOUT FONT SCALE), but
 * in landscape orientation it acts as **Screen Height (hDP)**.
 * Usage example: `32.semLh(context)`.
 */
fun Number.semLh(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, inverter = Inverter.SW_TO_LH)
fun Number.semLha(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, inverter = Inverter.SW_TO_LH, applyAspectRatio = true)
fun Number.semLhi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, inverter = Inverter.SW_TO_LH, ignoreMultiWindows = true)
fun Number.semLhia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, inverter = Inverter.SW_TO_LH, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)** (WITHOUT FONT SCALE), but
 * in portrait orientation it acts as **Screen Width (wDP)**.
 * Usage example: `32.semPw(context)`.
 */
fun Number.semPw(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, inverter = Inverter.SW_TO_PW)
fun Number.semPwa(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, inverter = Inverter.SW_TO_PW, applyAspectRatio = true)
fun Number.semPwi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, inverter = Inverter.SW_TO_PW, ignoreMultiWindows = true)
fun Number.semPwia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, inverter = Inverter.SW_TO_PW, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swDP)** (WITHOUT FONT SCALE), but
 * in landscape orientation it acts as **Screen Width (wDP)**.
 * Usage example: `32.semLw(context)`.
 */
fun Number.semLw(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, inverter = Inverter.SW_TO_LW)
fun Number.semLwa(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, inverter = Inverter.SW_TO_LW, applyAspectRatio = true)
fun Number.semLwi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, inverter = Inverter.SW_TO_LW, ignoreMultiWindows = true)
fun Number.semLwia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale = false, inverter = Inverter.SW_TO_LW, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Height (hDP)** (WITHOUT FONT SCALE).
 * Usage example: `32.hei(context)`.
 */
fun Number.hei(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = false)
fun Number.heia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = false, applyAspectRatio = true)
fun Number.heii(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = false, ignoreMultiWindows = true)
fun Number.heiia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = false, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Height (hDP)** (WITHOUT FONT SCALE), but
 * in landscape orientation it acts as **Screen Width (wDP)**.
 * Usage example: `32.hemLw(context)`.
 */
fun Number.hemLw(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = false, inverter = Inverter.PH_TO_LW)
fun Number.hemLwa(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = false, inverter = Inverter.PH_TO_LW, applyAspectRatio = true)
fun Number.hemLwi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = false, inverter = Inverter.PH_TO_LW, ignoreMultiWindows = true)
fun Number.hemLwia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = false, inverter = Inverter.PH_TO_LW, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Height (hDP)** (WITHOUT FONT SCALE), but
 * in portrait orientation it acts as **Screen Width (wDP)**.
 * Usage example: `32.hemPw(context)`.
 */
fun Number.hemPw(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = false, inverter = Inverter.LH_TO_PW)
fun Number.hemPwa(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = false, inverter = Inverter.LH_TO_PW, applyAspectRatio = true)
fun Number.hemPwi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = false, inverter = Inverter.LH_TO_PW, ignoreMultiWindows = true)
fun Number.hemPwia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale = false, inverter = Inverter.LH_TO_PW, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Width (wDP)** (WITHOUT FONT SCALE).
 * Usage example: `100.wei(context)`.
 */
fun Number.wei(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = false)
fun Number.weia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = false, applyAspectRatio = true)
fun Number.weii(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = false, ignoreMultiWindows = true)
fun Number.weiia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = false, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Width (wDP)** (WITHOUT FONT SCALE), but
 * in landscape orientation it acts as **Screen Height (hDP)**.
 * Usage example: `100.wemLh(context)`.
 */
fun Number.wemLh(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = false, inverter = Inverter.PW_TO_LH)
fun Number.wemLha(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = false, inverter = Inverter.PW_TO_LH, applyAspectRatio = true)
fun Number.wemLhi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = false, inverter = Inverter.PW_TO_LH, ignoreMultiWindows = true)
fun Number.wemLhia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = false, inverter = Inverter.PW_TO_LH, ignoreMultiWindows = true, applyAspectRatio = true)

/**
 * EN
 * Extension for Int with dynamic scaling based on the **Screen Width (wDP)** (WITHOUT FONT SCALE), but
 * in portrait orientation it acts as **Screen Height (hDP)**.
 * Usage example: `100.dwemPh(context)`.
 */
fun Number.dwemPh(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = false, inverter = Inverter.LW_TO_PH)
fun Number.dwemPha(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = false, inverter = Inverter.LW_TO_PH, applyAspectRatio = true)
fun Number.dwemPhi(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = false, inverter = Inverter.LW_TO_PH, ignoreMultiWindows = true)
fun Number.dwemPhia(context: AppDimensContext): Float = this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale = false, inverter = Inverter.LW_TO_PH, ignoreMultiWindows = true, applyAspectRatio = true)

// EN Qualifier-based conditional dynamic scaling for Sp.
// PT Escalonamento condicional baseado em qualificador para Sp.

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swSP)**.
 * Uses the base value by default, but when the screen metric for [qualifierType]
 * is >= [qualifierValue], it uses [qualifiedValue] instead.
 *
 * PT
 * Extensão para Int com dimensionamento dinâmico baseado na **Smallest Width (swSP)**.
 * Usa o valor base por padrão, mas quando a métrica de tela para [qualifierType]
 * é >= [qualifierValue], usa [qualifiedValue] no lugar.
 */
fun Number.dsspQualifier(context: AppDimensContext, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float {
    val configuration = context.configuration
    val qualifierMatch = getQualifierValue(qualifierType, configuration) >= qualifierValue.toFloat()
    return if (qualifierMatch) {
        qualifiedValue.toDynamicDensitySpPx(context, finalQualifierResolver ?: DpQualifier.SMALL_WIDTH, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

/**
 * EN
 * Extension for Int with dynamic scaling based on **Screen Height (hSP)**.
 * Uses the base value by default, but when the screen metric for [qualifierType]
 * is >= [qualifierValue], it uses [qualifiedValue] instead.
 *
 * PT
 * Extensão para Int com dimensionamento dinâmico baseado na **Altura da Tela (hSP)**.
 * Usa o valor base por padrão, mas quando a métrica de tela para [qualifierType]
 * é >= [qualifierValue], usa [qualifiedValue] no lugar.
 */
fun Number.dhspQualifier(context: AppDimensContext, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float {
    val configuration = context.configuration
    val qualifierMatch = getQualifierValue(qualifierType, configuration) >= qualifierValue.toFloat()
    return if (qualifierMatch) {
        qualifiedValue.toDynamicDensitySpPx(context, finalQualifierResolver ?: DpQualifier.HEIGHT, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

/**
 * EN
 * Extension for Int with dynamic scaling based on **Screen Width (wSP)**.
 * Uses the base value by default, but when the screen metric for [qualifierType]
 * is >= [qualifierValue], it uses [qualifiedValue] instead.
 *
 * PT
 * Extensão para Int com dimensionamento dinâmico baseado na **Largura da Tela (wSP)**.
 * Usa o valor base por padrão, mas quando a métrica de tela para [qualifierType]
 * é >= [qualifierValue], usa [qualifiedValue] no lugar.
 */
fun Number.dwspQualifier(context: AppDimensContext, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float {
    val configuration = context.configuration
    val qualifierMatch = getQualifierValue(qualifierType, configuration) >= qualifierValue.toFloat()
    return if (qualifierMatch) {
        qualifiedValue.toDynamicDensitySpPx(context, finalQualifierResolver ?: DpQualifier.WIDTH, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

// EN UiModeType + DpQualifier combined facilitator extensions for Sp.
// PT Extensões facilitadoras combinadas UiModeType + DpQualifier para Sp.

/**
 * EN
 * Extension for Int with dynamic scaling based on **Smallest Width (swSP)**.
 * Uses the base value by default, but when the device matches [uiModeType] AND
 * the screen metric for [qualifierType] is >= [qualifierValue], it uses [screenValue] instead.
 *
 * PT
 * Extensão para Int com dimensionamento dinâmico baseado na **Smallest Width (swSP)**.
 * Usa o valor base por padrão, mas quando o dispositivo corresponde ao [uiModeType] E
 * a métrica de tela para [qualifierType] é >= [qualifierValue], usa [screenValue] no lugar.
 */
fun Number.dsspScreen(context: AppDimensContext, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float {
    val configuration = context.configuration
    val currentUiModeType = DimenCache.getCachedUiModeType(context)
    val uiModeMatch = currentUiModeType == uiModeType
    val qualifierMatch = getQualifierValue(qualifierType, configuration) >= qualifierValue.toFloat()
    return if (uiModeMatch && qualifierMatch) {
        screenValue.toDynamicDensitySpPx(context, finalQualifierResolver ?: DpQualifier.SMALL_WIDTH, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDensitySpPx(context, DpQualifier.SMALL_WIDTH, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

/**
 * EN
 * Extension for Int with dynamic scaling based on **Screen Height (hSP)**.
 * Uses the base value by default, but when the device matches [uiModeType] AND
 * the screen metric for [qualifierType] is >= [qualifierValue], it uses [screenValue] instead.
 *
 * PT
 * Extensão para Int com dimensionamento dinâmico baseado na **Altura da Tela (hSP)**.
 * Usa o valor base por padrão, mas quando o dispositivo corresponde ao [uiModeType] E
 * a métrica de tela para [qualifierType] é >= [qualifierValue], usa [screenValue] no lugar.
 */
fun Number.dhspScreen(context: AppDimensContext, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float {
    val configuration = context.configuration
    val currentUiModeType = DimenCache.getCachedUiModeType(context)
    val uiModeMatch = currentUiModeType == uiModeType
    val qualifierMatch = getQualifierValue(qualifierType, configuration) >= qualifierValue.toFloat()
    return if (uiModeMatch && qualifierMatch) {
        screenValue.toDynamicDensitySpPx(context, finalQualifierResolver ?: DpQualifier.HEIGHT, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDensitySpPx(context, DpQualifier.HEIGHT, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

/**
 * EN
 * Extension for Int with dynamic scaling based on **Screen Width (wSP)**.
 * Uses the base value by default, but when the device matches [uiModeType] AND
 * the screen metric for [qualifierType] is >= [qualifierValue], it uses [screenValue] instead.
 *
 * PT
 * Extensão para Int com dimensionamento dinâmico baseado na **Largura da Tela (wSP)**.
 * Usa o valor base por padrão, mas quando o dispositivo corresponde ao [uiModeType] E
 * a métrica de tela para [qualifierType] é >= [qualifierValue], usa [screenValue] no lugar.
 */
fun Number.dwspScreen(context: AppDimensContext, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float {
    val configuration = context.configuration
    val currentUiModeType = DimenCache.getCachedUiModeType(context)
    val uiModeMatch = currentUiModeType == uiModeType
    val qualifierMatch = getQualifierValue(qualifierType, configuration) >= qualifierValue.toFloat()
    return if (uiModeMatch && qualifierMatch) {
        screenValue.toDynamicDensitySpPx(context, finalQualifierResolver ?: DpQualifier.WIDTH, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    } else {
        this.toDynamicDensitySpPx(context, DpQualifier.WIDTH, fontScale, ignoreMultiWindows = ignoreMultiWindows, applyAspectRatio = applyAspectRatio, customSensitivityK = customSensitivityK)
    }
}

// EN Dynamic scaling function for Sp (Resource-based).
// PT Função de dimensionamento dinâmico para Sp (baseada em recursos).

/**
 * EN
 * Converts an Int (the base Sp value) into a dynamically scaled pixel value (Float).
 *
 * Sp→px uses `scaledSp * density * fontScale` when respecting font scale (equivalent to
 * [android.util.TypedValue.applyDimension] for `COMPLEX_UNIT_SP`), else `scaledSp * density`
 * for the fixed-Sp path. For many lookups, prefer [DimenCache.getBatch]; for early initialization,
 * [DimenDensitySp.warmupCache].
 *
 * PT
 * Converte um Int (o valor base de Sp) em um valor de pixel dinamicamente escalado (Float).
 *
 * @param context The Android context to access configuration and density.
 * @param qualifier The screen qualifier used for scaling (sw, h, w).
 * @param fontScale Whether to respect the system font scale.
 * @return The scaled pixel value.
 */
fun Number.toDynamicDensitySpPx(
    context: AppDimensContext,
    qualifier: DpQualifier,
    fontScale: Boolean = true,
    inverter: Inverter = Inverter.DEFAULT,
    ignoreMultiWindows: Boolean = false,
    applyAspectRatio: Boolean = false,
    customSensitivityK: Float? = null
): Float {
    val base = this.toFloat()
    val configuration = context.configuration

    val density = context.density

    val valueType = if (fontScale) DimenCache.ValueType.SP_PX_WITH_SCALE else DimenCache.ValueType.SP_PX_NO_SCALE

    val cacheKey = DimenCache.buildKey(
        baseValue = base,
        isLandscape = configuration.orientation == ScreenConfiguration.ORIENTATION_LANDSCAPE,
        ignoreMultiWindows = ignoreMultiWindows,
        calcType = DimenCache.CalcType.DENSITY,
        qualifier = qualifier,
        inverter = inverter,
        applyAspectRatio = applyAspectRatio,
        valueType = valueType,
        customSensitivityK = customSensitivityK
    )

    return DimenCache.getOrPut(cacheKey, context) {
        val scaledSp = calculateDensitySp(base, configuration, qualifier, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        if (fontScale) {
            val fs = configuration.fontScale
            scaledSp * density * if (fs > 0f) fs else 1f
        } else {
            scaledSp * density
        }
    }
}

/**
 * EN Internal logic to calculate the scaled SP value (similar to DP calculation but conceptually for text).
 */
private fun calculateDensitySp(
    baseValue: Float,
    configuration: ScreenConfiguration,
    qualifier: DpQualifier,
    inverter: Inverter,
    ignoreMultiWindows: Boolean,
    applyAspectRatio: Boolean,
    customSensitivityK: Float?
): Float = calculateDensityDp(baseValue, configuration, qualifier, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)


/**
 * EN Converts an Int (base Sp) to a dynamically scaled Sp value (as Float).
 *
 * @param context The Android context to access configuration and density.
 * @param qualifier The screen qualifier used for scaling (sw, h, w).
 * @param fontScale Whether to respect the system font scale.
 * @param inverter The inverter logic to apply.
 * @param ignoreMultiWindows Whether to ignore multi-window mode.
 * @param applyAspectRatio If `true`, applies the aspect-ratio multiplier.
 * @param customSensitivityK Override for the AR sensitivity constant (null = library default).
 * @return The scaled Sp value as [Float].
 *
 * **Bulk / init:** see [toDynamicDensitySpPx] for [DimenCache.getBatch] and [DimenDensitySp.warmupCache].
 */
fun Number.toDynamicDensitySp(
    context: AppDimensContext,
    qualifier: DpQualifier,
    fontScale: Boolean = true,
    inverter: Inverter = Inverter.DEFAULT,
    ignoreMultiWindows: Boolean = false,
    applyAspectRatio: Boolean = false,
    customSensitivityK: Float? = null
): Float {
    val base = this.toFloat()
    val configuration = context.configuration

    val valueType = if (fontScale) DimenCache.ValueType.SP_WITH_SCALE else DimenCache.ValueType.SP_NO_SCALE

    val cacheKey = DimenCache.buildKey(
        baseValue = base,
        isLandscape = configuration.orientation == ScreenConfiguration.ORIENTATION_LANDSCAPE,
        ignoreMultiWindows = ignoreMultiWindows,
        calcType = DimenCache.CalcType.DENSITY,
        qualifier = qualifier,
        inverter = inverter,
        applyAspectRatio = applyAspectRatio,
        valueType = valueType,
        customSensitivityK = customSensitivityK
    )

    return DimenCache.getOrPut(cacheKey, context) {
        val raw = calculateDensitySp(base, configuration, qualifier, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
        if (fontScale) {
            raw
        } else {
            val scale = configuration.fontScale
            if (scale > 0f) raw / scale else raw
        }
    }
}