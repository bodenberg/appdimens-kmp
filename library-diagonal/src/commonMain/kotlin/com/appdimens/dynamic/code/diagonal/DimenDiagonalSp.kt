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
package com.appdimens.dynamic.code.diagonal

import com.appdimens.dynamic.core.AppDimensContext
import com.appdimens.dynamic.core.ScreenConfiguration
import com.appdimens.dynamic.core.currentScreenConfiguration
import com.appdimens.dynamic.core.localAppDimensContext

import com.appdimens.dynamic.common.DpQualifier
import com.appdimens.dynamic.common.Inverter
import com.appdimens.dynamic.common.Orientation
import com.appdimens.dynamic.common.UiModeType
import com.appdimens.dynamic.core.DimenCache
import kotlin.math.max
import kotlin.math.min

/**
 * EN
 * Utility object for handling SSP (Scalable Sp) dimensions from Java.
 *
 * PT
 * Objeto utilitário para manipulação de dimensões SSP (Scalable Sp) no Java.
 */
object DimenDiagonalSp {

    /**
     * EN Eagerly initializes [DimenCache] so the first resolution on a hot path avoids lazy-init work.
     * PT Inicializa o [DimenCache] antecipadamente para evitar custo lazy no primeiro uso.
     */
    fun warmupCache(context: AppDimensContext) {
        DimenCache.init(context)
    }

    /**
     * EN Quick resolution for Smallest Width (dgssp).
     * PT Resolução rápida para Smallest Width (dgssp).
     */
    fun dgssp(context: AppDimensContext, value: Int): Float = value.dgssp(context)

    fun dgsspa(context: AppDimensContext, value: Int): Float = value.dgsspa(context)

    fun dgsspi(context: AppDimensContext, value: Int): Float = value.dgsspi(context)

    fun dgsspia(context: AppDimensContext, value: Int): Float = value.dgsspia(context)

    /**
     * EN Quick resolution for Smallest Width (dgssp), but in portrait orientation it acts as Screen Height (dghsp).
     */
    fun dgsspPh(context: AppDimensContext, value: Int): Float = value.dgsspPh(context)

    fun dgsspPha(context: AppDimensContext, value: Int): Float = value.dgsspPha(context)

    fun dgsspPhi(context: AppDimensContext, value: Int): Float = value.dgsspPhi(context)

    fun dgsspPhia(context: AppDimensContext, value: Int): Float = value.dgsspPhia(context)

    /**
     * EN Quick resolution for Smallest Width (dgssp), but in landscape orientation it acts as Screen Height (dghsp).
     */
    fun dgsspLh(context: AppDimensContext, value: Int): Float = value.dgsspLh(context)

    fun dgsspLha(context: AppDimensContext, value: Int): Float = value.dgsspLha(context)

    fun dgsspLhi(context: AppDimensContext, value: Int): Float = value.dgsspLhi(context)

    fun dgsspLhia(context: AppDimensContext, value: Int): Float = value.dgsspLhia(context)

    /**
     * EN Quick resolution for Smallest Width (dgssp), but in portrait orientation it acts as Screen Width (dgwsp).
     */
    fun dgsspPw(context: AppDimensContext, value: Int): Float = value.dgsspPw(context)

    fun dgsspPwa(context: AppDimensContext, value: Int): Float = value.dgsspPwa(context)

    fun dgsspPwi(context: AppDimensContext, value: Int): Float = value.dgsspPwi(context)

    fun dgsspPwia(context: AppDimensContext, value: Int): Float = value.dgsspPwia(context)

    /**
     * EN Quick resolution for Smallest Width (dgssp), but in landscape orientation it acts as Screen Width (dgwsp).
     */
    fun dgsspLw(context: AppDimensContext, value: Int): Float = value.dgsspLw(context)

    fun dgsspLwa(context: AppDimensContext, value: Int): Float = value.dgsspLwa(context)

    fun dgsspLwi(context: AppDimensContext, value: Int): Float = value.dgsspLwi(context)

    fun dgsspLwia(context: AppDimensContext, value: Int): Float = value.dgsspLwia(context)

    /**
     * EN Quick resolution for Screen Height (dghsp).
     * PT Resolução rápida para Altura da Tela (dghsp).
     */
    fun dghsp(context: AppDimensContext, value: Int): Float = value.dghsp(context)

    fun dghspa(context: AppDimensContext, value: Int): Float = value.dghspa(context)

    fun dghspi(context: AppDimensContext, value: Int): Float = value.dghspi(context)

    fun dghspia(context: AppDimensContext, value: Int): Float = value.dghspia(context)

    /**
     * EN Quick resolution for Screen Height (dghsp), but in landscape orientation it acts as Screen Width (dgwsp).
     */
    fun dghspLw(context: AppDimensContext, value: Int): Float = value.dghspLw(context)

    fun dghspLwa(context: AppDimensContext, value: Int): Float = value.dghspLwa(context)

    fun dghspLwi(context: AppDimensContext, value: Int): Float = value.dghspLwi(context)

    fun dghspLwia(context: AppDimensContext, value: Int): Float = value.dghspLwia(context)

