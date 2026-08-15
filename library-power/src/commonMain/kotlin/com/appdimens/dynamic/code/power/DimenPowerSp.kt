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
package com.appdimens.dynamic.code.power

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
object DimenPowerSp {

    /**
     * EN Eagerly initializes [DimenCache] so the first resolution on a hot path avoids lazy-init work.
     * PT Inicializa o [DimenCache] antecipadamente para evitar custo lazy no primeiro uso.
     */
    fun warmupCache(context: AppDimensContext) {
        DimenCache.init(context)
    }

    /**
     * EN Quick resolution for Smallest Width (pwssp).
     * PT Resolução rápida para Smallest Width (pwssp).
     */
    fun pwssp(context: AppDimensContext, value: Int): Float = value.pwssp(context)

    fun pwsspa(context: AppDimensContext, value: Int): Float = value.pwsspa(context)

    fun pwsspi(context: AppDimensContext, value: Int): Float = value.pwsspi(context)

    fun pwsspia(context: AppDimensContext, value: Int): Float = value.pwsspia(context)

    /**
     * EN Quick resolution for Smallest Width (pwssp), but in portrait orientation it acts as Screen Height (pwhsp).
     */
    fun pwsspPh(context: AppDimensContext, value: Int): Float = value.pwsspPh(context)

    fun pwsspPha(context: AppDimensContext, value: Int): Float = value.pwsspPha(context)

    fun pwsspPhi(context: AppDimensContext, value: Int): Float = value.pwsspPhi(context)

    fun pwsspPhia(context: AppDimensContext, value: Int): Float = value.pwsspPhia(context)

    /**
     * EN Quick resolution for Smallest Width (pwssp), but in landscape orientation it acts as Screen Height (pwhsp).
     */
    fun pwsspLh(context: AppDimensContext, value: Int): Float = value.pwsspLh(context)

    fun pwsspLha(context: AppDimensContext, value: Int): Float = value.pwsspLha(context)

    fun pwsspLhi(context: AppDimensContext, value: Int): Float = value.pwsspLhi(context)

    fun pwsspLhia(context: AppDimensContext, value: Int): Float = value.pwsspLhia(context)

    /**
     * EN Quick resolution for Smallest Width (pwssp), but in portrait orientation it acts as Screen Width (pwwsp).
     */
    fun pwsspPw(context: AppDimensContext, value: Int): Float = value.pwsspPw(context)

    fun pwsspPwa(context: AppDimensContext, value: Int): Float = value.pwsspPwa(context)

    fun pwsspPwi(context: AppDimensContext, value: Int): Float = value.pwsspPwi(context)

    fun pwsspPwia(context: AppDimensContext, value: Int): Float = value.pwsspPwia(context)

    /**
     * EN Quick resolution for Smallest Width (pwssp), but in landscape orientation it acts as Screen Width (pwwsp).
     */
    fun pwsspLw(context: AppDimensContext, value: Int): Float = value.pwsspLw(context)

    fun pwsspLwa(context: AppDimensContext, value: Int): Float = value.pwsspLwa(context)

    fun pwsspLwi(context: AppDimensContext, value: Int): Float = value.pwsspLwi(context)

    fun pwsspLwia(context: AppDimensContext, value: Int): Float = value.pwsspLwia(context)

    /**
     * EN Quick resolution for Screen Height (pwhsp).
     * PT Resolução rápida para Altura da Tela (pwhsp).
     */
    fun pwhsp(context: AppDimensContext, value: Int): Float = value.pwhsp(context)

    fun pwhspa(context: AppDimensContext, value: Int): Float = value.pwhspa(context)

    fun pwhspi(context: AppDimensContext, value: Int): Float = value.pwhspi(context)

    fun pwhspia(context: AppDimensContext, value: Int): Float = value.pwhspia(context)

    /**
     * EN Quick resolution for Screen Height (pwhsp), but in landscape orientation it acts as Screen Width (pwwsp).
     */
    fun pwhspLw(context: AppDimensContext, value: Int): Float = value.pwhspLw(context)

    fun pwhspLwa(context: AppDimensContext, value: Int): Float = value.pwhspLwa(context)

    fun pwhspLwi(context: AppDimensContext, value: Int): Float = value.pwhspLwi(context)

    fun pwhspLwia(context: AppDimensContext, value: Int): Float = value.pwhspLwia(context)

    /**
     * EN Quick resolution for Screen Height (pwhsp), but in portrait orientation it acts as Screen Width (pwwsp).
     */
    fun pwhspPw(context: AppDimensContext, value: Int): Float = value.pwhspPw(context)

    fun pwhspPwa(context: AppDimensContext, value: Int): Float = value.pwhspPwa(context)

    fun pwhspPwi(context: AppDimensContext, value: Int): Float = value.pwhspPwi(context)

