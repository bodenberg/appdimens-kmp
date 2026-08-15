/**
 * EN macOS actual: the chaintech artifact publishes NO macOS target, so this
 *    replicates Lib #2's exact scaling math: `min(screenWidthDp,
 *    screenHeightDp) / 300 × value` (its SDPConfig.getScalingRatio() = 300).
 *    LocalConfiguration is Android-only, so the window dimensions come from
 *    the AppDimens window handle — same fields, same formula, same operation
 *    shape. Keeps the benchlab compiling on macOS.
 * PT Actual macOS: o artefato chaintech NÃO publica target macOS, então isto
 *    replica a matemática exata da Lib #2: `min(screenWidthDp,
 *    screenHeightDp) / 300 × valor` (SDPConfig.getScalingRatio() = 300).
 *    LocalConfiguration é Android-only, então as dimensões vêm do handle de
 *    janela da AppDimens — mesmos campos, mesma fórmula, mesmo formato de
 *    operação. Mantém o benchlab compilando no macOS.
 */
package com.example.benchlab.benchmark

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.appdimens.dynamic.core.localAppDimensContext

@Composable
internal actual fun lib2SdpDp(value: Int): Dp {
    val config = localAppDimensContext()?.configuration
    val minDimension = if (config != null) {
        minOf(config.screenWidthDp, config.screenHeightDp)
    } else {
        0
    }
    return (value * (minDimension / 300f)).dp
}
