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
package com.appdimens.kmp.code.fit

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
object DimenFitSp {

    /**
     * EN Eagerly initializes [DimenCache] so the first resolution on a hot path avoids lazy-init work.
     * PT Inicializa o [DimenCache] antecipadamente para evitar custo lazy no primeiro uso.
     */
    fun warmupCache(context: AppDimensContext) {
        DimenCache.init(context)
    }

    /**
     * EN Quick resolution for Smallest Width (ftssp).
     * PT Resolução rápida para Smallest Width (ftssp).
     */
    fun ftssp(context: AppDimensContext, value: Int): Float = value.ftssp(context)

    fun ftsspa(context: AppDimensContext, value: Int): Float = value.ftsspa(context)

    fun ftsspi(context: AppDimensContext, value: Int): Float = value.ftsspi(context)

    fun ftsspia(context: AppDimensContext, value: Int): Float = value.ftsspia(context)

    /**
     * EN Quick resolution for Smallest Width (ftssp), but in portrait orientation it acts as Screen Height (fthsp).
     */
    fun ftsspPh(context: AppDimensContext, value: Int): Float = value.ftsspPh(context)

    fun ftsspPha(context: AppDimensContext, value: Int): Float = value.ftsspPha(context)

    fun ftsspPhi(context: AppDimensContext, value: Int): Float = value.ftsspPhi(context)

    fun ftsspPhia(context: AppDimensContext, value: Int): Float = value.ftsspPhia(context)

    /**
     * EN Quick resolution for Smallest Width (ftssp), but in landscape orientation it acts as Screen Height (fthsp).
     */
    fun ftsspLh(context: AppDimensContext, value: Int): Float = value.ftsspLh(context)

    fun ftsspLha(context: AppDimensContext, value: Int): Float = value.ftsspLha(context)

    fun ftsspLhi(context: AppDimensContext, value: Int): Float = value.ftsspLhi(context)

    fun ftsspLhia(context: AppDimensContext, value: Int): Float = value.ftsspLhia(context)

    /**
     * EN Quick resolution for Smallest Width (ftssp), but in portrait orientation it acts as Screen Width (ftwsp).
     */
    fun ftsspPw(context: AppDimensContext, value: Int): Float = value.ftsspPw(context)

    fun ftsspPwa(context: AppDimensContext, value: Int): Float = value.ftsspPwa(context)

    fun ftsspPwi(context: AppDimensContext, value: Int): Float = value.ftsspPwi(context)

    fun ftsspPwia(context: AppDimensContext, value: Int): Float = value.ftsspPwia(context)

    /**
     * EN Quick resolution for Smallest Width (ftssp), but in landscape orientation it acts as Screen Width (ftwsp).
     */
    fun ftsspLw(context: AppDimensContext, value: Int): Float = value.ftsspLw(context)

    fun ftsspLwa(context: AppDimensContext, value: Int): Float = value.ftsspLwa(context)

    fun ftsspLwi(context: AppDimensContext, value: Int): Float = value.ftsspLwi(context)

    fun ftsspLwia(context: AppDimensContext, value: Int): Float = value.ftsspLwia(context)

    /**
     * EN Quick resolution for Screen Height (fthsp).
     * PT Resolução rápida para Altura da Tela (fthsp).
     */
    fun fthsp(context: AppDimensContext, value: Int): Float = value.fthsp(context)

    fun fthspa(context: AppDimensContext, value: Int): Float = value.fthspa(context)

    fun fthspi(context: AppDimensContext, value: Int): Float = value.fthspi(context)

    fun fthspia(context: AppDimensContext, value: Int): Float = value.fthspia(context)

    /**
     * EN Quick resolution for Screen Height (fthsp), but in landscape orientation it acts as Screen Width (ftwsp).
     */
    fun fthspLw(context: AppDimensContext, value: Int): Float = value.fthspLw(context)

    fun fthspLwa(context: AppDimensContext, value: Int): Float = value.fthspLwa(context)

    fun fthspLwi(context: AppDimensContext, value: Int): Float = value.fthspLwi(context)

    fun fthspLwia(context: AppDimensContext, value: Int): Float = value.fthspLwia(context)

    /**
     * EN Quick resolution for Screen Height (fthsp), but in portrait orientation it acts as Screen Width (ftwsp).
     */
    fun fthspPw(context: AppDimensContext, value: Int): Float = value.fthspPw(context)

    fun fthspPwa(context: AppDimensContext, value: Int): Float = value.fthspPwa(context)

    fun fthspPwi(context: AppDimensContext, value: Int): Float = value.fthspPwi(context)

