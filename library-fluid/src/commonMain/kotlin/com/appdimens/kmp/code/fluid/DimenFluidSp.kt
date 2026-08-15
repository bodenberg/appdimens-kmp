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
package com.appdimens.kmp.code.fluid

import com.appdimens.kmp.core.AppDimensContext
import com.appdimens.kmp.core.ScreenConfiguration
import com.appdimens.kmp.core.currentScreenConfiguration
import com.appdimens.kmp.core.localAppDimensContext

import com.appdimens.kmp.common.DpQualifier
import com.appdimens.kmp.common.Inverter
import com.appdimens.kmp.common.Orientation
import com.appdimens.kmp.common.UiModeType
import com.appdimens.kmp.core.DimenCache
import kotlin.math.max
import kotlin.math.min

/**
 * EN
 * Utility object for handling SSP (Scalable Sp) dimensions from Java.
 *
 * PT
 * Objeto utilitário para manipulação de dimensões SSP (Scalable Sp) no Java.
 */
object DimenFluidSp {

    /**
     * EN Eagerly initializes [DimenCache] so the first resolution on a hot path avoids lazy-init work.
     * PT Inicializa o [DimenCache] antecipadamente para evitar custo lazy no primeiro uso.
     */
    fun warmupCache(context: AppDimensContext) {
        DimenCache.init(context)
    }

    /**
     * EN Quick resolution for Smallest Width (fssp).
     * PT Resolução rápida para Smallest Width (fssp).
     */
    fun fssp(context: AppDimensContext, value: Int): Float = value.fssp(context)

    fun fsspa(context: AppDimensContext, value: Int): Float = value.fsspa(context)

    fun fsspi(context: AppDimensContext, value: Int): Float = value.fsspi(context)

    fun fsspia(context: AppDimensContext, value: Int): Float = value.fsspia(context)

    /**
     * EN Quick resolution for Smallest Width (fssp), but in portrait orientation it acts as Screen Height (fhsp).
     */
    fun fsspPh(context: AppDimensContext, value: Int): Float = value.fsspPh(context)

    fun fsspPha(context: AppDimensContext, value: Int): Float = value.fsspPha(context)

    fun fsspPhi(context: AppDimensContext, value: Int): Float = value.fsspPhi(context)

    fun fsspPhia(context: AppDimensContext, value: Int): Float = value.fsspPhia(context)

    /**
     * EN Quick resolution for Smallest Width (fssp), but in landscape orientation it acts as Screen Height (fhsp).
     */
    fun fsspLh(context: AppDimensContext, value: Int): Float = value.fsspLh(context)

    fun fsspLha(context: AppDimensContext, value: Int): Float = value.fsspLha(context)

    fun fsspLhi(context: AppDimensContext, value: Int): Float = value.fsspLhi(context)

    fun fsspLhia(context: AppDimensContext, value: Int): Float = value.fsspLhia(context)

    /**
     * EN Quick resolution for Smallest Width (fssp), but in portrait orientation it acts as Screen Width (fwsp).
     */
    fun fsspPw(context: AppDimensContext, value: Int): Float = value.fsspPw(context)

    fun fsspPwa(context: AppDimensContext, value: Int): Float = value.fsspPwa(context)

    fun fsspPwi(context: AppDimensContext, value: Int): Float = value.fsspPwi(context)

    fun fsspPwia(context: AppDimensContext, value: Int): Float = value.fsspPwia(context)

    /**
     * EN Quick resolution for Smallest Width (fssp), but in landscape orientation it acts as Screen Width (fwsp).
     */
    fun fsspLw(context: AppDimensContext, value: Int): Float = value.fsspLw(context)

    fun fsspLwa(context: AppDimensContext, value: Int): Float = value.fsspLwa(context)

    fun fsspLwi(context: AppDimensContext, value: Int): Float = value.fsspLwi(context)

    fun fsspLwia(context: AppDimensContext, value: Int): Float = value.fsspLwia(context)

    /**
     * EN Quick resolution for Screen Height (fhsp).
     * PT Resolução rápida para Altura da Tela (fhsp).
     */
    fun fhsp(context: AppDimensContext, value: Int): Float = value.fhsp(context)

    fun fhspa(context: AppDimensContext, value: Int): Float = value.fhspa(context)

    fun fhspi(context: AppDimensContext, value: Int): Float = value.fhspi(context)

    fun fhspia(context: AppDimensContext, value: Int): Float = value.fhspia(context)

    /**
     * EN Quick resolution for Screen Height (fhsp), but in landscape orientation it acts as Screen Width (fwsp).
     */
    fun fhspLw(context: AppDimensContext, value: Int): Float = value.fhspLw(context)

    fun fhspLwa(context: AppDimensContext, value: Int): Float = value.fhspLwa(context)

    fun fhspLwi(context: AppDimensContext, value: Int): Float = value.fhspLwi(context)

    fun fhspLwia(context: AppDimensContext, value: Int): Float = value.fhspLwia(context)

    /**
     * EN Quick resolution for Screen Height (fhsp), but in portrait orientation it acts as Screen Width (fwsp).
     */
    fun fhspPw(context: AppDimensContext, value: Int): Float = value.fhspPw(context)

