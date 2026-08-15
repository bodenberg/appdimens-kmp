@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

/**
 * EN Internal branching helpers for View-side **Plain** APIs: [plainRotatePx], [plainModePx],
 * [plainQualifierPx], [plainScreenPx]. Receiver and alternate values are already in **layout/text px**;
 * [AppDimensContext] is only used to read [res.ScreenConfiguration] and UI mode cache.
 *
 * PT Funções internas de ramificação para APIs **Plain** no lado View: [plainRotatePx], [plainModePx],
 * [plainQualifierPx], [plainScreenPx]. O recetor e o valor alternativo já estão em **px** de layout/texto;
 * o [AppDimensContext] serve só para ler [res.ScreenConfiguration] e a cache de modo de UI.
 *
 * EN Strategy-facing entry points: `Dimen*PlainPx.kt` files under each `com.appdimens.dynamic.code.<strategy>` package
 * (for example `DimenPercentPlainPx.kt` in `com.appdimens.dynamic.code.percent`).
 * PT Pontos de entrada por estratégia: ficheiros `Dimen*PlainPx.kt` em `com.appdimens.dynamic.code.<estratégia>`
 * (por exemplo `DimenPercentPlainPx.kt` em `com.appdimens.dynamic.code.percent`).
 */
package com.appdimens.dynamic.code.plain

import com.appdimens.dynamic.core.AppDimensContext
import com.appdimens.dynamic.core.ScreenConfiguration
import com.appdimens.dynamic.core.currentScreenConfiguration
import com.appdimens.dynamic.core.localAppDimensContext

import com.appdimens.dynamic.common.DpQualifier
import com.appdimens.dynamic.common.Orientation
import com.appdimens.dynamic.common.UiModeType
import com.appdimens.dynamic.core.DimenCache

fun isOrientationMatch(configuration: ScreenConfiguration, orientation: Orientation): Boolean =
    when (orientation) {
        Orientation.LANDSCAPE -> configuration.orientation == ScreenConfiguration.ORIENTATION_LANDSCAPE
        Orientation.PORTRAIT -> configuration.orientation == ScreenConfiguration.ORIENTATION_PORTRAIT
        else -> false
    }

fun qualifierMeetsThreshold(
    configuration: ScreenConfiguration,
    qualifierType: DpQualifier,
    qualifierValue: Number,
): Boolean {
    val v = when (qualifierType) {
        DpQualifier.SMALL_WIDTH -> configuration.smallestScreenWidthDp.toFloat()
        DpQualifier.HEIGHT -> configuration.screenHeightDp.toFloat()
        DpQualifier.WIDTH -> configuration.screenWidthDp.toFloat()
    }
    return v >= qualifierValue.toFloat()
}

fun plainRotatePx(
    context: AppDimensContext,
    receiverPx: Float,
    branchPx: Float,
    orientation: Orientation,
): Float {
    val c = context.configuration
    return if (isOrientationMatch(c, orientation)) branchPx else receiverPx
}

fun plainModePx(
    context: AppDimensContext,
    receiverPx: Float,
    branchPx: Float,
    uiModeType: UiModeType,
): Float =
    if (DimenCache.getCachedUiModeType(context) == uiModeType) branchPx else receiverPx

fun plainQualifierPx(
    context: AppDimensContext,
    receiverPx: Float,
    qualifiedPx: Float,
    qualifierType: DpQualifier,
    qualifierValue: Number,
): Float {
    val c = context.configuration
    return if (qualifierMeetsThreshold(c, qualifierType, qualifierValue)) qualifiedPx else receiverPx
}

fun plainScreenPx(
    context: AppDimensContext,
    receiverPx: Float,
    screenPx: Float,
    uiModeType: UiModeType,
    qualifierType: DpQualifier,
    qualifierValue: Number,
): Float {
    val c = context.configuration
    val uiOk = DimenCache.getCachedUiModeType(context) == uiModeType
    val qOk = qualifierMeetsThreshold(c, qualifierType, qualifierValue)
    return if (uiOk && qOk) screenPx else receiverPx
}
