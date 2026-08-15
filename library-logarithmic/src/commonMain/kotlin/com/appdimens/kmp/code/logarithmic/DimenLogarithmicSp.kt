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
package com.appdimens.kmp.code.logarithmic

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
object DimenLogarithmicSp {

    /**
     * EN Eagerly initializes [DimenCache] so the first resolution on a hot path avoids lazy-init work.
     * PT Inicializa o [DimenCache] antecipadamente para evitar custo lazy no primeiro uso.
     */
    fun warmupCache(context: AppDimensContext) {
        DimenCache.init(context)
    }

    /**
     * EN Quick resolution for Smallest Width (logssp).
     * PT Resolução rápida para Smallest Width (logssp).
     */
    fun logssp(context: AppDimensContext, value: Int): Float = value.logssp(context)

    fun logsspa(context: AppDimensContext, value: Int): Float = value.logsspa(context)

    fun logsspi(context: AppDimensContext, value: Int): Float = value.logsspi(context)

    fun logsspia(context: AppDimensContext, value: Int): Float = value.logsspia(context)

    /**
     * EN Quick resolution for Smallest Width (logssp), but in portrait orientation it acts as Screen Height (loghsp).
     */
    fun logsspPh(context: AppDimensContext, value: Int): Float = value.logsspPh(context)

    fun logsspPha(context: AppDimensContext, value: Int): Float = value.logsspPha(context)

    fun logsspPhi(context: AppDimensContext, value: Int): Float = value.logsspPhi(context)

    fun logsspPhia(context: AppDimensContext, value: Int): Float = value.logsspPhia(context)

    /**
     * EN Quick resolution for Smallest Width (logssp), but in landscape orientation it acts as Screen Height (loghsp).
     */
    fun logsspLh(context: AppDimensContext, value: Int): Float = value.logsspLh(context)

    fun logsspLha(context: AppDimensContext, value: Int): Float = value.logsspLha(context)

    fun logsspLhi(context: AppDimensContext, value: Int): Float = value.logsspLhi(context)

    fun logsspLhia(context: AppDimensContext, value: Int): Float = value.logsspLhia(context)

    /**
     * EN Quick resolution for Smallest Width (logssp), but in portrait orientation it acts as Screen Width (logwsp).
     */
    fun logsspPw(context: AppDimensContext, value: Int): Float = value.logsspPw(context)

    fun logsspPwa(context: AppDimensContext, value: Int): Float = value.logsspPwa(context)

    fun logsspPwi(context: AppDimensContext, value: Int): Float = value.logsspPwi(context)

    fun logsspPwia(context: AppDimensContext, value: Int): Float = value.logsspPwia(context)

    /**
     * EN Quick resolution for Smallest Width (logssp), but in landscape orientation it acts as Screen Width (logwsp).
     */
    fun logsspLw(context: AppDimensContext, value: Int): Float = value.logsspLw(context)

    fun logsspLwa(context: AppDimensContext, value: Int): Float = value.logsspLwa(context)

    fun logsspLwi(context: AppDimensContext, value: Int): Float = value.logsspLwi(context)

    fun logsspLwia(context: AppDimensContext, value: Int): Float = value.logsspLwia(context)

    /**
     * EN Quick resolution for Screen Height (loghsp).
     * PT Resolução rápida para Altura da Tela (loghsp).
     */
    fun loghsp(context: AppDimensContext, value: Int): Float = value.loghsp(context)

    fun loghspa(context: AppDimensContext, value: Int): Float = value.loghspa(context)

    fun loghspi(context: AppDimensContext, value: Int): Float = value.loghspi(context)

    fun loghspia(context: AppDimensContext, value: Int): Float = value.loghspia(context)

    /**
     * EN Quick resolution for Screen Height (loghsp), but in landscape orientation it acts as Screen Width (logwsp).
     */
    fun loghspLw(context: AppDimensContext, value: Int): Float = value.loghspLw(context)

    fun loghspLwa(context: AppDimensContext, value: Int): Float = value.loghspLwa(context)

    fun loghspLwi(context: AppDimensContext, value: Int): Float = value.loghspLwi(context)

    fun loghspLwia(context: AppDimensContext, value: Int): Float = value.loghspLwia(context)

    /**
     * EN Quick resolution for Screen Height (loghsp), but in portrait orientation it acts as Screen Width (logwsp).
     */
    fun loghspPw(context: AppDimensContext, value: Int): Float = value.loghspPw(context)

    fun loghspPwa(context: AppDimensContext, value: Int): Float = value.loghspPwa(context)

