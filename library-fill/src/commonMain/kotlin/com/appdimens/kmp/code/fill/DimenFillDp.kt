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
 * Utility object for handling SDP (Scalable Dp) dimensions from Java.
 *
 * PT
 * Objeto utilitário para manipulação de dimensões SDP (Scalable Dp) no Java.
 */
object DimenFillDp {

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
    fun flsdp(context: AppDimensContext, value: Int): Float = value.flsdp(context)

    /**
     * EN Quick resolution for Smallest Width (sdpa) with aspect ratio.
     * PT Resolução rápida para Smallest Width (sdpa) com proporção de tela.
     */
    fun flsdpa(context: AppDimensContext, value: Int): Float = value.flsdpa(context)

    /**
     * EN Quick resolution for Smallest Width (sdpi) ignoring multi-windows.
     * PT Resolução rápida para Smallest Width (sdpi) ignorando janelas múltiplas.
     */
    fun flsdpi(context: AppDimensContext, value: Int): Float = value.flsdpi(context)

    /**
     * EN Quick resolution for Smallest Width (sdpia) ignoring multi-windows and with aspect ratio.
     * PT Resolução rápida para Smallest Width (sdpia) ignorando janelas múltiplas e com proporção.
     */
    fun flsdpia(context: AppDimensContext, value: Int): Float = value.flsdpia(context)

    /**
     * EN Quick resolution for Smallest Width (sdp), but in portrait orientation it acts as Screen Height (hdp).
     * PT Resolução rápida para Smallest Width (sdp), mas na orientação retrato atua como Altura da Tela (hdp).
     */
    fun flsdpPh(context: AppDimensContext, value: Int): Float = value.flsdpPh(context)

    fun flsdpPha(context: AppDimensContext, value: Int): Float = value.flsdpPha(context)

    fun flsdpPhi(context: AppDimensContext, value: Int): Float = value.flsdpPhi(context)

    fun flsdpPhia(context: AppDimensContext, value: Int): Float = value.flsdpPhia(context)

    /**
     * EN Quick resolution for Smallest Width (sdp), but in landscape orientation it acts as Screen Height (hdp).
     * PT Resolução rápida para Smallest Width (sdp), mas na orientação paisagem atua como Altura da Tela (hdp).
     */
    fun flsdpLh(context: AppDimensContext, value: Int): Float = value.flsdpLh(context)

    fun flsdpLha(context: AppDimensContext, value: Int): Float = value.flsdpLha(context)

    fun flsdpLhi(context: AppDimensContext, value: Int): Float = value.flsdpLhi(context)

    fun flsdpLhia(context: AppDimensContext, value: Int): Float = value.flsdpLhia(context)

    /**
     * EN Quick resolution for Smallest Width (sdp), but in portrait orientation it acts as Screen Width (wdp).
     * PT Resolução rápida para Smallest Width (sdp), mas na orientação retrato atua como Largura da Tela (wdp).
     */
    fun flsdpPw(context: AppDimensContext, value: Int): Float = value.flsdpPw(context)

    fun flsdpPwa(context: AppDimensContext, value: Int): Float = value.flsdpPwa(context)

    fun flsdpPwi(context: AppDimensContext, value: Int): Float = value.flsdpPwi(context)

    fun flsdpPwia(context: AppDimensContext, value: Int): Float = value.flsdpPwia(context)

    /**
     * EN Quick resolution for Smallest Width (sdp), but in landscape orientation it acts as Screen Width (wdp).
     * PT Resolução rápida para Smallest Width (sdp), mas na orientação paisagem atua como Largura da Tela (wdp).
     */
    fun flsdpLw(context: AppDimensContext, value: Int): Float = value.flsdpLw(context)

    fun flsdpLwa(context: AppDimensContext, value: Int): Float = value.flsdpLwa(context)

    fun flsdpLwi(context: AppDimensContext, value: Int): Float = value.flsdpLwi(context)

    fun flsdpLwia(context: AppDimensContext, value: Int): Float = value.flsdpLwia(context)

    /**
     * EN Quick resolution for Screen Height (hdp).
     * PT Resolução rápida para Altura da Tela (hdp).
     */
    fun flhdp(context: AppDimensContext, value: Int): Float = value.flhdp(context)

    fun flhdpa(context: AppDimensContext, value: Int): Float = value.flhdpa(context)

    fun flhdpi(context: AppDimensContext, value: Int): Float = value.flhdpi(context)

    fun flhdpia(context: AppDimensContext, value: Int): Float = value.flhdpia(context)

    /**
     * EN Quick resolution for Screen Height (hdp), but in landscape orientation it acts as Screen Width (wdp).
     */
    fun flhdpLw(context: AppDimensContext, value: Int): Float = value.flhdpLw(context)

    fun flhdpLwa(context: AppDimensContext, value: Int): Float = value.flhdpLwa(context)

