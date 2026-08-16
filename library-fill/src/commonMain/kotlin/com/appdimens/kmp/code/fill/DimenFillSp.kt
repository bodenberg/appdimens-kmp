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
package com.appdimens.kmp.code.fill

import com.appdimens.kmp.core.AppDimensContext
import com.appdimens.kmp.core.ScreenConfiguration

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
object DimenFillSp {

    /**
     * EN Eagerly initializes [DimenCache] so the first resolution on a hot path avoids lazy-init work.
     * PT Inicializa o [DimenCache] antecipadamente para evitar custo lazy no primeiro uso.
     */
    fun warmupCache(context: AppDimensContext) {
        DimenCache.init(context)
    }

    /**
     * EN Quick resolution for Smallest Width (flssp).
     * PT Resolução rápida para Smallest Width (flssp).
     */
    fun flssp(context: AppDimensContext, value: Int): Float = value.flssp(context)

    fun flsspa(context: AppDimensContext, value: Int): Float = value.flsspa(context)

    fun flsspi(context: AppDimensContext, value: Int): Float = value.flsspi(context)

    fun flsspia(context: AppDimensContext, value: Int): Float = value.flsspia(context)

    /**
     * EN Quick resolution for Smallest Width (flssp), but in portrait orientation it acts as Screen Height (flhsp).
     */
    fun flsspPh(context: AppDimensContext, value: Int): Float = value.flsspPh(context)

    fun flsspPha(context: AppDimensContext, value: Int): Float = value.flsspPha(context)

    fun flsspPhi(context: AppDimensContext, value: Int): Float = value.flsspPhi(context)

    fun flsspPhia(context: AppDimensContext, value: Int): Float = value.flsspPhia(context)

    /**
     * EN Quick resolution for Smallest Width (flssp), but in landscape orientation it acts as Screen Height (flhsp).
     */
    fun flsspLh(context: AppDimensContext, value: Int): Float = value.flsspLh(context)

    fun flsspLha(context: AppDimensContext, value: Int): Float = value.flsspLha(context)

    fun flsspLhi(context: AppDimensContext, value: Int): Float = value.flsspLhi(context)

    fun flsspLhia(context: AppDimensContext, value: Int): Float = value.flsspLhia(context)

    /**
     * EN Quick resolution for Smallest Width (flssp), but in portrait orientation it acts as Screen Width (flwsp).
     */
    fun flsspPw(context: AppDimensContext, value: Int): Float = value.flsspPw(context)

    fun flsspPwa(context: AppDimensContext, value: Int): Float = value.flsspPwa(context)

    fun flsspPwi(context: AppDimensContext, value: Int): Float = value.flsspPwi(context)

    fun flsspPwia(context: AppDimensContext, value: Int): Float = value.flsspPwia(context)

    /**
     * EN Quick resolution for Smallest Width (flssp), but in landscape orientation it acts as Screen Width (flwsp).
     */
    fun flsspLw(context: AppDimensContext, value: Int): Float = value.flsspLw(context)

    fun flsspLwa(context: AppDimensContext, value: Int): Float = value.flsspLwa(context)

    fun flsspLwi(context: AppDimensContext, value: Int): Float = value.flsspLwi(context)

    fun flsspLwia(context: AppDimensContext, value: Int): Float = value.flsspLwia(context)

    /**
     * EN Quick resolution for Screen Height (flhsp).
     * PT Resolução rápida para Altura da Tela (flhsp).
     */
    fun flhsp(context: AppDimensContext, value: Int): Float = value.flhsp(context)

    fun flhspa(context: AppDimensContext, value: Int): Float = value.flhspa(context)

    fun flhspi(context: AppDimensContext, value: Int): Float = value.flhspi(context)

    fun flhspia(context: AppDimensContext, value: Int): Float = value.flhspia(context)

    /**
     * EN Quick resolution for Screen Height (flhsp), but in landscape orientation it acts as Screen Width (flwsp).
     */
    fun flhspLw(context: AppDimensContext, value: Int): Float = value.flhspLw(context)

    fun flhspLwa(context: AppDimensContext, value: Int): Float = value.flhspLwa(context)

    fun flhspLwi(context: AppDimensContext, value: Int): Float = value.flhspLwi(context)

    fun flhspLwia(context: AppDimensContext, value: Int): Float = value.flhspLwia(context)

    /**
     * EN Quick resolution for Screen Height (flhsp), but in portrait orientation it acts as Screen Width (flwsp).
     */
    fun flhspPw(context: AppDimensContext, value: Int): Float = value.flhspPw(context)