    fun fthspPwia(context: AppDimensContext, value: Int): Float = value.fthspPwia(context)

    /**
     * EN Quick resolution for Screen Width (ftwsp).
     * PT Resolução rápida para Largura da Tela (ftwsp).
     */
    fun ftwsp(context: AppDimensContext, value: Int): Float = value.ftwsp(context)

    fun ftwspa(context: AppDimensContext, value: Int): Float = value.ftwspa(context)

    fun ftwspi(context: AppDimensContext, value: Int): Float = value.ftwspi(context)

    fun ftwspia(context: AppDimensContext, value: Int): Float = value.ftwspia(context)

    /**
     * EN Quick resolution for Screen Width (ftwsp), but in landscape orientation it acts as Screen Height (fthsp).
     */
    fun ftwspLh(context: AppDimensContext, value: Int): Float = value.ftwspLh(context)

    fun ftwspLha(context: AppDimensContext, value: Int): Float = value.ftwspLha(context)

    fun ftwspLhi(context: AppDimensContext, value: Int): Float = value.ftwspLhi(context)

    fun ftwspLhia(context: AppDimensContext, value: Int): Float = value.ftwspLhia(context)

    /**
     * EN Quick resolution for Screen Width (ftwsp), but in portrait orientation it acts as Screen Height (fthsp).
     */
    fun ftwspPh(context: AppDimensContext, value: Int): Float = value.ftwspPh(context)

    fun ftwspPha(context: AppDimensContext, value: Int): Float = value.ftwspPha(context)

    fun ftwspPhi(context: AppDimensContext, value: Int): Float = value.ftwspPhi(context)

    fun ftwspPhia(context: AppDimensContext, value: Int): Float = value.ftwspPhia(context)


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
    fun ftwemPh(context: AppDimensContext, value: Int): Float = value.ftwemPh(context)

    fun ftwemPha(context: AppDimensContext, value: Int): Float = value.ftwemPha(context)

    fun ftwemPhi(context: AppDimensContext, value: Int): Float = value.ftwemPhi(context)

    fun ftwemPhia(context: AppDimensContext, value: Int): Float = value.ftwemPhia(context)

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
    ): Float = value.toDynamicFitSpPx(context, qualifier, fontScale, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

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
    ): Float = value.toDynamicFitSp(context, qualifier, fontScale, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Starts the build chain for the custom dimension FitSp from a base Int.
     * PT Inicia a cadeia de construção para a dimensão customizada FitSp a partir de um Int base.
     */
    fun scaled(initialBaseValue: Int): FitSp = FitSp(initialBaseValue)

    /**
     * EN Starts the build chain for the custom dimension FitSp from a base Float.
     * PT Inicia a cadeia de construção para a dimensão customizada FitSp a partir de um Float base.
     */
    fun scaled(initialBaseValue: Float): FitSp = FitSp(initialBaseValue)

    // EN Qualifier-based conditional dynamic scaling for Sp.
    // PT Escalonamento condicional baseado em qualificador para Sp.

    /**
     * EN Quick resolution for Smallest Width (swSP) conditional scaling.
     */
    fun ftsspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.ftsspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hSP) conditional scaling.
     */
    fun fthspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.fthspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wSP) conditional scaling.
     */
    fun ftwspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.ftwspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType + DpQualifier combined facilitator extensions for Sp.
    // PT Extensões facilitadoras combinadas UiModeType + DpQualifier para Sp.

    /**
     * EN Quick resolution for Smallest Width (swSP) context conditional scaling.
     */
    fun ftsspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.ftsspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hSP) context conditional scaling.
     */
    fun fthspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.fthspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wSP) context conditional scaling.
     */
    fun ftwspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.ftwspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN Rotation facilitator functions for Java.
    // PT Funções facilitadoras de rotação para Java.

    /**
     * EN Facilitator for Smallest Width (ftssp) with rotation override.
     */
    fun ftsspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.SMALL_WIDTH, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.ftsspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (fthsp) with rotation override.
     */
    fun fthspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.HEIGHT, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.fthspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (ftwsp) with rotation override.
     */
    fun ftwspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.WIDTH, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.ftwspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType facilitator functions for Java.
    // PT Funções facilitadoras de UiModeType para Java.

    /**
     * EN Facilitator for Smallest Width (ftssp) with UiModeType override.
     */
    fun ftsspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.ftsspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (fthsp) with UiModeType override.
     */
    fun fthspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.fthspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (ftwsp) with UiModeType override.
     */
    fun ftwspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.ftwspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
}