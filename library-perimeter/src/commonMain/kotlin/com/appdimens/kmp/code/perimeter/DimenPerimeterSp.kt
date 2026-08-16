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
package com.appdimens.kmp.code.perimeter

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
object DimenPerimeterSp {

    /**
     * EN Eagerly initializes [DimenCache] so the first resolution on a hot path avoids lazy-init work.
     * PT Inicializa o [DimenCache] antecipadamente para evitar custo lazy no primeiro uso.
     */
    fun warmupCache(context: AppDimensContext) {
        DimenCache.init(context)
    }

    /**
     * EN Quick resolution for Smallest Width (prssp).
     * PT Resolução rápida para Smallest Width (prssp).
     */
    fun prssp(context: AppDimensContext, value: Int): Float = value.prssp(context)

    fun prsspa(context: AppDimensContext, value: Int): Float = value.prsspa(context)

    fun prsspi(context: AppDimensContext, value: Int): Float = value.prsspi(context)

    fun prsspia(context: AppDimensContext, value: Int): Float = value.prsspia(context)

    /**
     * EN Quick resolution for Smallest Width (prssp), but in portrait orientation it acts as Screen Height (prhsp).
     */
    fun prsspPh(context: AppDimensContext, value: Int): Float = value.prsspPh(context)

    fun prsspPha(context: AppDimensContext, value: Int): Float = value.prsspPha(context)

    fun prsspPhi(context: AppDimensContext, value: Int): Float = value.prsspPhi(context)

    fun prsspPhia(context: AppDimensContext, value: Int): Float = value.prsspPhia(context)

    /**
     * EN Quick resolution for Smallest Width (prssp), but in landscape orientation it acts as Screen Height (prhsp).
     */
    fun prsspLh(context: AppDimensContext, value: Int): Float = value.prsspLh(context)

    fun prsspLha(context: AppDimensContext, value: Int): Float = value.prsspLha(context)

    fun prsspLhi(context: AppDimensContext, value: Int): Float = value.prsspLhi(context)

    fun prsspLhia(context: AppDimensContext, value: Int): Float = value.prsspLhia(context)

    /**
     * EN Quick resolution for Smallest Width (prssp), but in portrait orientation it acts as Screen Width (prwsp).
     */
    fun prsspPw(context: AppDimensContext, value: Int): Float = value.prsspPw(context)

    fun prsspPwa(context: AppDimensContext, value: Int): Float = value.prsspPwa(context)

    fun prsspPwi(context: AppDimensContext, value: Int): Float = value.prsspPwi(context)

    fun prsspPwia(context: AppDimensContext, value: Int): Float = value.prsspPwia(context)

    /**
     * EN Quick resolution for Smallest Width (prssp), but in landscape orientation it acts as Screen Width (prwsp).
     */
    fun prsspLw(context: AppDimensContext, value: Int): Float = value.prsspLw(context)

    fun prsspLwa(context: AppDimensContext, value: Int): Float = value.prsspLwa(context)

    fun prsspLwi(context: AppDimensContext, value: Int): Float = value.prsspLwi(context)

    fun prsspLwia(context: AppDimensContext, value: Int): Float = value.prsspLwia(context)

    /**
     * EN Quick resolution for Screen Height (prhsp).
     * PT Resolução rápida para Altura da Tela (prhsp).
     */
    fun prhsp(context: AppDimensContext, value: Int): Float = value.prhsp(context)

    fun prhspa(context: AppDimensContext, value: Int): Float = value.prhspa(context)

    fun prhspi(context: AppDimensContext, value: Int): Float = value.prhspi(context)

    fun prhspia(context: AppDimensContext, value: Int): Float = value.prhspia(context)

    /**
     * EN Quick resolution for Screen Height (prhsp), but in landscape orientation it acts as Screen Width (prwsp).
     */
    fun prhspLw(context: AppDimensContext, value: Int): Float = value.prhspLw(context)

    fun prhspLwa(context: AppDimensContext, value: Int): Float = value.prhspLwa(context)

    fun prhspLwi(context: AppDimensContext, value: Int): Float = value.prhspLwi(context)

    fun prhspLwia(context: AppDimensContext, value: Int): Float = value.prhspLwia(context)

    /**
     * EN Quick resolution for Screen Height (prhsp), but in portrait orientation it acts as Screen Width (prwsp).
     */
    fun prhspPw(context: AppDimensContext, value: Int): Float = value.prhspPw(context)

    fun prhspPwa(context: AppDimensContext, value: Int): Float = value.prhspPwa(context)

