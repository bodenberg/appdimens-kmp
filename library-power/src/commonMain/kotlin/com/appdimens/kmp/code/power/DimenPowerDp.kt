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
package com.appdimens.kmp.code.power

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
object DimenPowerDp {

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
    fun pwsdp(context: AppDimensContext, value: Int): Float = value.pwsdp(context)

    /**
     * EN Quick resolution for Smallest Width (sdpa) with aspect ratio.
     * PT Resolução rápida para Smallest Width (sdpa) com proporção de tela.
     */
    fun pwsdpa(context: AppDimensContext, value: Int): Float = value.pwsdpa(context)

    /**
     * EN Quick resolution for Smallest Width (sdpi) ignoring multi-windows.
     * PT Resolução rápida para Smallest Width (sdpi) ignorando janelas múltiplas.
     */
    fun pwsdpi(context: AppDimensContext, value: Int): Float = value.pwsdpi(context)

    /**
     * EN Quick resolution for Smallest Width (sdpia) ignoring multi-windows and with aspect ratio.
     * PT Resolução rápida para Smallest Width (sdpia) ignorando janelas múltiplas e com proporção.
     */
    fun pwsdpia(context: AppDimensContext, value: Int): Float = value.pwsdpia(context)

    /**
     * EN Quick resolution for Smallest Width (sdp), but in portrait orientation it acts as Screen Height (hdp).
     * PT Resolução rápida para Smallest Width (sdp), mas na orientação retrato atua como Altura da Tela (hdp).
     */
    fun pwsdpPh(context: AppDimensContext, value: Int): Float = value.pwsdpPh(context)

    fun pwsdpPha(context: AppDimensContext, value: Int): Float = value.pwsdpPha(context)

    fun pwsdpPhi(context: AppDimensContext, value: Int): Float = value.pwsdpPhi(context)

    fun pwsdpPhia(context: AppDimensContext, value: Int): Float = value.pwsdpPhia(context)

    /**
     * EN Quick resolution for Smallest Width (sdp), but in landscape orientation it acts as Screen Height (hdp).
     * PT Resolução rápida para Smallest Width (sdp), mas na orientação paisagem atua como Altura da Tela (hdp).
     */
    fun pwsdpLh(context: AppDimensContext, value: Int): Float = value.pwsdpLh(context)

    fun pwsdpLha(context: AppDimensContext, value: Int): Float = value.pwsdpLha(context)

    fun pwsdpLhi(context: AppDimensContext, value: Int): Float = value.pwsdpLhi(context)

    fun pwsdpLhia(context: AppDimensContext, value: Int): Float = value.pwsdpLhia(context)

    /**
     * EN Quick resolution for Smallest Width (sdp), but in portrait orientation it acts as Screen Width (wdp).
     * PT Resolução rápida para Smallest Width (sdp), mas na orientação retrato atua como Largura da Tela (wdp).
     */
    fun pwsdpPw(context: AppDimensContext, value: Int): Float = value.pwsdpPw(context)

    fun pwsdpPwa(context: AppDimensContext, value: Int): Float = value.pwsdpPwa(context)

    fun pwsdpPwi(context: AppDimensContext, value: Int): Float = value.pwsdpPwi(context)

    fun pwsdpPwia(context: AppDimensContext, value: Int): Float = value.pwsdpPwia(context)

    /**
     * EN Quick resolution for Smallest Width (sdp), but in landscape orientation it acts as Screen Width (wdp).
     * PT Resolução rápida para Smallest Width (sdp), mas na orientação paisagem atua como Largura da Tela (wdp).
     */
    fun pwsdpLw(context: AppDimensContext, value: Int): Float = value.pwsdpLw(context)

    fun pwsdpLwa(context: AppDimensContext, value: Int): Float = value.pwsdpLwa(context)

    fun pwsdpLwi(context: AppDimensContext, value: Int): Float = value.pwsdpLwi(context)

    fun pwsdpLwia(context: AppDimensContext, value: Int): Float = value.pwsdpLwia(context)

    /**
     * EN Quick resolution for Screen Height (hdp).
     * PT Resolução rápida para Altura da Tela (hdp).
     */
    fun pwhdp(context: AppDimensContext, value: Int): Float = value.pwhdp(context)

    fun pwhdpa(context: AppDimensContext, value: Int): Float = value.pwhdpa(context)

    fun pwhdpi(context: AppDimensContext, value: Int): Float = value.pwhdpi(context)

    fun pwhdpia(context: AppDimensContext, value: Int): Float = value.pwhdpia(context)

    /**
     * EN Quick resolution for Screen Height (hdp), but in landscape orientation it acts as Screen Width (wdp).
     */
    fun pwhdpLw(context: AppDimensContext, value: Int): Float = value.pwhdpLw(context)

    fun pwhdpLwa(context: AppDimensContext, value: Int): Float = value.pwhdpLwa(context)

    fun pwhdpLwi(context: AppDimensContext, value: Int): Float = value.pwhdpLwi(context)

    fun pwhdpLwia(context: AppDimensContext, value: Int): Float = value.pwhdpLwia(context)

    /**
     * EN Quick resolution for Screen Height (hdp), but in portrait orientation it acts as Screen Width (wdp).
     */
    fun pwhdpPw(context: AppDimensContext, value: Int): Float = value.pwhdpPw(context)

    fun pwhdpPwa(context: AppDimensContext, value: Int): Float = value.pwhdpPwa(context)