    fun loghspPwi(context: AppDimensContext, value: Int): Float = value.loghspPwi(context)

    fun loghspPwia(context: AppDimensContext, value: Int): Float = value.loghspPwia(context)

    /**
     * EN Quick resolution for Screen Width (logwsp).
     * PT Resolução rápida para Largura da Tela (logwsp).
     */
    fun logwsp(context: AppDimensContext, value: Int): Float = value.logwsp(context)

    fun logwspa(context: AppDimensContext, value: Int): Float = value.logwspa(context)

    fun logwspi(context: AppDimensContext, value: Int): Float = value.logwspi(context)

    fun logwspia(context: AppDimensContext, value: Int): Float = value.logwspia(context)

    /**
     * EN Quick resolution for Screen Width (logwsp), but in landscape orientation it acts as Screen Height (loghsp).
     */
    fun logwspLh(context: AppDimensContext, value: Int): Float = value.logwspLh(context)

    fun logwspLha(context: AppDimensContext, value: Int): Float = value.logwspLha(context)

    fun logwspLhi(context: AppDimensContext, value: Int): Float = value.logwspLhi(context)

    fun logwspLhia(context: AppDimensContext, value: Int): Float = value.logwspLhia(context)

    /**
     * EN Quick resolution for Screen Width (logwsp), but in portrait orientation it acts as Screen Height (loghsp).
     */
    fun logwspPh(context: AppDimensContext, value: Int): Float = value.logwspPh(context)

    fun logwspPha(context: AppDimensContext, value: Int): Float = value.logwspPha(context)

    fun logwspPhi(context: AppDimensContext, value: Int): Float = value.logwspPhi(context)

    fun logwspPhia(context: AppDimensContext, value: Int): Float = value.logwspPhia(context)


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
    fun logwemPh(context: AppDimensContext, value: Int): Float = value.logwemPh(context)

    fun logwemPha(context: AppDimensContext, value: Int): Float = value.logwemPha(context)

    fun logwemPhi(context: AppDimensContext, value: Int): Float = value.logwemPhi(context)

    fun logwemPhia(context: AppDimensContext, value: Int): Float = value.logwemPhia(context)

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
    ): Float = value.toDynamicLogarithmicSpPx(context, qualifier, fontScale, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

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
    ): Float = value.toDynamicLogarithmicSp(context, qualifier, fontScale, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Starts the build chain for the custom dimension LogarithmicSp from a base Int.
     * PT Inicia a cadeia de construção para a dimensão customizada LogarithmicSp a partir de um Int base.
     */
    fun scaled(initialBaseValue: Int): LogarithmicSp = LogarithmicSp(initialBaseValue)

    /**
     * EN Starts the build chain for the custom dimension LogarithmicSp from a base Float.
     * PT Inicia a cadeia de construção para a dimensão customizada LogarithmicSp a partir de um Float base.
     */
    fun scaled(initialBaseValue: Float): LogarithmicSp = LogarithmicSp(initialBaseValue)

    // EN Qualifier-based conditional dynamic scaling for Sp.
    // PT Escalonamento condicional baseado em qualificador para Sp.

    /**
     * EN Quick resolution for Smallest Width (swSP) conditional scaling.
     */
    fun logsspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.logsspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hSP) conditional scaling.
     */
    fun loghspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.loghspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wSP) conditional scaling.
     */
    fun logwspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.logwspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType + DpQualifier combined facilitator extensions for Sp.
    // PT Extensões facilitadoras combinadas UiModeType + DpQualifier para Sp.

    /**
     * EN Quick resolution for Smallest Width (swSP) context conditional scaling.
     */
    fun logsspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.logsspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hSP) context conditional scaling.
     */
    fun loghspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.loghspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wSP) context conditional scaling.
     */
    fun logwspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.logwspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN Rotation facilitator functions for Java.
    // PT Funções facilitadoras de rotação para Java.

    /**
     * EN Facilitator for Smallest Width (logssp) with rotation override.
     */
    fun logsspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.SMALL_WIDTH, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.logsspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (loghsp) with rotation override.
     */
    fun loghspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.HEIGHT, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.loghspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (logwsp) with rotation override.
     */
    fun logwspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.WIDTH, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.logwspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType facilitator functions for Java.
    // PT Funções facilitadoras de UiModeType para Java.

    /**
     * EN Facilitator for Smallest Width (logssp) with UiModeType override.
     */
    fun logsspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.logsspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (loghsp) with UiModeType override.
     */
    fun loghspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.loghspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (logwsp) with UiModeType override.
     */
    fun logwspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.logwspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
}