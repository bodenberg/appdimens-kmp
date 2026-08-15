/**
 * EN Android actual: delegates to the real Lib #2 extension.
 * PT Actual Android: delega à extensão real da Lib #2.
 */
package com.example.benchlab.benchmark

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import network.chaintech.sdpcomposemultiplatform.sdp

@Composable
internal actual fun lib2SdpDp(value: Int): Dp = value.sdp