    fun pwhdpPwi(context: AppDimensContext, value: Int): Float = value.pwhdpPwi(context)

    fun pwhdpPwia(context: AppDimensContext, value: Int): Float = value.pwhdpPwia(context)

    /**
     * EN Quick resolution for Screen Width (wdp).
     * PT Resolução rápida para Largura da Tela (wdp).
     */
    fun pwwdp(context: AppDimensContext, value: Int): Float = value.pwwdp(context)

    fun pwwdpa(context: AppDimensContext, value: Int): Float = value.pwwdpa(context)

    fun pwwdpi(context: AppDimensContext, value: Int): Float = value.pwwdpi(context)

    fun pwwdpia(context: AppDimensContext, value: Int): Float = value.pwwdpia(context)

    /**
     * EN Quick resolution for Screen Width (wdp), but in landscape orientation it acts as Screen Height (hdp).
     */
    fun pwwdpLh(context: AppDimensContext, value: Int): Float = value.pwwdpLh(context)

    fun pwwdpLha(context: AppDimensContext, value: Int): Float = value.pwwdpLha(context)

    fun pwwdpLhi(context: AppDimensContext, value: Int): Float = value.pwwdpLhi(context)

    fun pwwdpLhia(context: AppDimensContext, value: Int): Float = value.pwwdpLhia(context)

    /**
     * EN Quick resolution for Screen Width (wdp), but in portrait orientation it acts as Screen Height (hdp).
     */
    fun pwwdpPh(context: AppDimensContext, value: Int): Float = value.pwwdpPh(context)

    fun pwwdpPha(context: AppDimensContext, value: Int): Float = value.pwwdpPha(context)

    fun pwwdpPhi(context: AppDimensContext, value: Int): Float = value.pwwdpPhi(context)

    fun pwwdpPhia(context: AppDimensContext, value: Int): Float = value.pwwdpPhia(context)

    // EN Qualifier-based conditional dynamic scaling.
    // PT Escalonamento condicional baseado em qualificador.

    /**
     * EN Quick resolution for Smallest Width (swDP) conditional scaling.
     */
    fun pwsdpQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwsdpQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hDP) conditional scaling.
     */
    fun pwhdpQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwhdpQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wDP) conditional scaling.
     */
    fun pwwdpQualifier(context: AppDimensContext, value: Int, qualifiedValue: Number, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwwdpQualifier(context, qualifiedValue, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType + DpQualifier combined facilitator extensions.
    // PT Extensões facilitadoras combinadas UiModeType + DpQualifier.

    /**
     * EN Quick resolution for Smallest Width (swDP) context conditional scaling.
     */
    fun pwsdpScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwsdpScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Height (hDP) context conditional scaling.
     */
    fun pwhdpScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwhdpScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Quick resolution for Screen Width (wDP) context conditional scaling.
     */
    fun pwwdpScreen(context: AppDimensContext, value: Int, screenValue: Number, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwwdpScreen(context, screenValue, uiModeType, qualifierType, qualifierValue, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

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
    ): Float = value.toDynamicPowerPx(context, qualifier, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

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
    ): Float = value.toDynamicPowerDp(context, qualifier, inverter, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Starts the build chain for the custom dimension DimenPower from a base Int.
     * PT Inicia a cadeia de construção para a dimensão customizada DimenPower a partir de um Int base.
     */
    fun scaled(initialBaseValue: Int): DimenPower = DimenPower(initialBaseValue.toFloat())

    /**
     * EN Starts the build chain for the custom dimension DimenPower from a base Float.
     */
    fun scaled(initialBaseValue: Float): DimenPower = DimenPower(initialBaseValue)

    // EN Rotation facilitator functions for Java.
    // PT Funções facilitadoras de rotação para Java.

    /**
     * EN Facilitator for Smallest Width (sdp) with rotation override.
     */
    fun pwsdpRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.SMALL_WIDTH, orientation: Orientation = Orientation.LANDSCAPE, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwsdpRotate(context, rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (hdp) with rotation override.
     */
    fun pwhdpRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.HEIGHT, orientation: Orientation = Orientation.LANDSCAPE, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwhdpRotate(context, rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (wdp) with rotation override.
     */
    fun pwwdpRotate(context: AppDimensContext, value: Int, rotationValue: Number, finalQualifierResolver: DpQualifier = DpQualifier.WIDTH, orientation: Orientation = Orientation.LANDSCAPE, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwwdpRotate(context, rotationValue, finalQualifierResolver, orientation, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    // EN UiModeType facilitator functions for Java.
    // PT Funções facilitadoras de UiModeType para Java.

    /**
     * EN Facilitator for Smallest Width (sdp) with UiModeType override.
     */
    fun pwsdpMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwsdpMode(context, modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Height (hdp) with UiModeType override.
     */
    fun pwhdpMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwhdpMode(context, modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)

    /**
     * EN Facilitator for Screen Width (wdp) with UiModeType override.
     */
    fun pwwdpMode(context: AppDimensContext, value: Int, modeValue: Number, uiModeType: UiModeType, finalQualifierResolver: DpQualifier? = null, ignoreMultiWindows: Boolean = false, applyAspectRatio: Boolean = false, customSensitivityK: Float? = null): Float =
        value.pwwdpMode(context, modeValue, uiModeType, finalQualifierResolver, ignoreMultiWindows, applyAspectRatio, customSensitivityK)
}