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
package com.appdimens.kmp.code.custom

import com.appdimens.kmp.core.AppDimensContext
import com.appdimens.kmp.core.DimenCache
import com.appdimens.kmp.custom.ScalingStrategies
import com.appdimens.kmp.custom.ScalingStrategy
import com.appdimens.kmp.custom.ScreenSnapshot
import com.appdimens.kmp.custom.cacheKey
import com.appdimens.kmp.custom.metricsOf
import com.appdimens.kmp.custom.resolve
import com.appdimens.kmp.custom.toScreenSnapshot

// EN Non-Compose API (any KMP target). Like the other `code.*` packages, the short names return **px**.
// PT API sem Compose (qualquer alvo KMP). Como os outros pacotes `code.*`, os nomes curtos retornam **px**.

/**
 * EN Scales this dp value with [strategy] → **px**. `16.cdp(context, Brand)`.
 * PT Escala este valor dp com [strategy] → **px**.
 */
fun Number.cdp(context: AppDimensContext, strategy: ScalingStrategy = ScalingStrategies.default): Float {
    val base = toFloat()
    return DimenCache.getOrPut(strategy.cacheKey(base, DimenCache.ValueType.PX), context) {
        val m = DimenCache.currentMetrics
        strategy.resolve(m).scale(base) * m.density
    }
}

/**
 * EN Scales this dp value with [strategy] → **dp** (`Float`).
 * PT Escala este valor dp com [strategy] → **dp** (`Float`).
 */
fun Number.cdpDp(context: AppDimensContext, strategy: ScalingStrategy = ScalingStrategies.default): Float {
    val base = toFloat()
    return DimenCache.getOrPut(strategy.cacheKey(base, DimenCache.ValueType.DP), context) {
        strategy.resolve(DimenCache.currentMetrics).scale(base)
    }
}

/**
 * EN Scales this sp value for text → **px**, applying the strategy font-scale policy.
 * PT Escala este valor sp para texto → **px**, aplicando a política de font scale.
 */
fun Number.csp(context: AppDimensContext, strategy: ScalingStrategy = ScalingStrategies.default): Float {
    val base = toFloat()
    return DimenCache.getOrPut(strategy.cacheKey(base, DimenCache.ValueType.SP_PX_WITH_SCALE), context) {
        val m = DimenCache.currentMetrics
        strategy.resolve(m).scaleSpDp(base) * m.density
    }
}

/**
 * EN Text size in **sp units** for `an sp-based text API`
 * (the system applies its font scale; the strategy policy is pre-compensated).
 * PT Tamanho em **unidades sp** para uma API de texto em sp.
 */
fun Number.cspUnits(context: AppDimensContext, strategy: ScalingStrategy = ScalingStrategies.default): Float {
    val base = toFloat()
    return DimenCache.getOrPut(strategy.cacheKey(base, DimenCache.ValueType.SP_WITH_SCALE), context) {
        strategy.resolve(DimenCache.currentMetrics).scaleSpUnits(base)
    }
}

// EN Strategy-first facilitators. PT Facilitadores a partir da estratégia.

/** EN `Brand.px(context, 16)` → px. */
fun ScalingStrategy.px(context: AppDimensContext, value: Number): Float = value.cdp(context, this)

/** EN `Brand.dp(context, 16)` → dp. */
fun ScalingStrategy.dp(context: AppDimensContext, value: Number): Float = value.cdpDp(context, this)

/** EN `Brand.spPx(context, 14)` → px for text. */
fun ScalingStrategy.spPx(context: AppDimensContext, value: Number): Float = value.csp(context, this)

/** EN Current factor for the window of [context]. PT Fator atual da janela de [context]. */
fun ScalingStrategy.factor(context: AppDimensContext): Float = resolve(DimenCache.metricsOf(context)).factor

/** EN Debug breakdown for the window of [context]. PT Explicação para a janela de [context]. */
fun ScalingStrategy.explain(context: AppDimensContext): String = resolve(DimenCache.metricsOf(context)).explain(id)

/** EN Platform-free snapshot of the window of this context. */
fun AppDimensContext.screenSnapshot(): ScreenSnapshot = DimenCache.metricsOf(this).toScreenSnapshot()
