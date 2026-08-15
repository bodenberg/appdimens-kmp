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
import com.appdimens.kmp.core.currentScreenConfiguration
import com.appdimens.kmp.core.localAppDimensContext

import com.appdimens.kmp.common.DpQualifier
import com.appdimens.kmp.common.DpQualifierEntry
import com.appdimens.kmp.common.Inverter
import com.appdimens.kmp.common.Orientation
import com.appdimens.kmp.common.UiModeType
import com.appdimens.kmp.core.DimenCache

/**
 * EN
 * Represents a custom dimension entry with qualifiers and priority.
 * Used by the DimenAuto class to define specific values for screen conditions.
 *
 * PT
 * Representa uma entrada de dimensão customizada com qualificadores e prioridade.
 * Usada pela classe DimenAuto para definir valores específicos para condições de tela.
 */
data class CustomDpEntry(
    val uiModeType: UiModeType? = null,
    val dpQualifierEntry: DpQualifierEntry? = null,
    val orientation: Orientation? = Orientation.DEFAULT,
    val customValue: Float, // Representing DP
    val finalQualifierResolver: DpQualifier? = null,
    val priority: Int,
    val inverter: Inverter? = Inverter.DEFAULT
)

// EN Methods for creating the DimenAuto class.
// PT Métodos de criação da classe DimenAuto.

/**
 * EN Starts the build chain for the custom dimension DimenAuto from a base Float (Dp).
 * PT Inicia a cadeia de construção para a dimensão customizada DimenAuto a partir de um Float (Dp) base.
 */
fun Float.autoScaledDp(): DimenAuto = DimenAuto(this)

/**
 * EN Starts the build chain for the custom dimension DimenAuto from a base Int (Dp).
 * PT Inicia a cadeia de construção para a dimensão customizada DimenAuto a partir de um Int (Dp) base.
 */
fun Number.autoScaledDp(): DimenAuto = this.toFloat().autoScaledDp()

/**
 * EN
 * A class that allows defining custom dimensions
 * based on screen qualifiers (UiModeType, Width, Height, Smallest Width).
 *
 * The value is resolved using a AppDimensContext and uses the base value or a
 * custom value, applying dynamic scaling at the end.
 *
 * PT
 * Classe que permite a definição de dimensões customizadas
 * baseadas em qualificadores de tela (UiModeType, Largura, Altura, Smallest Width).
 */
