@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

/**
 * EN View **Plain** helpers (`Float` px + [AppDimensContext]): same **logic-only** branching as Compose `Dp`/`TextUnit` Plain pairs
 * in `*DpExtensions` / `*SpExtensions` (no extra scaling of receiver or alternate).
 * PT Helpers **Plain** para Views (`Float` px + [AppDimensContext]): mesma ramificação **só lógica** que os pares Plain `Dp`/`TextUnit`
 * em `*DpExtensions` / `*SpExtensions` (logsem nova escala do recetor nem do alternativo).
 *
 * EN Shared implementation: `com.appdimens.kmp.code.plain` (`DimenPlainBranch.kt`): `plainRotatePx`, `plainModePx`, `plainQualifierPx`, `plainScreenPx`.
 * PT Implementação partilhada: `com.appdimens.kmp.code.plain` (`DimenPlainBranch.kt`): `plainRotatePx`, `plainModePx`, `plainQualifierPx`, `plainScreenPx`.
 */
package com.appdimens.kmp.code.logarithmic

import com.appdimens.kmp.core.AppDimensContext
import com.appdimens.kmp.core.ScreenConfiguration
import com.appdimens.kmp.core.currentScreenConfiguration
import com.appdimens.kmp.core.localAppDimensContext

import com.appdimens.kmp.common.DpQualifier
import com.appdimens.kmp.common.Orientation
import com.appdimens.kmp.common.UiModeType
import com.appdimens.kmp.code.plain.plainModePx
import com.appdimens.kmp.code.plain.plainQualifierPx
import com.appdimens.kmp.code.plain.plainRotatePx
import com.appdimens.kmp.code.plain.plainScreenPx

fun Float.logsdpRotatePlainPx(context: AppDimensContext, rotationPx: Float, orientation: Orientation = Orientation.LANDSCAPE): Float =
    plainRotatePx(context, this, rotationPx, orientation)

fun Float.logsdpModePlainPx(context: AppDimensContext, modePx: Float, uiModeType: UiModeType): Float =
    plainModePx(context, this, modePx, uiModeType)

fun Float.logsdpQualifierPlainPx(context: AppDimensContext, qualifiedPx: Float, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainQualifierPx(context, this, qualifiedPx, qualifierType, qualifierValue)

fun Float.logsdpScreenPlainPx(context: AppDimensContext, screenPx: Float, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainScreenPx(context, this, screenPx, uiModeType, qualifierType, qualifierValue)

fun Float.loghdpRotatePlainPx(context: AppDimensContext, rotationPx: Float, orientation: Orientation = Orientation.LANDSCAPE): Float =
    plainRotatePx(context, this, rotationPx, orientation)

fun Float.loghdpModePlainPx(context: AppDimensContext, modePx: Float, uiModeType: UiModeType): Float =
    plainModePx(context, this, modePx, uiModeType)

fun Float.loghdpQualifierPlainPx(context: AppDimensContext, qualifiedPx: Float, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainQualifierPx(context, this, qualifiedPx, qualifierType, qualifierValue)

fun Float.loghdpScreenPlainPx(context: AppDimensContext, screenPx: Float, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainScreenPx(context, this, screenPx, uiModeType, qualifierType, qualifierValue)

fun Float.logwdpRotatePlainPx(context: AppDimensContext, rotationPx: Float, orientation: Orientation = Orientation.LANDSCAPE): Float =
    plainRotatePx(context, this, rotationPx, orientation)

fun Float.logwdpModePlainPx(context: AppDimensContext, modePx: Float, uiModeType: UiModeType): Float =
    plainModePx(context, this, modePx, uiModeType)

fun Float.logwdpQualifierPlainPx(context: AppDimensContext, qualifiedPx: Float, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainQualifierPx(context, this, qualifiedPx, qualifierType, qualifierValue)

fun Float.logwdpScreenPlainPx(context: AppDimensContext, screenPx: Float, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainScreenPx(context, this, screenPx, uiModeType, qualifierType, qualifierValue)

fun Float.logsspRotatePlainPx(context: AppDimensContext, rotationPx: Float, orientation: Orientation = Orientation.LANDSCAPE): Float =
    plainRotatePx(context, this, rotationPx, orientation)

fun Float.logsspModePlainPx(context: AppDimensContext, modePx: Float, uiModeType: UiModeType): Float =
    plainModePx(context, this, modePx, uiModeType)

fun Float.logsspQualifierPlainPx(context: AppDimensContext, qualifiedPx: Float, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainQualifierPx(context, this, qualifiedPx, qualifierType, qualifierValue)

fun Float.logsspScreenPlainPx(context: AppDimensContext, screenPx: Float, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainScreenPx(context, this, screenPx, uiModeType, qualifierType, qualifierValue)

fun Float.loghspRotatePlainPx(context: AppDimensContext, rotationPx: Float, orientation: Orientation = Orientation.LANDSCAPE): Float =
    plainRotatePx(context, this, rotationPx, orientation)

fun Float.loghspModePlainPx(context: AppDimensContext, modePx: Float, uiModeType: UiModeType): Float =
    plainModePx(context, this, modePx, uiModeType)

fun Float.loghspQualifierPlainPx(context: AppDimensContext, qualifiedPx: Float, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainQualifierPx(context, this, qualifiedPx, qualifierType, qualifierValue)

fun Float.loghspScreenPlainPx(context: AppDimensContext, screenPx: Float, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainScreenPx(context, this, screenPx, uiModeType, qualifierType, qualifierValue)

fun Float.logwspRotatePlainPx(context: AppDimensContext, rotationPx: Float, orientation: Orientation = Orientation.LANDSCAPE): Float =
    plainRotatePx(context, this, rotationPx, orientation)

fun Float.logwspModePlainPx(context: AppDimensContext, modePx: Float, uiModeType: UiModeType): Float =
    plainModePx(context, this, modePx, uiModeType)

fun Float.logwspQualifierPlainPx(context: AppDimensContext, qualifiedPx: Float, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainQualifierPx(context, this, qualifiedPx, qualifierType, qualifierValue)

fun Float.logwspScreenPlainPx(context: AppDimensContext, screenPx: Float, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainScreenPx(context, this, screenPx, uiModeType, qualifierType, qualifierValue)
