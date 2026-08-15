@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

/**
 * Author & Developer: Jean Bodenberg
 * GIT: https://github.com/bodenberg/appdimens.git
 * Date: 2025-10-04
 *
 * Library: AppDimens
 *
 * Description:
 * Physical units conversion utilities for AppDimens Android Code library,
 * providing conversion between physical measurements and Dp/Px/Sp.
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
package com.appdimens.dynamic.code.units

import com.appdimens.dynamic.core.AppDimensContext
import com.appdimens.dynamic.core.ScreenConfiguration
import com.appdimens.dynamic.core.currentScreenConfiguration
import com.appdimens.dynamic.core.localAppDimensContext

import com.appdimens.dynamic.common.UnitType

/**
 * EN Utility class for physical unit conversions.
 * PT Classe utilitária para conversões de unidades físicas.
 */
object DimenPhysicalUnits {

    private const val MM_TO_INCH_FACTOR = 25.4f

    // MARK: - Conversion Methods

    /**
     * EN Converts millimeters to Dp.
     * PT Converte milímetros para Dp.
     */
    fun toDpFromMm(mm: Float, appContext: AppDimensContext?): Float {
        val density = appContext?.density ?: 1f
        val xdpi = appContext?.xdpi ?: 160f
        return mm * xdpi / MM_TO_INCH_FACTOR / density
    }

    /**
     * EN Converts centimeters to Dp.
     * PT Converte centímetros para Dp.
     */
    fun toDpFromCm(cm: Float, appContext: AppDimensContext?): Float =
        toDpFromMm(cm * 10f, appContext)

    /**
     * EN Converts inches to Dp.
     * PT Converte polegadas para Dp.
     */
    fun toDpFromInch(inch: Float, appContext: AppDimensContext?): Float {
        val density = appContext?.density ?: 1f
        val xdpi = appContext?.xdpi ?: 160f
        return inch * xdpi / MM_TO_INCH_FACTOR / density
    }

    /**
     * EN Converts millimeters to Pixels.
     * PT Converte milímetros para Pixels.
     */
    fun toPxFromMm(mm: Float, appContext: AppDimensContext?): Float =
        toDpFromMm(mm, appContext) * (appContext?.density ?: 1f)

    /**
     * EN Converts centimeters to Pixels.
     * PT Converte centímetros para Pixels.
     */
    fun toPxFromCm(cm: Float, appContext: AppDimensContext?): Float =
        toPxFromMm(cm * 10f, appContext)

    /**
     * EN Converts inches to Pixels.
     * PT Converte polegadas para Pixels.
     */
    fun toPxFromInch(inch: Float, appContext: AppDimensContext?): Float =
        toDpFromInch(inch, appContext) * (appContext?.density ?: 1f)

    /**
     * EN Converts millimeters to SP.
     * PT Converte milímetros para SP.
     */
    fun toSpFromMm(mm: Float, appContext: AppDimensContext?): Float {
        val density = appContext?.density ?: 1f
        val fontScale = appContext?.configuration?.fontScale ?: 1f
        return toPxFromMm(mm, appContext) / (density * fontScale)
    }

    /**
     * EN Converts centimeters to SP.
     * PT Converte centímetros para SP.
     */
    fun toSpFromCm(cm: Float, appContext: AppDimensContext?): Float {
        val density = appContext?.density ?: 1f
        val fontScale = appContext?.configuration?.fontScale ?: 1f
        return toPxFromCm(cm, appContext) / (density * fontScale)
    }

    /**
     * EN Converts inches to SP.
     * PT Converte polegadas para SP.
     */
    fun toSpFromInch(inch: Float, appContext: AppDimensContext?): Float {
        val density = appContext?.density ?: 1f
        val fontScale = appContext?.configuration?.fontScale ?: 1f
        return toPxFromInch(inch, appContext) / (density * fontScale)
    }

    // MARK: - Utility Methods

    /**
     * EN Converts a diameter value in a specific physical unit to radius in Dp.
     * @param diameter The diameter value.
     * @param unitType The unit type (mm, cm, inch).
     * @param resources The AppDimensContext's Resources.
     * @return The radius in Dp.
     * PT Converte um valor de diâmetro em uma unidade física específica para raio em Dp.
     * @param diameter O valor do diâmetro.
     * @param unitType O tipo de unidade (mm, cm, inch).
     * @param resources Os Resources do AppDimensContext.
     * @return O raio em Dp.
     */
    fun radiusFromDiameter(diameter: Float, unitType: UnitType, appContext: AppDimensContext?): Float {
        val diameterInDp = when (unitType) {
            UnitType.MM -> toDpFromMm(diameter, appContext)
            UnitType.CM -> toDpFromCm(diameter, appContext)
            UnitType.INCH -> toDpFromInch(diameter, appContext)
            UnitType.DP -> diameter
            UnitType.SP -> diameter * (appContext?.configuration?.fontScale ?: 1f)
            UnitType.PX -> diameter / (appContext?.density ?: 1f)
        }
        
        return diameterInDp / 2.0f
    }

    /**
     * EN Converts a circumference value in a specific physical unit to radius in Dp.
     * @param circumference The circumference value.
     * @param unitType The unit type (mm, cm, inch, dp, sp, px).
     * @param resources The AppDimensContext's Resources.
     * @return The radius in Dp.
     * PT Converte um valor de circunferência em uma unidade física específica para raio em Dp.
     * @param circumference O valor da circunferência.
     * @param unitType O tipo de unidade (mm, cm, inch, dp, sp, px).
     * @param resources Os Resources do AppDimensContext.
     * @return O raio em Dp.
     */
    fun radiusFromCircumference(circumference: Float, unitType: UnitType, appContext: AppDimensContext?): Float {
        val circumferenceInDp = when (unitType) {
            UnitType.MM -> toDpFromMm(circumference, appContext)
            UnitType.CM -> toDpFromCm(circumference, appContext)
            UnitType.INCH -> toDpFromInch(circumference, appContext)
            UnitType.DP -> circumference
            UnitType.SP -> circumference * (appContext?.configuration?.fontScale ?: 1f)
            UnitType.PX -> circumference / (appContext?.density ?: 1f)
        }
        
        return circumferenceInDp / (2.0f * kotlin.math.PI.toFloat())
    }

    // MARK: - Conversion Extensions
    /**
     * EN Float extension to convert MM to CM.
     * PT Extensão de Float para converter MM para CM.
     */
    fun Float.mmToCm(): Float = this / 10.0f

    /**
     * EN Float extension to convert MM to Inch.
     * PT Extensão de Float para converter MM para Inch.
     */
    fun Float.mmToInch(): Float = this / 25.4f

    /**
     * EN Float extension to convert CM to MM.
     * PT Extensão de Float para converter CM para MM.
     */
    fun Float.cmToMm(): Float = this * 10.0f

    /**
     * EN Float extension to convert CM to Inch.
     * PT Extensão de Float para converter CM para Inch.
     */
    fun Float.cmToInch(): Float = this / 2.54f

    /**
     * EN Float extension to convert Inch to MM.
     * PT Extensão de Float para converter Inch para MM.
     */
    fun Float.inchToMm(): Float = this * 25.4f

    /**
     * EN Float extension to convert Inch to CM.
     * PT Extensão de Float para converter Inch para CM.
     */
    fun Float.inchToCm(): Float = this * 2.54f
}