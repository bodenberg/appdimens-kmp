/**
 * Author & Developer: Jean Bodenberg
 * GIT: https://github.com/bodenberg/appdimens-kmp.git
 *
 * Library: AppDimens — Custom scaling strategies
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
package com.appdimens.kmp.compose.custom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.appdimens.kmp.core.DimenCache
import com.appdimens.kmp.core.ScreenConfiguration
import com.appdimens.kmp.core.currentScreenConfiguration
import com.appdimens.kmp.core.localAppDimensContext
import com.appdimens.kmp.core.layoutRememberStamp
import com.appdimens.kmp.core.pxRememberStamp
import com.appdimens.kmp.core.rememberDimenDp
import com.appdimens.kmp.core.rememberDimenPxFromDp
import com.appdimens.kmp.core.rememberDimenSp
import com.appdimens.kmp.core.rememberDimenSpPx
import com.appdimens.kmp.core.spRememberStamp
import com.appdimens.kmp.custom.ScalingStrategies
import com.appdimens.kmp.custom.ScalingStrategy
import com.appdimens.kmp.custom.cacheKey
import com.appdimens.kmp.custom.metricsOf
import com.appdimens.kmp.custom.resolve

// EN Scope ─────────────────────────────────────────────────────────────────────
// PT Escopo ────────────────────────────────────────────────────────────────────

/**
 * EN Strategy provided to a subtree by [ProvideScalingStrategy]; `null` means
 * [ScalingStrategies.default].
 * PT Estratégia fornecida a uma subárvore; `null` significa [ScalingStrategies.default].
 */
val LocalScalingStrategy = compositionLocalOf<ScalingStrategy?> { null }

/**
 * EN Every `16.cdp` / `14.csp` without an explicit strategy inside [content] uses [strategy].
 * PT Todo `16.cdp` / `14.csp` sem estratégia explícita dentro de [content] usa [strategy].
 */
@Composable
fun ProvideScalingStrategy(strategy: ScalingStrategy, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalScalingStrategy provides strategy, content = content)
}

/** EN Strategy in effect at this point of the composition. PT Estratégia em vigor aqui. */
val currentScalingStrategy: ScalingStrategy
    @Composable @ReadOnlyComposable get() = LocalScalingStrategy.current ?: ScalingStrategies.default

/**
 * EN Layout stamp plus the UI mode, so a rule keyed on the device class (car dock, desk…)
 * recomputes when only the UI mode changes.
 */
private fun customLayoutStamp(configuration: ScreenConfiguration): Long =
    layoutRememberStamp(configuration) xor (configuration.uiMode.toLong() * -0x61c8864680b583ebL)

// EN Dp ────────────────────────────────────────────────────────────────────────

/**
 * EN Scales this dp value with [strategy] → [Dp]. `16.cdp(Brand)`.
 * PT Escala este valor dp com [strategy] → [Dp].
 */
@Composable
fun Number.cdp(strategy: ScalingStrategy): Dp {
    val configuration = currentScreenConfiguration()
    val appContext = localAppDimensContext()
    val base = toFloat()
    return rememberDimenDp(strategy.cacheKey(base, DimenCache.ValueType.DP), customLayoutStamp(configuration), appContext) {
        strategy.resolve(DimenCache.currentMetrics).scale(base)
    }
}

/** EN `16.cdp` — uses [currentScalingStrategy]. PT Usa a estratégia do escopo. */
@get:Composable
val Number.cdp: Dp get() = cdp(currentScalingStrategy)

/** EN Scaled value in **px**. PT Valor escalado em **px**. */
@Composable
fun Number.cdpPx(strategy: ScalingStrategy): Float {
    val configuration = currentScreenConfiguration()
    val appContext = localAppDimensContext()
    val density = LocalDensity.current
    val base = toFloat()
    val stamp = pxRememberStamp(customLayoutStamp(configuration), density)
    return rememberDimenPxFromDp(strategy.cacheKey(base, DimenCache.ValueType.DP), stamp, appContext, density) {
        strategy.resolve(DimenCache.currentMetrics).scale(base)
    }
}