    fun pwhspPwia(context: AppDimensContext, value: Int): Float = value.pwhspPwia(context)

    /**
     * EN Quick resolution for Screen Width (pwwsp).
     * PT Resolução rápida para Largura da Tela (pwwsp).
     */
    fun pwwsp(context: AppDimensContext, value: Int): Float = value.pwwsp(context)

    fun pwwspa(context: AppDimensContext, value: Int): Float = value.pwwspa(context)

    fun pwwspi(context: AppDimensContext, value: Int): Float = value.pwwspi(context)

    fun pwwspia(context: AppDimensContext, value: Int): Float = value.pwwspia(context)

    /**
     * EN Quick resolution for Screen Width (pwwsp), but in landscape orientation it acts as Screen Height (pwhsp).
     */
    fun pwwspLh(context: AppDimensContext, value: Int): Float = value.pwwspLh(context)

    fun pwwspLha(context: AppDimensContext, value: Int): Float = value.pwwspLha(context)

    fun pwwspLhi(context: AppDimensContext, value: Int): Float = value.pwwspLhi(context)

    fun pwwspLhia(context: AppDimensContext, value: Int): Float = value.pwwspLhia(context)

    /**
     * EN Quick resolution for Screen Width (pwwsp), but in portrait orientation it acts as Screen Height (pwhsp).
     */
    fun pwwspPh(context: AppDimensContext, value: Int): Float = value.pwwspPh(context)

    fun pwwspPha(context: AppDimensContext, value: Int): Float = value.pwwspPha(context)

    fun pwwspPhi(context: AppDimensContext, value: Int): Float = value.pwwspPhi(context)

    fun pwwspPhia(context: AppDimensContext, value: Int): Float = value.pwwspPhia(context)


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
    fun pwwemPh(context: AppDimensContext, value: Int): Float = value.pwwemPh(context)

    fun pwwemPha(context: AppDimensContext, value: Int): Float = value.pwwemPha(context)

    fun pwwemPhi(context: AppDimensContext, value: Int): Float = value.pwwemPhi(context)

    fun pwwemPhia(context: AppDimensContext, value: Int): Float = value.pwwemPhia(context)

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
    ): Float = value.toDynamicPowerSpPx(context, qualifier, fontScale, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

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
    ): Float = value.toDynamicPowerSp(context, qualifier, fontScale, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Starts the build chain for the custom dimension PowerSp from a base Int.
     * PT Inicia a cadeia de construção para a dimensão customizada PowerSp a partir de um Int base.
     */
    fun scaled(initialBaseValue: Int): PowerSp = PowerSp(initialBaseValue)

    /**
     * EN Starts the build chain for the custom dimension PowerSp from a base Float.
     * PT Inicia a cadeia de construção para a dimensão customizada PowerSp a partir de um Float base.
     */
    fun scaled(initialBaseValue: Float): PowerSp = PowerSp(initialBaseValue)

    // EN Qualifier-based conditional dynamic scaling for Sp.
    // PT Escalonamento condicional baseado em qualificador para Sp.

    /**
     * EN Quick resolution for Smallest Width (swSP) conditional scaling.
     */
    fun pwsspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwsspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hSP) conditional scaling.
     */
    fun pwhspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwhspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wSP) conditional scaling.
     */
    fun pwwspQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwwspQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType + DpQualifier combined facilitator extensions for Sp.
    // PT Extensões facilitadoras combinadas UiModeType + DpQualifier para Sp.

    /**
     * EN Quick resolution for Smallest Width (swSP) context conditional scaling.
     */
    fun pwsspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwsspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hSP) context conditional scaling.
     */
    fun pwhspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwhspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wSP) context conditional scaling.
     */
    fun pwwspScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwwspScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN Rotation facilitator functions for Java.
    // PT Funções facilitadoras de rotação para Java.

    /**
     * EN Facilitator for Smallest Width (pwssp) with rotation override.
     */
    fun pwsspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.SMALL_WIDTH, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwsspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (pwhsp) with rotation override.
     */
    fun pwhspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.HEIGHT, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwhspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (pwwsp) with rotation override.
     */
    fun pwwspRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.WIDTH, orientation: Orientation = Orientation.LANDSCAPE, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwwspRotate(context, rotationValue, finalQualifierResolver, orientation, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType facilitator functions for Java.
    // PT Funções facilitadoras de UiModeType para Java.

    /**
     * EN Facilitator for Smallest Width (pwssp) with UiModeType override.
     */
    fun pwsspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwsspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (pwhsp) with UiModeType override.
     */
    fun pwhspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwhspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (pwwsp) with UiModeType override.
     */
    fun pwwspMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwwspMode(context, modeValue, uiModeType, finalQualifierResolver, fontScale, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
}