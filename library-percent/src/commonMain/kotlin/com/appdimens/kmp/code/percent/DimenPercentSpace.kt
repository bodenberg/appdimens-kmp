@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

/**
 * EN Literal percent of screen width / smallest width / height, or of a reference length (dp).
 * PT Percentual literal da largura da tela, smallest width, altura ou de um comprimento de referência (dp).
 */
package com.appdimens.kmp.code.percent

import com.appdimens.kmp.core.AppDimensContext
import com.appdimens.kmp.core.ScreenConfiguration

import com.appdimens.kmp.common.DpQualifier
import com.appdimens.kmp.core.literalPercentOfReferenceDp
import com.appdimens.kmp.core.literalPercentOfScreenDp

// ─── Screen fraction → px (same idea as psdp / pwdp returning px) ─────────────

fun Number.spaceW(context: AppDimensContext, ignoreMultiWindows: Boolean = false): Float {
    val c = context.configuration
    val dp = literalPercentOfScreenDp(toFloat(), DpQualifier.WIDTH, c, ignoreMultiWindows)
    return dp * context.density
}

fun Number.spaceSw(context: AppDimensContext, ignoreMultiWindows: Boolean = false): Float {
    val c = context.configuration
    val dp = literalPercentOfScreenDp(toFloat(), DpQualifier.SMALL_WIDTH, c, ignoreMultiWindows)
    return dp * context.density
}

fun Number.spaceH(context: AppDimensContext, ignoreMultiWindows: Boolean = false): Float {
    val c = context.configuration
    val dp = literalPercentOfScreenDp(toFloat(), DpQualifier.HEIGHT, c, ignoreMultiWindows)
    return dp * context.density
}

/** PT Igual a [spaceW] com `ignoreMultiWindows = true` (split-screen / multi-janela). */
fun Number.spaceWi(context: AppDimensContext): Float = spaceW(context, ignoreMultiWindows = true)

/** PT Igual a [spaceSw] com `ignoreMultiWindows = true`. */
fun Number.spaceSwi(context: AppDimensContext): Float = spaceSw(context, ignoreMultiWindows = true)

/** PT Igual a [spaceH] com `ignoreMultiWindows = true`. */
fun Number.spaceHi(context: AppDimensContext): Float = spaceH(context, ignoreMultiWindows = true)

// ─── Mesmo valor em px com sufixo explícito (paridade com Compose: spaceWPx, psdpPx, etc.) ─

fun Number.spaceWPx(context: AppDimensContext, ignoreMultiWindows: Boolean = false): Float =
    spaceW(context, ignoreMultiWindows)

/** PT Igual a [spaceWPx] com `ignoreMultiWindows = true`. */
fun Number.spaceWPxi(context: AppDimensContext): Float = spaceWPx(context, ignoreMultiWindows = true)

fun Number.spaceSwPx(context: AppDimensContext, ignoreMultiWindows: Boolean = false): Float =
    spaceSw(context, ignoreMultiWindows)

/** PT Igual a [spaceSwPx] com `ignoreMultiWindows = true`. */
fun Number.spaceSwPxi(context: AppDimensContext): Float = spaceSwPx(context, ignoreMultiWindows = true)

fun Number.spaceHPx(context: AppDimensContext, ignoreMultiWindows: Boolean = false): Float =
    spaceH(context, ignoreMultiWindows)

/** PT Igual a [spaceHPx] com `ignoreMultiWindows = true`. */
fun Number.spaceHPxi(context: AppDimensContext): Float = spaceHPx(context, ignoreMultiWindows = true)

// ─── Screen fraction → dp (raw) ─────────────────────────────────────────────

fun Number.spaceWDp(context: AppDimensContext, ignoreMultiWindows: Boolean = false): Float =
    literalPercentOfScreenDp(toFloat(), DpQualifier.WIDTH, context.configuration, ignoreMultiWindows)

fun Number.spaceSwDp(context: AppDimensContext, ignoreMultiWindows: Boolean = false): Float =
    literalPercentOfScreenDp(toFloat(), DpQualifier.SMALL_WIDTH, context.configuration, ignoreMultiWindows)

fun Number.spaceHDp(context: AppDimensContext, ignoreMultiWindows: Boolean = false): Float =
    literalPercentOfScreenDp(toFloat(), DpQualifier.HEIGHT, context.configuration, ignoreMultiWindows)

/** PT Igual a [spaceWDp] com `ignoreMultiWindows = true`. */
fun Number.spaceWDpi(context: AppDimensContext): Float = spaceWDp(context, ignoreMultiWindows = true)

/** PT Igual a [spaceSwDp] com `ignoreMultiWindows = true`. */
fun Number.spaceSwDpi(context: AppDimensContext): Float = spaceSwDp(context, ignoreMultiWindows = true)

/** PT Igual a [spaceHDp] com `ignoreMultiWindows = true`. */
fun Number.spaceHDpi(context: AppDimensContext): Float = spaceHDp(context, ignoreMultiWindows = true)

// ─── Reference length (dp) ──────────────────────────────────────────────────

fun Number.space(referenceDp: Number, context: AppDimensContext, ignoreMultiWindows: Boolean = false): Float {
    val c = context.configuration
    val dp = literalPercentOfReferenceDp(toFloat(), referenceDp.toFloat(), c, ignoreMultiWindows)
    return dp * context.density
}

