/**
 * Author & Developer: Jean Bodenberg
 * GIT: https://github.com/bodenberg/appdimens-kmp.git
 *
 * Library: AppDimens KMP — Custom scaling strategies
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
package com.appdimens.kmp.custom

import com.appdimens.kmp.core.AppDimensContext
import com.appdimens.kmp.core.DimenCache
import com.appdimens.kmp.core.DimenMetrics
import com.appdimens.kmp.core.ScreenConfiguration

/**
 * EN Device class for the current target when the snapshot only carries the generic
 * `uiMode = 1` (desktop JVM / macOS / Linux / Windows report [DeviceClass.DESKTOP];
 * Android decodes the real UI mode; iOS / web classify by smallest width).
 * PT Classe de dispositivo do alvo atual quando o snapshot só traz `uiMode = 1`.
 */
internal expect fun platformDeviceClass(uiMode: Int, smallestWidthDp: Float): DeviceClass

/**
 * EN Converts the core per-window snapshot into the platform-free [ScreenSnapshot].
 * PT Converte o snapshot por janela do core no [ScreenSnapshot] independente de plataforma.
 */
fun DimenMetrics.toScreenSnapshot(): ScreenSnapshot = ScreenSnapshot(
    widthDp = screenWidthDp.toFloat(),
    heightDp = screenHeightDp.toFloat(),
    smallestWidthDp = smallestWidthDp,
    density = density,
    fontScale = fontScale,
    isLandscape = when (orientation) {
        ScreenConfiguration.ORIENTATION_LANDSCAPE -> true
        ScreenConfiguration.ORIENTATION_PORTRAIT -> false
        else -> screenWidthDp > screenHeightDp
    },
    isMultiWindow = isInMultiWindowMode,
    device = platformDeviceClass(uiMode, smallestWidthDp),
)

/**
 * EN Resolves for a core snapshot; memoized per [DimenMetrics] (identity, then equality).
 * PT Resolve para um snapshot do core; memoizado por [DimenMetrics].
 */
fun ScalingStrategy.resolve(metrics: DimenMetrics): ResolvedScaling = resolve(metrics) { metrics.toScreenSnapshot() }

/**
 * EN `DimenCache` key for this strategy. Factor-only strategies are flagged non-cacheable
 * (resolved with one multiply); value-dependent ones are stored per window snapshot.
 * PT Chave do `DimenCache` para esta estratégia.
 */
fun ScalingStrategy.cacheKey(base: Float, valueType: DimenCache.ValueType): Long =
    DimenCache.buildCustomKey(
        baseValue = base,
        strategyKey = if (cacheId >= 0) cacheId else 0,
        isLandscape = false,          // the cache partition is already per window snapshot
        ignoreMultiWindows = false,
        valueType = valueType,
        cacheable = isValueDependent && cacheId >= 0,
    )

private val METRICS_PROBE_KEY: Long = DimenCache.buildCustomKey(
    baseValue = 0f, strategyKey = 0, isLandscape = false, ignoreMultiWindows = false,
    valueType = DimenCache.ValueType.DP, cacheable = false,
)

/**
 * EN Coherent window snapshot for [context] (same one the cache partitions by).
 * PT Snapshot coerente da janela de [context] (o mesmo usado pelo cache).
 */
fun DimenCache.metricsOf(context: AppDimensContext?): DimenMetrics {
    var out: DimenMetrics? = null
    getOrPut(METRICS_PROBE_KEY, context) {
        out = currentMetrics
        0f
    }
    return out ?: context?.toMetrics() ?: DimenMetrics.DEFAULT
}
