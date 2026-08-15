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
package com.appdimens.kmp.common

/**
 * EN Defines the Android UI Mode Types for dimension customization,
 * based on Configuration.uiMode.
 *
 * PT Define os tipos de modo de interface do usuário (UI Mode Type) do Android
 * para customização de dimensões, com base em Configuration.uiMode.
 *
 * KMP note: the `configValue` constants mirror the Android
 * `Configuration.UI_MODE_TYPE_*` values so entries stay source-compatible.
 */
enum class UiModeType(val configValue: Int) {
    /**
     * EN Default Phone/Tablet.
     *
     * PT Telefone/Tablet Padrão.
     */
    NORMAL(1),

    /**
     * EN Television.
     *
     * PT Televisão.
     */
    TELEVISION(2),

    /**
     * EN Car.
     *
     * PT Carro.
     */
    CAR(3),

    /**
     * EN Watch (Wear OS).
     *
     * PT Relógio (Wear OS).
     */
    WATCH(4),

    /**
     * EN Desk Device (Docked).
     *
     * PT Dispositivo de Mesa (Docked).
     */
    DESK(5),

    /**
     * EN Projection Device (e.g., Android Auto, Cast).
     *
     * PT Dispositivo de Projeção (e.g., Android Auto, Cast).
     */
    APPLIANCE(6),

    /**
     * EN Virtual Reality (VR) Device.
     *
     * PT Dispositivo de Realidade Virtual (VR).
     */
    VR_HEADSET(7),

    /**
     * EN Any unspecified/other UI mode.
     *
     * PT Qualquer modo de UI não especificado/outros.
     */
    UNDEFINED(0),

    /**
     * EN Foldable Device (Open state).
     * PT Dispositivo Dobrável tipo Fold (Estado aberto).
     */
    FOLD_OPEN(-101),

    /**
     * EN Foldable Device (Closed state).
     * PT Dispositivo Dobrável tipo Fold (Estado fechado).
     */
    FOLD_CLOSED(-102),

    /**
     * EN Flip Device (Open state).
     * PT Dispositivo Dobrável tipo Flip (Estado aberto).
     */
    FLIP_OPEN(-103),

    /**
     * EN Flip Device (Closed state).
     * PT Dispositivo Dobrável tipo Flip (Estado fechado).
     */
    FLIP_CLOSED(-104),

    /**
     * EN Foldable Device (Half-opened state).
     * PT Dispositivo Dobrável tipo Fold (Estado semiaberto).
     */
    FOLD_HALF_OPENED(-105),

    /**
     * EN Flip Device (Half-opened state).
     * PT Dispositivo Dobrável tipo Flip (Estado semiaberto).
     */
    FLIP_HALF_OPENED(-106);

    companion object {
        /**
         * EN Resolves the [UiModeType] for a raw `uiMode` int value (platform
         * agnostic; Android detection including foldables lives in the
         * platform-specific actual).
         * PT Resolve o [UiModeType] para um valor `uiMode` bruto.
         */
        fun fromConfigurationValue(configValue: Int): UiModeType =
            entries.firstOrNull { it.configValue == configValue } ?: NORMAL
    }
}