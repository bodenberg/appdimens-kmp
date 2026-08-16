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
package com.appdimens.kmp.code.percent

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
object DimenPercentSp {

    /**
     * EN Eagerly initializes [DimenCache] so the first resolution on a hot path avoids lazy-init work.
     * PT Inicializa o [DimenCache] antecipadamente para evitar custo lazy no primeiro uso.
     */
    fun warmupCache(context: AppDimensContext) {
        DimenCache.init(context)
    }

    /**
     * EN Quick resolution for Smallest Width (pssp).
     * PT Resolução rápida para Smallest Width (pssp).
     */
    fun pssp(context: AppDimensContext, value: Int): Float = value.pssp(context)

    fun psspa(context: AppDimensContext, value: Int): Float = value.psspa(context)

    fun psspi(context: AppDimensContext, value: Int): Float = value.psspi(context)

    fun psspia(context: AppDimensContext, value: Int): Float = value.psspia(context)

    /**
     * EN Quick resolution for Smallest Width (pssp), but in portrait orientation it acts as Screen Height (phsp).
     */
    fun psspPh(context: AppDimensContext, value: Int): Float = value.psspPh(context)

    fun psspPha(context: AppDimensContext, value: Int): Float = value.psspPha(context)

    fun psspPhi(context: AppDimensContext, value: Int): Float = value.psspPhi(context)

    fun psspPhia(context: AppDimensContext, value: Int): Float = value.psspPhia(context)

    /**
     * EN Quick resolution for Smallest Width (pssp), but in landscape orientation it acts as Screen Height (phsp).
     */
    fun psspLh(context: AppDimensContext, value: Int): Float = value.psspLh(context)

    fun psspLha(context: AppDimensContext, value: Int): Float = value.psspLha(context)

    fun psspLhi(context: AppDimensContext, value: Int): Float = value.psspLhi(context)

    fun psspLhia(context: AppDimensContext, value: Int): Float = value.psspLhia(context)

    /**
     * EN Quick resolution for Smallest Width (pssp), but in portrait orientation it acts as Screen Width (pwsp).
     */
    fun psspPw(context: AppDimensContext, value: Int): Float = value.psspPw(context)

    fun psspPwa(context: AppDimensContext, value: Int): Float = value.psspPwa(context)

    fun psspPwi(context: AppDimensContext, value: Int): Float = value.psspPwi(context)

    fun psspPwia(context: AppDimensContext, value: Int): Float = value.psspPwia(context)

    /**
     * EN Quick resolution for Smallest Width (pssp), but in landscape orientation it acts as Screen Width (pwsp).
     */
    fun psspLw(context: AppDimensContext, value: Int): Float = value.psspLw(context)

    fun psspLwa(context: AppDimensContext, value: Int): Float = value.psspLwa(context)

    fun psspLwi(context: AppDimensContext, value: Int): Float = value.psspLwi(context)

    fun psspLwia(context: AppDimensContext, value: Int): Float = value.psspLwia(context)

    /**
     * EN Quick resolution for Screen Height (phsp).
     * PT Resolução rápida para Altura da Tela (phsp).
     */
    fun phsp(context: AppDimensContext, value: Int): Float = value.phsp(context)

    fun phspa(context: AppDimensContext, value: Int): Float = value.phspa(context)

    fun phspi(context: AppDimensContext, value: Int): Float = value.phspi(context)

    fun phspia(context: AppDimensContext, value: Int): Float = value.phspia(context)

    /**
     * EN Quick resolution for Screen Height (phsp), but in landscape orientation it acts as Screen Width (pwsp).
     */
    fun phspLw(context: AppDimensContext, value: Int): Float = value.phspLw(context)

    fun phspLwa(context: AppDimensContext, value: Int): Float = value.phspLwa(context)

    fun phspLwi(context: AppDimensContext, value: Int): Float = value.phspLwi(context)

    fun phspLwia(context: AppDimensContext, value: Int): Float = value.phspLwia(context)

    /**
     * EN Quick resolution for Screen Height (phsp), but in portrait orientation it acts as Screen Width (pwsp).
     */
    fun phspPw(context: AppDimensContext, value: Int): Float = value.phspPw(context)

    fun phspPwa(context: AppDimensContext, value: Int): Float = value.phspPwa(context)

    fun phspPwi(context: AppDimensContext, value: Int): Float = value.phspPwi(context)

