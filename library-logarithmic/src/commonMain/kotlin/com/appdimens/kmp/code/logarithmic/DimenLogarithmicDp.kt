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

import com.appdimens.kmp.common.DpQualifier
import com.appdimens.kmp.common.Inverter
import com.appdimens.kmp.common.Orientation
import com.appdimens.kmp.common.UiModeType
import com.appdimens.kmp.core.DimenCache
import kotlin.math.max
import kotlin.math.min

/**
 * EN
 * Utility object for handling SDP (Scalable Dp) dimensions from Java.
 *
 * PT
 * Objeto utilitário para manipulação de dimensões SDP (Scalable Dp) no Java.
 */
object DimenLogarithmicDp {

    /**
     * EN Eagerly initializes [DimenCache] so the first resolution on a hot path avoids lazy-init work.
     * PT Inicializa o [DimenCache] antecipadamente para evitar custo lazy no primeiro uso.
     */
    fun warmupCache(context: AppDimensContext) {
        DimenCache.init(context)
    }

    /**
     * EN Quick resolution for Smallest Width (sdp).
     * PT Resolução rápida para Smallest Width (sdp).
     */
    fun logsdp(context: AppDimensContext, value: Int): Float = value.logsdp(context)

    /**
     * EN Quick resolution for Smallest Width (sdpa) with aspect ratio.
     * PT Resolução rápida para Smallest Width (sdpa) com proporção de tela.
     */
    fun logsdpa(context: AppDimensContext, value: Int): Float = value.logsdpa(context)

    /**
     * EN Quick resolution for Smallest Width (sdpi) ignoring multi-windows.
     * PT Resolução rápida para Smallest Width (sdpi) ignorando janelas múltiplas.
     */
    fun logsdpi(context: AppDimensContext, value: Int): Float = value.logsdpi(context)

    /**
     * EN Quick resolution for Smallest Width (sdpia) ignoring multi-windows and with aspect ratio.
     * PT Resolução rápida para Smallest Width (sdpia) ignorando janelas múltiplas e com proporção.
     */
    fun logsdpia(context: AppDimensContext, value: Int): Float = value.logsdpia(context)

    /**
     * EN Quick resolution for Smallest Width (sdp), but in portrait orientation it acts as Screen Height (hdp).
     * PT Resolução rápida para Smallest Width (sdp), mas na orientação retrato atua como Altura da Tela (hdp).
     */
    fun logsdpPh(context: AppDimensContext, value: Int): Float = value.logsdpPh(context)

    fun logsdpPha(context: AppDimensContext, value: Int): Float = value.logsdpPha(context)

    fun logsdpPhi(context: AppDimensContext, value: Int): Float = value.logsdpPhi(context)

    fun logsdpPhia(context: AppDimensContext, value: Int): Float = value.logsdpPhia(context)

    /**
     * EN Quick resolution for Smallest Width (sdp), but in landscape orientation it acts as Screen Height (hdp).
     * PT Resolução rápida para Smallest Width (sdp), mas na orientação paisagem atua como Altura da Tela (hdp).
     */
    fun logsdpLh(context: AppDimensContext, value: Int): Float = value.logsdpLh(context)

    fun logsdpLha(context: AppDimensContext, value: Int): Float = value.logsdpLha(context)

    fun logsdpLhi(context: AppDimensContext, value: Int): Float = value.logsdpLhi(context)

    fun logsdpLhia(context: AppDimensContext, value: Int): Float = value.logsdpLhia(context)

    /**
     * EN Quick resolution for Smallest Width (sdp), but in portrait orientation it acts as Screen Width (wdp).
     * PT Resolução rápida para Smallest Width (sdp), mas na orientação retrato atua como Largura da Tela (wdp).
     */
    fun logsdpPw(context: AppDimensContext, value: Int): Float = value.logsdpPw(context)

    fun logsdpPwa(context: AppDimensContext, value: Int): Float = value.logsdpPwa(context)

    fun logsdpPwi(context: AppDimensContext, value: Int): Float = value.logsdpPwi(context)

    fun logsdpPwia(context: AppDimensContext, value: Int): Float = value.logsdpPwia(context)

    /**
     * EN Quick resolution for Smallest Width (sdp), but in landscape orientation it acts as Screen Width (wdp).
     * PT Resolução rápida para Smallest Width (sdp), mas na orientação paisagem atua como Largura da Tela (wdp).
     */
    fun logsdpLw(context: AppDimensContext, value: Int): Float = value.logsdpLw(context)

    fun logsdpLwa(context: AppDimensContext, value: Int): Float = value.logsdpLwa(context)

    fun logsdpLwi(context: AppDimensContext, value: Int): Float = value.logsdpLwi(context)

    fun logsdpLwia(context: AppDimensContext, value: Int): Float = value.logsdpLwia(context)

    /**
     * EN Quick resolution for Screen Height (hdp).
     * PT Resolução rápida para Altura da Tela (hdp).
     */
    fun loghdp(context: AppDimensContext, value: Int): Float = value.loghdp(context)

    fun loghdpa(context: AppDimensContext, value: Int): Float = value.loghdpa(context)

    fun loghdpi(context: AppDimensContext, value: Int): Float = value.loghdpi(context)

    fun loghdpia(context: AppDimensContext, value: Int): Float = value.loghdpia(context)

    /**
     * EN Quick resolution for Screen Height (hdp), but in landscape orientation it acts as Screen Width (wdp).
     */
    fun loghdpLw(context: AppDimensContext, value: Int): Float = value.loghdpLw(context)

    fun loghdpLwa(context: AppDimensContext, value: Int): Float = value.loghdpLwa(context)

    fun loghdpLwi(context: AppDimensContext, value: Int): Float = value.loghdpLwi(context)