fun Number.spaceDp(referenceDp: Number, context: AppDimensContext, ignoreMultiWindows: Boolean = false): Float =
    literalPercentOfReferenceDp(toFloat(), referenceDp.toFloat(), context.configuration, ignoreMultiWindows)

/** PT Igual a [space] com `ignoreMultiWindows = true`. */
fun Number.spaceI(referenceDp: Number, context: AppDimensContext): Float =
    space(referenceDp, context, ignoreMultiWindows = true)

/** PT Igual a [spaceDp] com `ignoreMultiWindows = true`. */
fun Number.spaceDpi(referenceDp: Number, context: AppDimensContext): Float =
    spaceDp(referenceDp, context, ignoreMultiWindows = true)

fun Number.spacePx(referenceDp: Number, context: AppDimensContext, ignoreMultiWindows: Boolean = false): Float =
    space(referenceDp, context, ignoreMultiWindows)

/** PT Igual a [spacePx] com `ignoreMultiWindows = true`. */
fun Number.spacePxi(referenceDp: Number, context: AppDimensContext): Float =
    spacePx(referenceDp, context, ignoreMultiWindows = true)

// ─── Sp: numeric sp for View APIs; SpPx matches dp pixel size when fontScale respected ─

fun Number.spaceWSp(context: AppDimensContext, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false): Float {
    val resultDp = spaceWDp(context, ignoreMultiWindows)
    return literalPercentSpValue(context, resultDp, fontScale)
}

fun Number.spaceSwSp(context: AppDimensContext, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false): Float {
    val resultDp = spaceSwDp(context, ignoreMultiWindows)
    return literalPercentSpValue(context, resultDp, fontScale)
}

fun Number.spaceHSp(context: AppDimensContext, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false): Float {
    val resultDp = spaceHDp(context, ignoreMultiWindows)
    return literalPercentSpValue(context, resultDp, fontScale)
}

fun Number.spaceWSpPx(context: AppDimensContext, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false): Float {
    val res = context
    val sp = spaceWSp(context, fontScale, ignoreMultiWindows)
    val density = res.density
    val fs = res.configuration.fontScale.coerceAtLeast(1e-6f)
    return if (fontScale) sp * density * fs else sp * density
}

fun Number.spaceSwSpPx(context: AppDimensContext, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false): Float {
    val res = context
    val sp = spaceSwSp(context, fontScale, ignoreMultiWindows)
    val density = res.density
    val fs = res.configuration.fontScale.coerceAtLeast(1e-6f)
    return if (fontScale) sp * density * fs else sp * density
}

fun Number.spaceHSpPx(context: AppDimensContext, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false): Float {
    val res = context
    val sp = spaceHSp(context, fontScale, ignoreMultiWindows)
    val density = res.density
    val fs = res.configuration.fontScale.coerceAtLeast(1e-6f)
    return if (fontScale) sp * density * fs else sp * density
}

fun Number.spaceSp(referenceDp: Number, context: AppDimensContext, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false): Float {
    val resultDp = spaceDp(referenceDp, context, ignoreMultiWindows)
    return literalPercentSpValue(context, resultDp, fontScale)
}

fun Number.spaceSpPx(referenceDp: Number, context: AppDimensContext, fontScale: Boolean = true, ignoreMultiWindows: Boolean = false): Float {
    val res = context
    val sp = spaceSp(referenceDp, context, fontScale, ignoreMultiWindows)
    val density = res.density
    val fs = res.configuration.fontScale.coerceAtLeast(1e-6f)
    return if (fontScale) sp * density * fs else sp * density
}

fun Number.spaceWSpi(context: AppDimensContext, fontScale: Boolean = true): Float =
    spaceWSp(context, fontScale, ignoreMultiWindows = true)

fun Number.spaceSwSpi(context: AppDimensContext, fontScale: Boolean = true): Float =
    spaceSwSp(context, fontScale, ignoreMultiWindows = true)

fun Number.spaceHSpi(context: AppDimensContext, fontScale: Boolean = true): Float =
    spaceHSp(context, fontScale, ignoreMultiWindows = true)

fun Number.spaceWSpiPx(context: AppDimensContext, fontScale: Boolean = true): Float =
    spaceWSpPx(context, fontScale, ignoreMultiWindows = true)

fun Number.spaceSwSpiPx(context: AppDimensContext, fontScale: Boolean = true): Float =
    spaceSwSpPx(context, fontScale, ignoreMultiWindows = true)

fun Number.spaceHSpiPx(context: AppDimensContext, fontScale: Boolean = true): Float =
    spaceHSpPx(context, fontScale, ignoreMultiWindows = true)

fun Number.spaceSpi(referenceDp: Number, context: AppDimensContext, fontScale: Boolean = true): Float =
    spaceSp(referenceDp, context, fontScale, ignoreMultiWindows = true)

fun Number.spaceSpiPx(referenceDp: Number, context: AppDimensContext, fontScale: Boolean = true): Float =
    spaceSpPx(referenceDp, context, fontScale, ignoreMultiWindows = true)

/** Sp value for [android.util.TypedValue.COMPLEX_UNIT_SP] when [fontScale] is true; else dp-like value for COMPLEX_UNIT_DIP. */
private fun literalPercentSpValue(context: AppDimensContext, resultDp: Float, fontScale: Boolean): Float {
    if (!fontScale) return resultDp
    val fs = context.configuration.fontScale
    return if (fs > 0f) resultDp / fs else resultDp
}