    fun flhspPwa(context: AppDimensContext, value: Int): Float = value.flhspPwa(context)

    fun flhspPwi(context: AppDimensContext, value: Int): Float = value.flhspPwi(context)

    fun flhspPwia(context: AppDimensContext, value: Int): Float = value.flhspPwia(context)

    /**
     * EN Quick resolution for Screen Width (flwsp).
     * PT Resolução rápida para Largura da Tela (flwsp).
     */
    fun flwsp(context: AppDimensContext, value: Int): Float = value.flwsp(context)

    fun flwspa(context: AppDimensContext, value: Int): Float = value.flwspa(context)

    fun flwspi(context: AppDimensContext, value: Int): Float = value.flwspi(context)

    fun flwspia(context: AppDimensContext, value: Int): Float = value.flwspia(context)

    /**
     * EN Quick resolution for Screen Width (flwsp), but in landscape orientation it acts as Screen Height (flhsp).
     */
    fun flwspLh(context: AppDimensContext, value: Int): Float = value.flwspLh(context)

    fun flwspLha(context: AppDimensContext, value: Int): Float = value.flwspLha(context)

    fun flwspLhi(context: AppDimensContext, value: Int): Float = value.flwspLhi(context)

    fun flwspLhia(context: AppDimensContext, value: Int): Float = value.flwspLhia(context)

    /**
     * EN Quick resolution for Screen Width (flwsp), but in portrait orientation it acts as Screen Height (flhsp).
     */
    fun flwspPh(context: AppDimensContext, value: Int): Float = value.flwspPh(context)

    fun flwspPha(context: AppDimensContext, value: Int): Float = value.flwspPha(context)

    fun flwspPhi(context: AppDimensContext, value: Int): Float = value.flwspPhi(context)

    fun flwspPhia(context: AppDimensContext, value: Int): Float = value.flwspPhia(context)


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
    fun flwemPh(context: AppDimensContext, value: Int): Float = value.flwemPh(context)

    fun flwemPha(context: AppDimensContext, value: Int): Float = value.flwemPha(context)

    fun flwemPhi(context: AppDimensContext, value: Int): Float = value.flwemPhi(context)

    fun flwemPhia(context: AppDimensContext, value: Int): Float = value.flwemPhia(context)

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
    ): Float = value.toDynamicFillSpPx(context, qualifier, fontScale, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

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
    ): Float = value.toDynamicFillSp(context, qualifier, fontScale, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Starts the build chain for the custom dimension FillSp from a base Int.
     * PT Inicia a cadeia de construção para a dimensão customizada FillSp a partir de um Int base.
     */
    fun scaled(initialBaseValue: Int): FillSp = FillSp(initialBaseValue)

    /**
     * EN Starts the build chain for the custom dimension FillSp from a base Float.
     * PT Inicia a cadeia de construção para a dimensão customizada FillSp a partir de um Float base.
     */
    fun scaled(initialBaseValue: Float): FillSp = FillSp(initialBaseValue)

    // EN Qualifier-based conditional dynamic scaling for Sp.
    // PT Escalonamento condicional baseado em qualificador para Sp.

    /**
     * EN Quick resolution for Smallest Width (swSP) conditional scaling.
     */
    fun flsspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flsspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hSP) conditional scaling.
     */
    fun flhspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flhspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wSP) conditional scaling.
     */
    fun flwspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flwspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType + DpQualifier combined facilitator extensions for Sp.
    // PT Extensões facilitadoras combinadas UiModeType + DpQualifier para Sp.

    /**
     * EN Quick resolution for Smallest Width (swSP) context conditional scaling.
     */
    fun flsspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flsspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hSP) context conditional scaling.
     */
    fun flhspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flhspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wSP) context conditional scaling.
     */
    fun flwspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flwspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN Rotation facilitator functions for Java.
    // PT Funções facilitadoras de rotação para Java.

    /**
     * EN Facilitator for Smallest Width (flssp) with rotation override.
     */
    fun flsspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.SMALL_WIDTH, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flsspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (flhsp) with rotation override.
     */
    fun flhspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.HEIGHT, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flhspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (flwsp) with rotation override.
     */
    fun flwspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.WIDTH, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flwspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType facilitator functions for Java.
    // PT Funções facilitadoras de UiModeType para Java.

    /**
     * EN Facilitator for Smallest Width (flssp) with UiModeType override.
     */
    fun flsspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flsspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (flhsp) with UiModeType override.
     */
    fun flhspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flhspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (flwsp) with UiModeType override.
     */
    fun flwspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flwspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
}