    fun loghdpLwia(context: AppDimensContext, value: Int): Float = value.loghdpLwia(context)

    /**
     * EN Quick resolution for Screen Height (hdp), but in portrait orientation it acts as Screen Width (wdp).
     */
    fun loghdpPw(context: AppDimensContext, value: Int): Float = value.loghdpPw(context)

    fun loghdpPwa(context: AppDimensContext, value: Int): Float = value.loghdpPwa(context)

    fun loghdpPwi(context: AppDimensContext, value: Int): Float = value.loghdpPwi(context)

    fun loghdpPwia(context: AppDimensContext, value: Int): Float = value.loghdpPwia(context)

    /**
     * EN Quick resolution for Screen Width (wdp).
     * PT Resolução rápida para Largura da Tela (wdp).
     */
    fun logwdp(context: AppDimensContext, value: Int): Float = value.logwdp(context)

    fun logwdpa(context: AppDimensContext, value: Int): Float = value.logwdpa(context)

    fun logwdpi(context: AppDimensContext, value: Int): Float = value.logwdpi(context)

    fun logwdpia(context: AppDimensContext, value: Int): Float = value.logwdpia(context)

    /**
     * EN Quick resolution for Screen Width (wdp), but in landscape orientation it acts as Screen Height (hdp).
     */
    fun logwdpLh(context: AppDimensContext, value: Int): Float = value.logwdpLh(context)

    fun logwdpLha(context: AppDimensContext, value: Int): Float = value.logwdpLha(context)

    fun logwdpLhi(context: AppDimensContext, value: Int): Float = value.logwdpLhi(context)

    fun logwdpLhia(context: AppDimensContext, value: Int): Float = value.logwdpLhia(context)

    /**
     * EN Quick resolution for Screen Width (wdp), but in portrait orientation it acts as Screen Height (hdp).
     */
    fun logwdpPh(context: AppDimensContext, value: Int): Float = value.logwdpPh(context)

    fun logwdpPha(context: AppDimensContext, value: Int): Float = value.logwdpPha(context)

    fun logwdpPhi(context: AppDimensContext, value: Int): Float = value.logwdpPhi(context)

    fun logwdpPhia(context: AppDimensContext, value: Int): Float = value.logwdpPhia(context)

    // EN Qualifier-based conditional dynamic scaling.
    // PT Escalonamento condicional baseado em qualificador.

    /**
     * EN Quick resolution for Smallest Width (swDP) conditional scaling.
     */
    fun logsdpQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.logsdpQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hDP) conditional scaling.
     */
    fun loghdpQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.loghdpQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wDP) conditional scaling.
     */
    fun logwdpQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.logwdpQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType + DpQualifier combined facilitator extensions.
    // PT Extensões facilitadoras combinadas UiModeType + DpQualifier.

    /**
     * EN Quick resolution for Smallest Width (swDP) context conditional scaling.
     */
    fun logsdpScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.logsdpScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hDP) context conditional scaling.
     */
    fun loghdpScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.loghdpScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wDP) context conditional scaling.
     */
    fun logwdpScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.logwdpScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Generic scaling function for Java.
     * PT Função de escala genérica para Java.
     */
    fun getDimensionInPx(
        context: AppDimensContext,
        qualifier: DpQualifier,
        value: Int,
        inverter: Inverter = Inverter.DEFAULT,
        ignoreMultiWindows: Boolean = false,
        applyAspectRatio: Boolean = false,
        customSensitivityK: Float? = null
    ): Float = value.toDynamicLogarithmicPx(context, qualifier, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Generic DP scaling function for Java.
     * PT Função de escala DP genérica para Java.
     */
    fun getDimensionInDp(
        context: AppDimensContext,
        qualifier: DpQualifier,
        value: Int,
        inverter: Inverter = Inverter.DEFAULT,
        ignoreMultiWindows: Boolean = false,
        applyAspectRatio: Boolean = false,
        customSensitivityK: Float? = null
    ): Float = value.toDynamicLogarithmicDp(context, qualifier, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Starts the build chain for the custom dimension DimenLogarithmic from a base Int.
     * PT Inicia a cadeia de construção para a dimensão customizada DimenLogarithmic a partir de um Int base.
     */
    fun scaled(initialBaseValue: Int): DimenLogarithmic = DimenLogarithmic(initialBaseValue.toFloat())

    /**
     * EN Starts the build chain for the custom dimension DimenLogarithmic from a base Float.
     */
    fun scaled(initialBaseValue: Float): DimenLogarithmic = DimenLogarithmic(initialBaseValue)

    // EN Rotation facilitator functions for Java.
    // PT Funções facilitadoras de rotação para Java.

    /**
     * EN Facilitator for Smallest Width (sdp) with rotation override.
     */
    fun logsdpRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.SMALL_WIDTH, orientation: Orientation = Orientation.LANDSCAPE, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.logsdpRotate(context, rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (hdp) with rotation override.
     */
    fun loghdpRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.HEIGHT, orientation: Orientation = Orientation.LANDSCAPE, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.loghdpRotate(context, rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (wdp) with rotation override.
     */
    fun logwdpRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.WIDTH, orientation: Orientation = Orientation.LANDSCAPE, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.logwdpRotate(context, rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType facilitator functions for Java.
    // PT Funções facilitadoras de UiModeType para Java.

    /**
     * EN Facilitator for Smallest Width (sdp) with UiModeType override.
     */
    fun logsdpMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.logsdpMode(context, modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (hdp) with UiModeType override.
     */
    fun loghdpMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.loghdpMode(context, modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (wdp) with UiModeType override.
     */
    fun logwdpMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.logwdpMode(context, modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
}