class DimenAuto private constructor(
    private val initialBaseDp: Float,
    private val sortedCustomEntries: List<CustomDpEntry> = emptyList(),
    private val ignoreMultiWindows: Boolean = false,
    private val applyAspectRatio: Boolean = false,
    private val customSensitivityK: Float? = null
) {

    // EN Main constructor to start the chain.
    constructor(initialBaseDp: Float) : this(initialBaseDp, emptyList(), false, false, null)


    /**
     * EN Allow applying aspect ratio based constraint scaling.
     * PT Permite aplicar o escalonamento restrito baseado na proporção da tela (aspect ratio).
     */
    fun applyAspectRatio(apply: Boolean = true): DimenAuto {
        return DimenAuto(initialBaseDp, sortedCustomEntries, ignoreMultiWindows, apply, customSensitivityK)
    }

    /**
     * EN Allow ignoring the constraint scaling based on multi-window resizing properties.
     * PT Permite ignorar o escalonamento restrito baseado nas propriedades de redimensionamento de multi-janelas.
     */
    fun ignoreMultiWindows(ignore: Boolean = true): DimenAuto {
        return DimenAuto(initialBaseDp, sortedCustomEntries, ignore, applyAspectRatio, customSensitivityK)
    }

    private fun reorderEntries(newEntry: CustomDpEntry): List<CustomDpEntry> {
        return (sortedCustomEntries + newEntry).sortedWith(
            compareBy<CustomDpEntry> { it.priority }
                .thenByDescending { it.dpQualifierEntry?.value?.toFloat() ?: 0f }
        )
    }

    // EN Builder methods.

    fun screen(
        uiModeType: UiModeType,
        qualifierType: DpQualifier,
        qualifierValue: Number,
        orientation: Orientation? = Orientation.DEFAULT,
        customValue: Float,
        finalQualifierResolver: DpQualifier? = null,
        inverter: Inverter? = Inverter.DEFAULT
    ): DimenAuto {
        val entry = CustomDpEntry(
            uiModeType = uiModeType,
            dpQualifierEntry = DpQualifierEntry(qualifierType, qualifierValue),
            orientation = orientation,
            customValue = customValue,
            finalQualifierResolver = finalQualifierResolver,
            priority = 1,
            inverter = inverter
        )
        return DimenAuto(initialBaseDp, reorderEntries(entry), ignoreMultiWindows, applyAspectRatio, customSensitivityK)
    }

    fun screen(
        uiModeType: UiModeType,
        qualifierType: DpQualifier,
        qualifierValue: Number,
        customValue: Number,
        finalQualifierResolver: DpQualifier? = null,
        orientation: Orientation? = Orientation.DEFAULT,
        inverter: Inverter? = Inverter.DEFAULT
    ): DimenAuto = screen(uiModeType, qualifierType, qualifierValue, orientation, customValue.toFloat(), finalQualifierResolver, inverter)

    fun screen(
        type: UiModeType,
        customValue: Float,
        finalQualifierResolver: DpQualifier? = null,
        orientation: Orientation? = Orientation.DEFAULT,
        inverter: Inverter? = Inverter.DEFAULT
    ): DimenAuto {
        val entry = CustomDpEntry(
            uiModeType = type,
            orientation = orientation,
            customValue = customValue,
            finalQualifierResolver = finalQualifierResolver,
            priority = 2,
            inverter = inverter
        )
        return DimenAuto(initialBaseDp, reorderEntries(entry), ignoreMultiWindows, applyAspectRatio, customSensitivityK)
    }

    fun screen(
        type: UiModeType,
        customValue: Number,
        finalQualifierResolver: DpQualifier? = null,
        orientation: Orientation? = Orientation.DEFAULT,
        inverter: Inverter? = Inverter.DEFAULT
    ): DimenAuto = screen(type, customValue.toFloat(), finalQualifierResolver, orientation, inverter)

    fun screen(
        type: DpQualifier,
        value: Int,
        customValue: Float,
        finalQualifierResolver: DpQualifier? = null,
        orientation: Orientation? = Orientation.DEFAULT,
        inverter: Inverter? = Inverter.DEFAULT
    ): DimenAuto {
        val entry = CustomDpEntry(
            dpQualifierEntry = DpQualifierEntry(type, value),
            orientation = orientation,
            customValue = customValue,
            finalQualifierResolver = finalQualifierResolver,
            priority = 3,
            inverter = inverter
        )
        return DimenAuto(initialBaseDp, reorderEntries(entry), ignoreMultiWindows, applyAspectRatio, customSensitivityK)
    }

    fun screen(
        type: DpQualifier,
        value: Int,
        customValue: Number,
        finalQualifierResolver: DpQualifier? = null,
        orientation: Orientation? = Orientation.DEFAULT,
        inverter: Inverter? = Inverter.DEFAULT
    ): DimenAuto = screen(type, value, customValue.toFloat(), finalQualifierResolver, orientation, inverter)

    fun screen(
        orientation: Orientation = Orientation.DEFAULT,
        customValue: Float,
        finalQualifierResolver: DpQualifier? = null,
        inverter: Inverter? = Inverter.DEFAULT
    ): DimenAuto {
        val entry = CustomDpEntry(
            orientation = orientation,
            customValue = customValue,
            finalQualifierResolver = finalQualifierResolver,
            priority = 4,
            inverter = inverter
        )
        return DimenAuto(initialBaseDp, reorderEntries(entry), ignoreMultiWindows, applyAspectRatio, customSensitivityK)
    }

    fun screen(
        orientation: Orientation = Orientation.DEFAULT,
        customValue: Number,
        finalQualifierResolver: DpQualifier? = null,
        inverter: Inverter? = Inverter.DEFAULT
    ): DimenAuto = screen(orientation, customValue.toFloat(), finalQualifierResolver, inverter)

    // EN Resolution logic.

    private fun resolveDp(context: AppDimensContext, qualifier: DpQualifier): Float {
        val configuration = context.configuration
        val currentUiModeType = DimenCache.getCachedUiModeType(context)
        return resolveDpInternal(context, qualifier, configuration, currentUiModeType)
    }

    /**
     * EN Resolves asdp, ahdp, and awdp in one pass (single [UiModeType.fromConfiguration] and config read).
     * PT Resolve asdp, ahdp e awdp numa só passagem.
     */
    fun asdpAhdpAwdpPx(context: AppDimensContext): Triple<Float, Float, Float> {
        val configuration = context.configuration
        val currentUiModeType = DimenCache.getCachedUiModeType(context)
        val density = context.density
        val asdp = resolveDpInternal(context, DpQualifier.SMALL_WIDTH, configuration, currentUiModeType) * density
        val ahdp = resolveDpInternal(context, DpQualifier.HEIGHT, configuration, currentUiModeType) * density
        val awdp = resolveDpInternal(context, DpQualifier.WIDTH, configuration, currentUiModeType) * density
        return Triple(asdp, ahdp, awdp)
    }

    private fun resolveDpInternal(
        context: AppDimensContext,
        qualifier: DpQualifier,
        configuration: ScreenConfiguration,
        currentUiModeType: UiModeType
    ): Float {
        val isLandscape = configuration.orientation == ScreenConfiguration.ORIENTATION_LANDSCAPE
        val isPortrait = configuration.orientation == ScreenConfiguration.ORIENTATION_PORTRAIT

        val foundEntry = sortedCustomEntries.firstOrNull { entry ->
            val qualifierEntry = entry.dpQualifierEntry
            val uiModeMatch = entry.uiModeType == null || entry.uiModeType == currentUiModeType
            val orientationMatch = when (entry.orientation) {
                Orientation.LANDSCAPE -> isLandscape
                Orientation.PORTRAIT -> isPortrait
                else -> true
            }

            if (qualifierEntry != null) {
                val qualifierMatch = getQualifierValue(qualifierEntry.type, configuration) >= qualifierEntry.value.toFloat()
                if (entry.priority == 1 && uiModeMatch && qualifierMatch && orientationMatch) return@firstOrNull true
                if (entry.priority == 3 && qualifierMatch && orientationMatch) return@firstOrNull true
                false
            } else {
                if (entry.priority == 2 && uiModeMatch && orientationMatch) return@firstOrNull true
                if (entry.priority == 4 && orientationMatch) return@firstOrNull true
                false
            }
        }

        val dpToUse = foundEntry?.customValue ?: initialBaseDp
        val finalQualifier = foundEntry?.finalQualifierResolver ?: qualifier

        return dpToUse.toDynamicAutoDp(
            context,
            finalQualifier,
            foundEntry?.inverter ?: Inverter.DEFAULT,
            ignoreMultiWindows,
            applyAspectRatio,
            customSensitivityK
        )
    }

    /**
     * EN Resolves the final value in pixels (Float).
     */
    fun px(context: AppDimensContext, qualifier: DpQualifier): Float {
        val configuration = context.configuration
        val currentUiModeType = DimenCache.getCachedUiModeType(context)
        val dpValue = resolveDpInternal(context, qualifier, configuration, currentUiModeType)
        return dpValue * context.density
    }

    // EN Convenience properties/methods similar to Compose version.

    fun asdp(context: AppDimensContext): Float = px(context, DpQualifier.SMALL_WIDTH)
    fun ahdp(context: AppDimensContext): Float = px(context, DpQualifier.HEIGHT)
    fun awdp(context: AppDimensContext): Float = px(context, DpQualifier.WIDTH)

    /** EN Get the resolved value in DP (as Float). */
    fun asdpBase(context: AppDimensContext): Float = resolveDp(context, DpQualifier.SMALL_WIDTH)
    fun ahdpBase(context: AppDimensContext): Float = resolveDp(context, DpQualifier.HEIGHT)
    fun awdpBase(context: AppDimensContext): Float = resolveDp(context, DpQualifier.WIDTH)
}