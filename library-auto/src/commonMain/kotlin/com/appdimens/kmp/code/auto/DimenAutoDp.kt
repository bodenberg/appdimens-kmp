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
package com.appdimens.kmp.code.auto

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
object DimenAutoDp {

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
    fun asdp(context: AppDimensContext, value: Int): Float = value.asdp(context)

    /**
     * EN Quick resolution for Smallest Width (sdpa) with aspect ratio.
     * PT Resolução rápida para Smallest Width (sdpa) com proporção de tela.
     */
    fun asdpa(context: AppDimensContext, value: Int): Float = value.asdpa(context)

    /**
     * EN Quick resolution for Smallest Width (sdpi) ignoring multi-windows.
     * PT Resolução rápida para Smallest Width (sdpi) ignorando janelas múltiplas.
     */
    fun asdpi(context: AppDimensContext, value: Int): Float = value.asdpi(context)

    /**
     * EN Quick resolution for Smallest Width (sdpia) ignoring multi-windows and with aspect ratio.
     * PT Resolução rápida para Smallest Width (sdpia) ignorando janelas múltiplas e com proporção.
     */
    fun asdpia(context: AppDimensContext, value: Int): Float = value.asdpia(context)

    /**
     * EN Quick resolution for Smallest Width (sdp), but in portrait orientation it acts as Screen Height (hdp).
     * PT Resolução rápida para Smallest Width (sdp), mas na orientação retrato atua como Altura da Tela (hdp).
     */
    fun asdpPh(context: AppDimensContext, value: Int): Float = value.asdpPh(context)

    fun asdpPha(context: AppDimensContext, value: Int): Float = value.asdpPha(context)

    fun asdpPhi(context: AppDimensContext, value: Int): Float = value.asdpPhi(context)

    fun asdpPhia(context: AppDimensContext, value: Int): Float = value.asdpPhia(context)

    /**
     * EN Quick resolution for Smallest Width (sdp), but in landscape orientation it acts as Screen Height (hdp).
     * PT Resolução rápida para Smallest Width (sdp), mas na orientação paisagem atua como Altura da Tela (hdp).
     */
    fun asdpLh(context: AppDimensContext, value: Int): Float = value.asdpLh(context)

    fun asdpLha(context: AppDimensContext, value: Int): Float = value.asdpLha(context)

    fun asdpLhi(context: AppDimensContext, value: Int): Float = value.asdpLhi(context)

    fun asdpLhia(context: AppDimensContext, value: Int): Float = value.asdpLhia(context)

    /**
     * EN Quick resolution for Smallest Width (sdp), but in portrait orientation it acts as Screen Width (wdp).
     * PT Resolução rápida para Smallest Width (sdp), mas na orientação retrato atua como Largura da Tela (wdp).
     */
    fun asdpPw(context: AppDimensContext, value: Int): Float = value.asdpPw(context)

    fun asdpPwa(context: AppDimensContext, value: Int): Float = value.asdpPwa(context)

    fun asdpPwi(context: AppDimensContext, value: Int): Float = value.asdpPwi(context)

    fun asdpPwia(context: AppDimensContext, value: Int): Float = value.asdpPwia(context)

    /**
     * EN Quick resolution for Smallest Width (sdp), but in landscape orientation it acts as Screen Width (wdp).
     * PT Resolução rápida para Smallest Width (sdp), mas na orientação paisagem atua como Largura da Tela (wdp).
     */
    fun asdpLw(context: AppDimensContext, value: Int): Float = value.asdpLw(context)

    fun asdpLwa(context: AppDimensContext, value: Int): Float = value.asdpLwa(context)

    fun asdpLwi(context: AppDimensContext, value: Int): Float = value.asdpLwi(context)

    fun asdpLwia(context: AppDimensContext, value: Int): Float = value.asdpLwia(context)

    /**
     * EN Quick resolution for Screen Height (hdp).
     * PT Resolução rápida para Altura da Tela (hdp).
     */
    fun ahdp(context: AppDimensContext, value: Int): Float = value.ahdp(context)

    fun ahdpa(context: AppDimensContext, value: Int): Float = value.ahdpa(context)

    fun ahdpi(context: AppDimensContext, value: Int): Float = value.ahdpi(context)

    fun ahdpia(context: AppDimensContext, value: Int): Float = value.ahdpia(context)

    /**
     * EN Quick resolution for Screen Height (hdp), but in landscape orientation it acts as Screen Width (wdp).
     */
    fun ahdpLw(context: AppDimensContext, value: Int): Float = value.ahdpLw(context)

    fun ahdpLwa(context: AppDimensContext, value: Int): Float = value.ahdpLwa(context)

    fun ahdpLwi(context: AppDimensContext, value: Int): Float = value.ahdpLwi(context)