    fun phspPwia(context: AppDimensContext, value: Int): Float = value.phspPwia(context)

    /**
     * EN Quick resolution for Screen Width (pwsp).
     * PT Resolução rápida para Largura da Tela (pwsp).
     */
    fun pwsp(context: AppDimensContext, value: Int): Float = value.pwsp(context)

    fun pwspa(context: AppDimensContext, value: Int): Float = value.pwspa(context)

    fun pwspi(context: AppDimensContext, value: Int): Float = value.pwspi(context)

    fun pwspia(context: AppDimensContext, value: Int): Float = value.pwspia(context)

    /**
     * EN Quick resolution for Screen Width (pwsp), but in landscape orientation it acts as Screen Height (phsp).
     */
    fun pwspLh(context: AppDimensContext, value: Int): Float = value.pwspLh(context)

    fun pwspLha(context: AppDimensContext, value: Int): Float = value.pwspLha(context)

    fun pwspLhi(context: AppDimensContext, value: Int): Float = value.pwspLhi(context)

    fun pwspLhia(context: AppDimensContext, value: Int): Float = value.pwspLhia(context)

    /**
     * EN Quick resolution for Screen Width (pwsp), but in portrait orientation it acts as Screen Height (phsp).
     */
    fun pwspPh(context: AppDimensContext, value: Int): Float = value.pwspPh(context)

    fun pwspPha(context: AppDimensContext, value: Int): Float = value.pwspPha(context)

    fun pwspPhi(context: AppDimensContext, value: Int): Float = value.pwspPhi(context)

    fun pwspPhia(context: AppDimensContext, value: Int): Float = value.pwspPhia(context)


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
    fun pwemPh(context: AppDimensContext, value: Int): Float = value.pwemPh(context)

    fun pwemPha(context: AppDimensContext, value: Int): Float = value.pwemPha(context)

    fun pwemPhi(context: AppDimensContext, value: Int): Float = value.pwemPhi(context)

    fun pwemPhia(context: AppDimensContext, value: Int): Float = value.pwemPhia(context)

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
    ): Float = value.toDynamicPercentSpPx(context, qualifier, fontScale, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

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
    ): Float = value.toDynamicPercentSp(context, qualifier, fontScale, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Starts the build chain for the custom dimension PercentSp from a base Int.
     * PT Inicia a cadeia de construção para a dimensão customizada PercentSp a partir de um Int base.
     */
    fun scaled(initialBaseValue: Int): PercentSp = PercentSp(initialBaseValue)

    /**
     * EN Starts the build chain for the custom dimension PercentSp from a base Float.
     * PT Inicia a cadeia de construção para a dimensão customizada PercentSp a partir de um Float base.
     */
    fun scaled(initialBaseValue: Float): PercentSp = PercentSp(initialBaseValue)

    // EN Qualifier-based conditional dynamic scaling for Sp.
    // PT Escalonamento condicional baseado em qualificador para Sp.

    /**
     * EN Quick resolution for Smallest Width (swSP) conditional scaling.
     */
    fun psspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.psspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hSP) conditional scaling.
     */
    fun phspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.phspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wSP) conditional scaling.
     */
    fun pwspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType + DpQualifier combined facilitator extensions for Sp.
    // PT Extensões facilitadoras combinadas UiModeType + DpQualifier para Sp.

    /**
     * EN Quick resolution for Smallest Width (swSP) context conditional scaling.
     */
    fun psspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.psspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hSP) context conditional scaling.
     */
    fun phspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.phspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wSP) context conditional scaling.
     */
    fun pwspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN Rotation facilitator functions for Java.
    // PT Funções facilitadoras de rotação para Java.

    /**
     * EN Facilitator for Smallest Width (pssp) with rotation override.
     */
    fun psspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.SMALL_WIDTH, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.psspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (phsp) with rotation override.
     */
    fun phspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.HEIGHT, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.phspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (pwsp) with rotation override.
     */
    fun pwspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.WIDTH, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType facilitator functions for Java.
    // PT Funções facilitadoras de UiModeType para Java.

    /**
     * EN Facilitator for Smallest Width (pssp) with UiModeType override.
     */
    fun psspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.psspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (phsp) with UiModeType override.
     */
    fun phspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.phspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (pwsp) with UiModeType override.
     */
    fun pwspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
}