@get:Composable
val Number.cdpPx: Float get() = cdpPx(currentScalingStrategy)

// EN Sp ────────────────────────────────────────────────────────────────────────

/**
 * EN Scales this sp value for text → [TextUnit], honoring the strategy font-scale policy
 * (`Follow` / `Ignore` / `Clamp`). `14.csp(Brand)`.
 * PT Escala este valor sp para texto, respeitando a política de font scale.
 */
@Composable
fun Number.csp(strategy: ScalingStrategy): TextUnit {
    val configuration = currentScreenConfiguration()
    val appContext = localAppDimensContext()
    val density = LocalDensity.current
    val base = toFloat()
    val key = strategy.cacheKey(base, DimenCache.ValueType.SP_WITH_SCALE)
    val stamp = spRememberStamp(customLayoutStamp(configuration), density)
    return rememberDimenSp(key, stamp) {
        DimenCache.getOrPut(key, appContext) {
            strategy.resolve(DimenCache.currentMetrics).scaleSpUnits(base)
        }.sp
    }
}

@get:Composable
val Number.csp: TextUnit get() = csp(currentScalingStrategy)

/** EN Text size in **px**. PT Tamanho de texto em **px**. */
@Composable
fun Number.cspPx(strategy: ScalingStrategy): Float {
    val configuration = currentScreenConfiguration()
    val appContext = localAppDimensContext()
    val density = LocalDensity.current
    val base = toFloat()
    val key = strategy.cacheKey(base, DimenCache.ValueType.SP_WITH_SCALE)
    val stamp = spRememberStamp(customLayoutStamp(configuration), density)
    return rememberDimenSpPx(key, stamp xor 0x5BD1E995L) {
        val units = DimenCache.getOrPut(key, appContext) {
            strategy.resolve(DimenCache.currentMetrics).scaleSpUnits(base)
        }
        with(density) { units.sp.toPx() }
    }
}

@get:Composable
val Number.cspPx: Float get() = cspPx(currentScalingStrategy)

// EN Facilitators ──────────────────────────────────────────────────────────────
// PT Facilitadores ─────────────────────────────────────────────────────────────

/** EN `Brand.dp(16)`. */
@Composable
fun ScalingStrategy.dp(value: Number): Dp = value.cdp(this)

/** EN `Brand.sp(14)`. */
@Composable
fun ScalingStrategy.sp(value: Number): TextUnit = value.csp(this)

/** EN `Brand.px(16)`. */
@Composable
fun ScalingStrategy.px(value: Number): Float = value.cdpPx(this)

/**
 * EN Scales an existing [Dp] with [strategy]: `16.dp.scaledBy(Brand)`.
 * PT Escala um [Dp] existente: `16.dp.scaledBy(Brand)`.
 */
@Composable
fun Dp.scaledBy(strategy: ScalingStrategy = currentScalingStrategy): Dp = value.cdp(strategy)

/**
 * EN Factor of [strategy] for the current window (debug overlays, previews).
 * PT Fator de [strategy] para a janela atual.
 */
@Composable
fun ScalingStrategy.currentFactor(): Float {
    val configuration = currentScreenConfiguration()
    val appContext = localAppDimensContext()
    return remember(this, customLayoutStamp(configuration)) {
        resolve(DimenCache.metricsOf(appContext)).factor
    }
}

/** EN Human-readable breakdown for the current window. PT Explicação para a janela atual. */
@Composable
fun ScalingStrategy.explainCurrent(): String {
    val configuration = currentScreenConfiguration()
    val appContext = localAppDimensContext()
    return remember(this, customLayoutStamp(configuration)) {
        resolve(DimenCache.metricsOf(appContext)).explain(id)
    }
}