    /**
     * EN Quick resolution for Screen Height (dghsp), but in portrait orientation it acts as Screen Width (dgwsp).
     */
    fun dghspPw(context: AppDimensContext, value: Int): Float = value.dghspPw(context)

    fun dghspPwa(context: AppDimensContext, value: Int): Float = value.dghspPwa(context)

    fun dghspPwi(context: AppDimensContext, value: Int): Float = value.dghspPwi(context)

    fun dghspPwia(context: AppDimensContext, value: Int): Float = value.dghspPwia(context)

    /**
     * EN Quick resolution for Screen Width (dgwsp).
     * PT Resolução rápida para Largura da Tela (dgwsp).
     */
    fun dgwsp(context: AppDimensContext, value: Int): Float = value.dgwsp(context)

    fun dgwspa(context: AppDimensContext, value: Int): Float = value.dgwspa(context)

    fun dgwspi(context: AppDimensContext, value: Int): Float = value.dgwspi(context)

    fun dgwspia(context: AppDimensContext, value: Int): Float = value.dgwspia(context)

    /**
     * EN Quick resolution for Screen Width (dgwsp), but in landscape orientation it acts as Screen Height (dghsp).
     */
    fun dgwspLh(context: AppDimensContext, value: Int): Float = value.dgwspLh(context)

    fun dgwspLha(context: AppDimensContext, value: Int): Float = value.dgwspLha(context)

    fun dgwspLhi(context: AppDimensContext, value: Int): Float = value.dgwspLhi(context)

    fun dgwspLhia(context: AppDimensContext, value: Int): Float = value.dgwspLhia(context)

    /**
     * EN Quick resolution for Screen Width (dgwsp), but in portrait orientation it acts as Screen Height (dghsp).
     */
    fun dgwspPh(context: AppDimensContext, value: Int): Float = value.dgwspPh(context)

    fun dgwspPha(context: AppDimensContext, value: Int): Float = value.dgwspPha(context)

    fun dgwspPhi(context: AppDimensContext, value: Int): Float = value.dgwspPhi(context)

    fun dgwspPhia(context: AppDimensContext, value: Int): Float = value.dgwspPhia(context)


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
    fun dgwemPh(context: AppDimensContext, value: Int): Float = value.dgwemPh(context)

    fun dgwemPha(context: AppDimensContext, value: Int): Float = value.dgwemPha(context)

    fun dgwemPhi(context: AppDimensContext, value: Int): Float = value.dgwemPhi(context)

    fun dgwemPhia(context: AppDimensContext, value: Int): Float = value.dgwemPhia(context)

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
    ): Float = value.toDynamicDiagonalSpPx(context, qualifier, fontScale, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

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
    ): Float = value.toDynamicDiagonalSp(context, qualifier, fontScale, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Starts the build chain for the custom dimension DiagonalSp from a base Int.
     * PT Inicia a cadeia de construção para a dimensão customizada DiagonalSp a partir de um Int base.
     */
    fun scaled(initialBaseValue: Int): DiagonalSp = DiagonalSp(initialBaseValue)

    /**
     * EN Starts the build chain for the custom dimension DiagonalSp from a base Float.
     * PT Inicia a cadeia de construção para a dimensão customizada DiagonalSp a partir de um Float base.
     */
    fun scaled(initialBaseValue: Float): DiagonalSp = DiagonalSp(initialBaseValue)

    // EN Qualifier-based conditional dynamic scaling for Sp.
    // PT Escalonamento condicional baseado em qualificador para Sp.

    /**
     * EN Quick resolution for Smallest Width (swSP) conditional scaling.
     */
    fun dgsspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.dgsspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hSP) conditional scaling.
     */
    fun dghspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.dghspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wSP) conditional scaling.
     */
    fun dgwspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.dgwspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType + DpQualifier combined facilitator extensions for Sp.
    // PT Extensões facilitadoras combinadas UiModeType + DpQualifier para Sp.

    /**
     * EN Quick resolution for Smallest Width (swSP) context conditional scaling.
     */
    fun dgsspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.dgsspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hSP) context conditional scaling.
     */
    fun dghspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.dghspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wSP) context conditional scaling.
     */
    fun dgwspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.dgwspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN Rotation facilitator functions for Java.
    // PT Funções facilitadoras de rotação para Java.

    /**
     * EN Facilitator for Smallest Width (dgssp) with rotation override.
     */
    fun dgsspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.SMALL_WIDTH, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.dgsspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (dghsp) with rotation override.
     */
    fun dghspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.HEIGHT, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.dghspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (dgwsp) with rotation override.
     */
    fun dgwspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.WIDTH, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.dgwspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType facilitator functions for Java.
    // PT Funções facilitadoras de UiModeType para Java.

    /**
     * EN Facilitator for Smallest Width (dgssp) with UiModeType override.
     */
    fun dgsspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.dgsspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (dghsp) with UiModeType override.
     */
    fun dghspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.dghspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (dgwsp) with UiModeType override.
     */
    fun dgwspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.dgwspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
}