    fun fhspPwa(context: AppDimensContext, value: Int): Float = value.fhspPwa(context)

    fun fhspPwi(context: AppDimensContext, value: Int): Float = value.fhspPwi(context)

    fun fhspPwia(context: AppDimensContext, value: Int): Float = value.fhspPwia(context)

    /**
     * EN Quick resolution for Screen Width (fwsp).
     * PT Resolução rápida para Largura da Tela (fwsp).
     */
    fun fwsp(context: AppDimensContext, value: Int): Float = value.fwsp(context)

    fun fwspa(context: AppDimensContext, value: Int): Float = value.fwspa(context)

    fun fwspi(context: AppDimensContext, value: Int): Float = value.fwspi(context)

    fun fwspia(context: AppDimensContext, value: Int): Float = value.fwspia(context)

    /**
     * EN Quick resolution for Screen Width (fwsp), but in landscape orientation it acts as Screen Height (fhsp).
     */
    fun fwspLh(context: AppDimensContext, value: Int): Float = value.fwspLh(context)

    fun fwspLha(context: AppDimensContext, value: Int): Float = value.fwspLha(context)

    fun fwspLhi(context: AppDimensContext, value: Int): Float = value.fwspLhi(context)

    fun fwspLhia(context: AppDimensContext, value: Int): Float = value.fwspLhia(context)

    /**
     * EN Quick resolution for Screen Width (fwsp), but in portrait orientation it acts as Screen Height (fhsp).
     */
    fun fwspPh(context: AppDimensContext, value: Int): Float = value.fwspPh(context)

    fun fwspPha(context: AppDimensContext, value: Int): Float = value.fwspPha(context)

    fun fwspPhi(context: AppDimensContext, value: Int): Float = value.fwspPhi(context)

    fun fwspPhia(context: AppDimensContext, value: Int): Float = value.fwspPhia(context)


    // EN WITHOUT FONT SCALE variants
    // PT Variantes SEM ESCALA DE FONTE

    /**
     * EN Quick resolution for Smallest Width (sei) - Without font scale.
     * PT Resolução rápida para Smallest Width (sei) - Sem escala de fonte.
     */
    fun sei(context: AppDimensContext, value: Int): Float = value.sei(context)

    fun seia(context: AppDimensContext, value: Int): Float = value.seia(context)

    fun seii(context: AppDimensContext, value: Int): Float = value.seii(context)

    fun seiia(context: AppDimensContext, value: Int): Float = value.seiia(context)

    /**
     * EN Quick resolution for Smallest Width without font scale, portrait is Screen Height.
     */
    fun semPh(context: AppDimensContext, value: Int): Float = value.semPh(context)

    fun semPha(context: AppDimensContext, value: Int): Float = value.semPha(context)

    fun semPhi(context: AppDimensContext, value: Int): Float = value.semPhi(context)

    fun semPhia(context: AppDimensContext, value: Int): Float = value.semPhia(context)

    /**
     * EN Quick resolution for Smallest Width without font scale, landscape is Screen Height.
     */
    fun semLh(context: AppDimensContext, value: Int): Float = value.semLh(context)

    fun semLha(context: AppDimensContext, value: Int): Float = value.semLha(context)

    fun semLhi(context: AppDimensContext, value: Int): Float = value.semLhi(context)

    fun semLhia(context: AppDimensContext, value: Int): Float = value.semLhia(context)

    /**
     * EN Quick resolution for Smallest Width without font scale, portrait is Screen Width.
     */
    fun semPw(context: AppDimensContext, value: Int): Float = value.semPw(context)

    fun semPwa(context: AppDimensContext, value: Int): Float = value.semPwa(context)

    fun semPwi(context: AppDimensContext, value: Int): Float = value.semPwi(context)

    fun semPwia(context: AppDimensContext, value: Int): Float = value.semPwia(context)

    /**
     * EN Quick resolution for Smallest Width without font scale, landscape is Screen Width.
     */
    fun semLw(context: AppDimensContext, value: Int): Float = value.semLw(context)

    fun semLwa(context: AppDimensContext, value: Int): Float = value.semLwa(context)

    fun semLwi(context: AppDimensContext, value: Int): Float = value.semLwi(context)

    fun semLwia(context: AppDimensContext, value: Int): Float = value.semLwia(context)

    /**
     * EN Quick resolution for Screen Height without font scale.
     */
    fun hei(context: AppDimensContext, value: Int): Float = value.hei(context)

    fun heia(context: AppDimensContext, value: Int): Float = value.heia(context)

    fun heii(context: AppDimensContext, value: Int): Float = value.heii(context)

    fun heiia(context: AppDimensContext, value: Int): Float = value.heiia(context)

    /**
     * EN Quick resolution for Screen Height without font scale, landscape is Screen Width.
     */
    fun hemLw(context: AppDimensContext, value: Int): Float = value.hemLw(context)

    fun hemLwa(context: AppDimensContext, value: Int): Float = value.hemLwa(context)

    fun hemLwi(context: AppDimensContext, value: Int): Float = value.hemLwi(context)

    fun hemLwia(context: AppDimensContext, value: Int): Float = value.hemLwia(context)