    fun ahdpLwia(context: AppDimensContext, value: Int): Float = value.ahdpLwia(context)

    /**
     * EN Quick resolution for Screen Height (hdp), but in portrait orientation it acts as Screen Width (wdp).
     */
    fun ahdpPw(context: AppDimensContext, value: Int): Float = value.ahdpPw(context)

    fun ahdpPwa(context: AppDimensContext, value: Int): Float = value.ahdpPwa(context)

    fun ahdpPwi(context: AppDimensContext, value: Int): Float = value.ahdpPwi(context)

    fun ahdpPwia(context: AppDimensContext, value: Int): Float = value.ahdpPwia(context)

    /**
     * EN Quick resolution for Screen Width (wdp).
     * PT Resolução rápida para Largura da Tela (wdp).
     */
    fun awdp(context: AppDimensContext, value: Int): Float = value.awdp(context)

    fun awdpa(context: AppDimensContext, value: Int): Float = value.awdpa(context)

    fun awdpi(context: AppDimensContext, value: Int): Float = value.awdpi(context)

    fun awdpia(context: AppDimensContext, value: Int): Float = value.awdpia(context)

    /**
     * EN Quick resolution for Screen Width (wdp), but in landscape orientation it acts as Screen Height (hdp).
     */
    fun awdpLh(context: AppDimensContext, value: Int): Float = value.awdpLh(context)

    fun awdpLha(context: AppDimensContext, value: Int): Float = value.awdpLha(context)

    fun awdpLhi(context: AppDimensContext, value: Int): Float = value.awdpLhi(context)

    fun awdpLhia(context: AppDimensContext, value: Int): Float = value.awdpLhia(context)

    /**
     * EN Quick resolution for Screen Width (wdp), but in portrait orientation it acts as Screen Height (hdp).
     */
    fun awdpPh(context: AppDimensContext, value: Int): Float = value.awdpPh(context)

    fun awdpPha(context: AppDimensContext, value: Int): Float = value.awdpPha(context)

    fun awdpPhi(context: AppDimensContext, value: Int): Float = value.awdpPhi(context)

    fun awdpPhia(context: AppDimensContext, value: Int): Float = value.awdpPhia(context)

    // EN Qualifier-based conditional dynamic scaling.
    // PT Escalonamento condicional baseado em qualificador.

    /**
     * EN Quick resolution for Smallest Width (swDP) conditional scaling.
     */
    fun asdpQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.asdpQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hDP) conditional scaling.
     */
    fun ahdpQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.ahdpQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wDP) conditional scaling.
     */
    fun awdpQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.awdpQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType + DpQualifier combined facilitator extensions.
    // PT Extensões facilitadoras combinadas UiModeType + DpQualifier.

    /**
     * EN Quick resolution for Smallest Width (swDP) context conditional scaling.
     */
    fun asdpScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.asdpScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hDP) context conditional scaling.
     */
    fun ahdpScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.ahdpScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wDP) context conditional scaling.
     */
    fun awdpScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.awdpScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

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
    ): Float = value.toDynamicAutoPx(context, qualifier, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

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
    ): Float = value.toDynamicAutoDp(context, qualifier, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Starts the build chain for the custom dimension DimenAuto from a base Int.
     * PT Inicia a cadeia de construção para a dimensão customizada DimenAuto a partir de um Int base.
     */
    fun scaled(initialBaseValue: Int): DimenAuto = DimenAuto(initialBaseValue.toFloat())

    /**
     * EN Starts the build chain for the custom dimension DimenAuto from a base Float.
     */
    fun scaled(initialBaseValue: Float): DimenAuto = DimenAuto(initialBaseValue)

    // EN Rotation facilitator functions for Java.
    // PT Funções facilitadoras de rotação para Java.

    /**
     * EN Facilitator for Smallest Width (sdp) with rotation override.
     */
    fun asdpRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.SMALL_WIDTH, orientation: Orientation = Orientation.LANDSCAPE, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.asdpRotate(context, rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (hdp) with rotation override.
     */
    fun ahdpRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.HEIGHT, orientation: Orientation = Orientation.LANDSCAPE, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.ahdpRotate(context, rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (wdp) with rotation override.
     */
    fun awdpRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.WIDTH, orientation: Orientation = Orientation.LANDSCAPE, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.awdpRotate(context, rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType facilitator functions for Java.
    // PT Funções facilitadoras de UiModeType para Java.

    /**
     * EN Facilitator for Smallest Width (sdp) with UiModeType override.
     */
    fun asdpMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.asdpMode(context, modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (hdp) with UiModeType override.
     */
    fun ahdpMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.ahdpMode(context, modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (wdp) with UiModeType override.
     */
    fun awdpMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.awdpMode(context, modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
}