    fun prhspPwi(context: AppDimensContext, value: Int): Float = value.prhspPwi(context)

    fun prhspPwia(context: AppDimensContext, value: Int): Float = value.prhspPwia(context)

    /**
     * EN Quick resolution for Screen Width (prwsp).
     * PT Resolução rápida para Largura da Tela (prwsp).
     */
    fun prwsp(context: AppDimensContext, value: Int): Float = value.prwsp(context)

    fun prwspa(context: AppDimensContext, value: Int): Float = value.prwspa(context)

    fun prwspi(context: AppDimensContext, value: Int): Float = value.prwspi(context)

    fun prwspia(context: AppDimensContext, value: Int): Float = value.prwspia(context)

    /**
     * EN Quick resolution for Screen Width (prwsp), but in landscape orientation it acts as Screen Height (prhsp).
     */
    fun prwspLh(context: AppDimensContext, value: Int): Float = value.prwspLh(context)

    fun prwspLha(context: AppDimensContext, value: Int): Float = value.prwspLha(context)

    fun prwspLhi(context: AppDimensContext, value: Int): Float = value.prwspLhi(context)

    fun prwspLhia(context: AppDimensContext, value: Int): Float = value.prwspLhia(context)

    /**
     * EN Quick resolution for Screen Width (prwsp), but in portrait orientation it acts as Screen Height (prhsp).
     */
    fun prwspPh(context: AppDimensContext, value: Int): Float = value.prwspPh(context)

    fun prwspPha(context: AppDimensContext, value: Int): Float = value.prwspPha(context)

    fun prwspPhi(context: AppDimensContext, value: Int): Float = value.prwspPhi(context)

    fun prwspPhia(context: AppDimensContext, value: Int): Float = value.prwspPhia(context)


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
    fun prwemPh(context: AppDimensContext, value: Int): Float = value.prwemPh(context)

    fun prwemPha(context: AppDimensContext, value: Int): Float = value.prwemPha(context)

    fun prwemPhi(context: AppDimensContext, value: Int): Float = value.prwemPhi(context)

    fun prwemPhia(context: AppDimensContext, value: Int): Float = value.prwemPhia(context)

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
    ): Float = value.toDynamicPerimeterSpPx(context, qualifier, fontScale, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

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
    ): Float = value.toDynamicPerimeterSp(context, qualifier, fontScale, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Starts the build chain for the custom dimension PerimeterSp from a base Int.
     * PT Inicia a cadeia de construção para a dimensão customizada PerimeterSp a partir de um Int base.
     */
    fun scaled(initialBaseValue: Int): PerimeterSp = PerimeterSp(initialBaseValue)

    /**
     * EN Starts the build chain for the custom dimension PerimeterSp from a base Float.
     * PT Inicia a cadeia de construção para a dimensão customizada PerimeterSp a partir de um Float base.
     */
    fun scaled(initialBaseValue: Float): PerimeterSp = PerimeterSp(initialBaseValue)

    // EN Qualifier-based conditional dynamic scaling for Sp.
    // PT Escalonamento condicional baseado em qualificador para Sp.

    /**
     * EN Quick resolution for Smallest Width (swSP) conditional scaling.
     */
    fun prsspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.prsspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hSP) conditional scaling.
     */
    fun prhspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.prhspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wSP) conditional scaling.
     */
    fun prwspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.prwspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType + DpQualifier combined facilitator extensions for Sp.
    // PT Extensões facilitadoras combinadas UiModeType + DpQualifier para Sp.

    /**
     * EN Quick resolution for Smallest Width (swSP) context conditional scaling.
     */
    fun prsspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.prsspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hSP) context conditional scaling.
     */
    fun prhspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.prhspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wSP) context conditional scaling.
     */
    fun prwspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.prwspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN Rotation facilitator functions for Java.
    // PT Funções facilitadoras de rotação para Java.

    /**
     * EN Facilitator for Smallest Width (prssp) with rotation override.
     */
    fun prsspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.SMALL_WIDTH, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.prsspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (prhsp) with rotation override.
     */
    fun prhspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.HEIGHT, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.prhspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (prwsp) with rotation override.
     */
    fun prwspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.WIDTH, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.prwspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType facilitator functions for Java.
    // PT Funções facilitadoras de UiModeType para Java.

    /**
     * EN Facilitator for Smallest Width (prssp) with UiModeType override.
     */
    fun prsspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.prsspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (prhsp) with UiModeType override.
     */
    fun prhspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.prhspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (prwsp) with UiModeType override.
     */
    fun prwspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.prwspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
}