    fun flhdpLwi(context: AppDimensContext, value: Int): Float = value.flhdpLwi(context)

    fun flhdpLwia(context: AppDimensContext, value: Int): Float = value.flhdpLwia(context)

    /**
     * EN Quick resolution for Screen Height (hdp), but in portrait orientation it acts as Screen Width (wdp).
     */
    fun flhdpPw(context: AppDimensContext, value: Int): Float = value.flhdpPw(context)

    fun flhdpPwa(context: AppDimensContext, value: Int): Float = value.flhdpPwa(context)

    fun flhdpPwi(context: AppDimensContext, value: Int): Float = value.flhdpPwi(context)

    fun flhdpPwia(context: AppDimensContext, value: Int): Float = value.flhdpPwia(context)

    /**
     * EN Quick resolution for Screen Width (wdp).
     * PT Resolução rápida para Largura da Tela (wdp).
     */
    fun flwdp(context: AppDimensContext, value: Int): Float = value.flwdp(context)

    fun flwdpa(context: AppDimensContext, value: Int): Float = value.flwdpa(context)

    fun flwdpi(context: AppDimensContext, value: Int): Float = value.flwdpi(context)

    fun flwdpia(context: AppDimensContext, value: Int): Float = value.flwdpia(context)

    /**
     * EN Quick resolution for Screen Width (wdp), but in landscape orientation it acts as Screen Height (hdp).
     */
    fun flwdpLh(context: AppDimensContext, value: Int): Float = value.flwdpLh(context)

    fun flwdpLha(context: AppDimensContext, value: Int): Float = value.flwdpLha(context)

    fun flwdpLhi(context: AppDimensContext, value: Int): Float = value.flwdpLhi(context)

    fun flwdpLhia(context: AppDimensContext, value: Int): Float = value.flwdpLhia(context)

    /**
     * EN Quick resolution for Screen Width (wdp), but in portrait orientation it acts as Screen Height (hdp).
     */
    fun flwdpPh(context: AppDimensContext, value: Int): Float = value.flwdpPh(context)

    fun flwdpPha(context: AppDimensContext, value: Int): Float = value.flwdpPha(context)

    fun flwdpPhi(context: AppDimensContext, value: Int): Float = value.flwdpPhi(context)

    fun flwdpPhia(context: AppDimensContext, value: Int): Float = value.flwdpPhia(context)

    // EN Qualifier-based conditional dynamic scaling.
    // PT Escalonamento condicional baseado em qualificador.

    /**
     * EN Quick resolution for Smallest Width (swDP) conditional scaling.
     */
    fun flsdpQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flsdpQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hDP) conditional scaling.
     */
    fun flhdpQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flhdpQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wDP) conditional scaling.
     */
    fun flwdpQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flwdpQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType + DpQualifier combined facilitator extensions.
    // PT Extensões facilitadoras combinadas UiModeType + DpQualifier.

    /**
     * EN Quick resolution for Smallest Width (swDP) context conditional scaling.
     */
    fun flsdpScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flsdpScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hDP) context conditional scaling.
     */
    fun flhdpScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flhdpScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wDP) context conditional scaling.
     */
    fun flwdpScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flwdpScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

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
    ): Float = value.toDynamicFillPx(context, qualifier, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

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
    ): Float = value.toDynamicFillDp(context, qualifier, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Starts the build chain for the custom dimension DimenFill from a base Int.
     * PT Inicia a cadeia de construção para a dimensão customizada DimenFill a partir de um Int base.
     */
    fun scaled(initialBaseValue: Int): DimenFill = DimenFill(initialBaseValue.toFloat())

    /**
     * EN Starts the build chain for the custom dimension DimenFill from a base Float.
     */
    fun scaled(initialBaseValue: Float): DimenFill = DimenFill(initialBaseValue)

    // EN Rotation facilitator functions for Java.
    // PT Funções facilitadoras de rotação para Java.

    /**
     * EN Facilitator for Smallest Width (sdp) with rotation override.
     */
    fun flsdpRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.SMALL_WIDTH, orientation: Orientation = Orientation.LANDSCAPE, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flsdpRotate(context, rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (hdp) with rotation override.
     */
    fun flhdpRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.HEIGHT, orientation: Orientation = Orientation.LANDSCAPE, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flhdpRotate(context, rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (wdp) with rotation override.
     */
    fun flwdpRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.WIDTH, orientation: Orientation = Orientation.LANDSCAPE, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flwdpRotate(context, rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType facilitator functions for Java.
    // PT Funções facilitadoras de UiModeType para Java.

    /**
     * EN Facilitator for Smallest Width (sdp) with UiModeType override.
     */
    fun flsdpMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flsdpMode(context, modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (hdp) with UiModeType override.
     */
    fun flhdpMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flhdpMode(context, modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (wdp) with UiModeType override.
     */
    fun flwdpMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.flwdpMode(context, modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
}