    /**
     * EN Quick resolution for Screen Height without font scale, portrait is Screen Width.
     */
    fun hemPw(context: AppDimensContext, value: Int): Float = value.hemPw(context)

    fun hemPwa(context: AppDimensContext, value: Int): Float = value.hemPwa(context)

    fun hemPwi(context: AppDimensContext, value: Int): Float = value.hemPwi(context)

    fun hemPwia(context: AppDimensContext, value: Int): Float = value.hemPwia(context)

    /**
     * EN Quick resolution for Screen Width without font scale.
     */
    fun wei(context: AppDimensContext, value: Int): Float = value.wei(context)

    fun weia(context: AppDimensContext, value: Int): Float = value.weia(context)

    fun weii(context: AppDimensContext, value: Int): Float = value.weii(context)

    fun weiia(context: AppDimensContext, value: Int): Float = value.weiia(context)

    /**
     * EN Quick resolution for Screen Width without font scale, landscape is Screen Height.
     */
    fun wemLh(context: AppDimensContext, value: Int): Float = value.wemLh(context)

    fun wemLha(context: AppDimensContext, value: Int): Float = value.wemLha(context)

    fun wemLhi(context: AppDimensContext, value: Int): Float = value.wemLhi(context)

    fun wemLhia(context: AppDimensContext, value: Int): Float = value.wemLhia(context)

    /**
     * EN Quick resolution for Screen Width without font scale, portrait is Screen Height.
     */
    fun fwemPh(context: AppDimensContext, value: Int): Float = value.fwemPh(context)

    fun fwemPha(context: AppDimensContext, value: Int): Float = value.fwemPha(context)

    fun fwemPhi(context: AppDimensContext, value: Int): Float = value.fwemPhi(context)

    fun fwemPhia(context: AppDimensContext, value: Int): Float = value.fwemPhia(context)

    /**
     * EN Generic scaling function for Java (PX).
     * PT Função de escala genérica para Java (PX).
     */
    fun getDimensionInPx(
        context: AppDimensContext,
        qualifier: DpQualifier,
        value: Int,
        fontScale: Boolean = true,
        inverter: Inverter = Inverter.DEFAULT,
        ignoreMultiWindows: Boolean = false,
        applyAspectRatio: Boolean = false,
        customSensitivityK: Float? = null
    ): Float = value.toDynamicFluidSpPx(context, qualifier, fontScale, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Generic scaling function for Java (SP value).
     * PT Função de escala genérica para Java (valor SP).
     */
    fun getDimensionInSp(
        context: AppDimensContext,
        qualifier: DpQualifier,
        value: Int,
        fontScale: Boolean = true,
        inverter: Inverter = Inverter.DEFAULT,
        ignoreMultiWindows: Boolean = false,
        applyAspectRatio: Boolean = false,
        customSensitivityK: Float? = null
    ): Float = value.toDynamicFluidSp(context, qualifier, fontScale, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Starts the build chain for the custom dimension FluidSp from a base Int.
     * PT Inicia a cadeia de construção para a dimensão customizada FluidSp a partir de um Int base.
     */
    fun scaled(initialBaseValue: Int): FluidSp = FluidSp(initialBaseValue)

    /**
     * EN Starts the build chain for the custom dimension FluidSp from a base Float.
     * PT Inicia a cadeia de construção para a dimensão customizada FluidSp a partir de um Float base.
     */
    fun scaled(initialBaseValue: Float): FluidSp = FluidSp(initialBaseValue)

    // EN Qualifier-based conditional dynamic scaling for Sp.
    // PT Escalonamento condicional baseado em qualificador para Sp.

    /**
     * EN Quick resolution for Smallest Width (swSP) conditional scaling.
     */
    fun fsspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.fsspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hSP) conditional scaling.
     */
    fun fhspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.fhspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wSP) conditional scaling.
     */
    fun fwspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.fwspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType + DpQualifier combined facilitator extensions for Sp.
    // PT Extensões facilitadoras combinadas UiModeType + DpQualifier para Sp.

    /**
     * EN Quick resolution for Smallest Width (swSP) context conditional scaling.
     */
    fun fsspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.fsspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hSP) context conditional scaling.
     */
    fun fhspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.fhspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wSP) context conditional scaling.
     */
    fun fwspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.fwspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN Rotation facilitator functions for Java.
    // PT Funções facilitadoras de rotação para Java.

    /**
     * EN Facilitator for Smallest Width (fssp) with rotation override.
     */
    fun fsspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.SMALL_WIDTH, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.fsspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (fhsp) with rotation override.
     */
    fun fhspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.HEIGHT, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.fhspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (fwsp) with rotation override.
     */
    fun fwspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.WIDTH, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.fwspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType facilitator functions for Java.
    // PT Funções facilitadoras de UiModeType para Java.

    /**
     * EN Facilitator for Smallest Width (fssp) with UiModeType override.
     */
    fun fsspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.fsspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (fhsp) with UiModeType override.
     */
    fun fhspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.fhspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (fwsp) with UiModeType override.
     */
    fun fwspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.fwspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
}