@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

/**
 * EN View **Plain** helpers (`Float` px + [AppDimensContext]): same **logic-only** branching as Compose `Dp`/`TextUnit` Plain pairs
 * in `*DpExtensions` / `*SpExtensions` (no extra scaling of receiver or alternate).
 * PT Helpers **Plain** para Views (`Float` px + [AppDimensContext]): mesma ramificação **só lógica** que os pares Plain `Dp`/`TextUnit`
 * em `*DpExtensions` / `*SpExtensions` (sem nova escala do recetor nem do alternativo).
 *
 * EN Shared implementation: `com.appdimens.kmp.code.plain` (`DimenPlainBranch.kt`): `plainRotatePx`, `plainModePx`, `plainQualifierPx`, `plainScreenPx`.
 * PT Implementação partilhada: `com.appdimens.kmp.code.plain` (`DimenPlainBranch.kt`): `plainRotatePx`, `plainModePx`, `plainQualifierPx`, `plainScreenPx`.
 */
package com.appdimens.kmp.code.fluid

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

fun Float.fsdpRotatePlainPx(context: AppDimensContext, rotationPx: Float, orientation: Orientation = Orientation.LANDSCAPE): Float =
    plainRotatePx(context, this, rotationPx, orientation)

fun Float.fsdpModePlainPx(context: AppDimensContext, modePx: Float, uiModeType: UiModeType): Float =
    plainModePx(context, this, modePx, uiModeType)

fun Float.fsdpQualifierPlainPx(context: AppDimensContext, qualifiedPx: Float, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainQualifierPx(context, this, qualifiedPx, qualifierType, qualifierValue)

fun Float.fsdpScreenPlainPx(context: AppDimensContext, screenPx: Float, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainScreenPx(context, this, screenPx, uiModeType, qualifierType, qualifierValue)

fun Float.fhdpRotatePlainPx(context: AppDimensContext, rotationPx: Float, orientation: Orientation = Orientation.LANDSCAPE): Float =
    plainRotatePx(context, this, rotationPx, orientation)

fun Float.fhdpModePlainPx(context: AppDimensContext, modePx: Float, uiModeType: UiModeType): Float =
    plainModePx(context, this, modePx, uiModeType)

fun Float.fhdpQualifierPlainPx(context: AppDimensContext, qualifiedPx: Float, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainQualifierPx(context, this, qualifiedPx, qualifierType, qualifierValue)

fun Float.fhdpScreenPlainPx(context: AppDimensContext, screenPx: Float, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainScreenPx(context, this, screenPx, uiModeType, qualifierType, qualifierValue)

fun Float.fwdpRotatePlainPx(context: AppDimensContext, rotationPx: Float, orientation: Orientation = Orientation.LANDSCAPE): Float =
    plainRotatePx(context, this, rotationPx, orientation)

fun Float.fwdpModePlainPx(context: AppDimensContext, modePx: Float, uiModeType: UiModeType): Float =
    plainModePx(context, this, modePx, uiModeType)

fun Float.fwdpQualifierPlainPx(context: AppDimensContext, qualifiedPx: Float, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainQualifierPx(context, this, qualifiedPx, qualifierType, qualifierValue)

fun Float.fwdpScreenPlainPx(context: AppDimensContext, screenPx: Float, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainScreenPx(context, this, screenPx, uiModeType, qualifierType, qualifierValue)

fun Float.fsspRotatePlainPx(context: AppDimensContext, rotationPx: Float, orientation: Orientation = Orientation.LANDSCAPE): Float =
    plainRotatePx(context, this, rotationPx, orientation)

fun Float.fsspModePlainPx(context: AppDimensContext, modePx: Float, uiModeType: UiModeType): Float =
    plainModePx(context, this, modePx, uiModeType)

fun Float.fsspQualifierPlainPx(context: AppDimensContext, qualifiedPx: Float, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainQualifierPx(context, this, qualifiedPx, qualifierType, qualifierValue)

fun Float.fsspScreenPlainPx(context: AppDimensContext, screenPx: Float, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainScreenPx(context, this, screenPx, uiModeType, qualifierType, qualifierValue)

fun Float.fhspRotatePlainPx(context: AppDimensContext, rotationPx: Float, orientation: Orientation = Orientation.LANDSCAPE): Float =
    plainRotatePx(context, this, rotationPx, orientation)

fun Float.fhspModePlainPx(context: AppDimensContext, modePx: Float, uiModeType: UiModeType): Float =
    plainModePx(context, this, modePx, uiModeType)

fun Float.fhspQualifierPlainPx(context: AppDimensContext, qualifiedPx: Float, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainQualifierPx(context, this, qualifiedPx, qualifierType, qualifierValue)

fun Float.fhspScreenPlainPx(context: AppDimensContext, screenPx: Float, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainScreenPx(context, this, screenPx, uiModeType, qualifierType, qualifierValue)

fun Float.fwspRotatePlainPx(context: AppDimensContext, rotationPx: Float, orientation: Orientation = Orientation.LANDSCAPE): Float =
    plainRotatePx(context, this, rotationPx, orientation)

fun Float.fwspModePlainPx(context: AppDimensContext, modePx: Float, uiModeType: UiModeType): Float =
    plainModePx(context, this, modePx, uiModeType)

fun Float.fwspQualifierPlainPx(context: AppDimensContext, qualifiedPx: Float, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainQualifierPx(context, this, qualifiedPx, qualifierType, qualifierValue)

fun Float.fwspScreenPlainPx(context: AppDimensContext, screenPx: Float, uiModeType: UiModeType, qualifierType: DpQualifier, qualifierValue: Number): Float =
    plainScreenPx(context, this, screenPx, uiModeType, qualifierType, qualifierValue)
