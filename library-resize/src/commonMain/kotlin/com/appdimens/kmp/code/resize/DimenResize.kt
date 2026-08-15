@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

/**
 * EN View / AppDimensContext API: same building blocks as [com.appdimens.kmp.compose.resize] — inner box px,
 * % of inner box, [ResizeBound] ranges, and text fitting via [StaticLayout].
 * PT API baseada em AppDimensContext: equivalente ao Compose — caixa interna, % da caixa, [ResizeBound] e texto com [StaticLayout].
 */
package com.appdimens.kmp.code.resize

import com.appdimens.kmp.core.AppDimensContext
import com.appdimens.kmp.core.ScreenConfiguration
import com.appdimens.kmp.core.currentScreenConfiguration
import com.appdimens.kmp.core.localAppDimensContext

import com.appdimens.kmp.core.AutoResizePercentBasis
import com.appdimens.kmp.core.ResizeBound
import com.appdimens.kmp.core.ResizeRangePx
import com.appdimens.kmp.core.resizeFixedDp
import com.appdimens.kmp.core.resizeFixedSp
import com.appdimens.kmp.core.resolveToPx
import kotlin.math.min
import kotlin.math.roundToInt

fun interface ResizeFitPredicate {
    fun fits(candidatePx: Float): Boolean
}

private fun requireFiniteBox(vararg values: Float, name: () -> String) {
    for (v in values) {
        require(v.isFinite()) { "${name()}: expected finite value, was $v" }
    }
}

private fun ResizeRangePx.requireFiniteRange(): ResizeRangePx = also {
    require(minPx.isFinite() && maxPx.isFinite() && stepPx.isFinite()) {
        "resize range must be finite (minPx=$minPx, maxPx=$maxPx, stepPx=$stepPx)"
    }
}

/** EN `null`, `≤ 0`, or `-1` → unlimited lines. PT `null`, `≤ 0` ou `-1` → linhas ilimitadas. */
fun resolveAutoResizeMaxLines(maxLines: Int?): Int =
    if (maxLines == null || maxLines <= 0 || maxLines == -1) Int.MAX_VALUE else maxLines

/** EN `null`, `≤ 0`, or `-1` → full [text]; else first [maxLength] UTF-16 code units. */
fun resolveAutoResizeTextForMeasure(text: String, maxLength: Int?): String =
    when {
        maxLength == null || maxLength <= 0 || maxLength == -1 -> text
        else -> text.take(maxLength)
    }

/** EN [Number] as percent 0–100 → multiplier 0..1 (same as Compose). PT Percentagem 0–100 → fator 0..1. */
fun percentOfBoxToFactor(percent: Number): Float =
    (percent.toFloat() / 100f).coerceIn(0f, 1f)

object DimenResize {

    /**
     * EN Inner width × height (px) after subtracting padding; each dimension is at least **1** px (matches Compose).
     * PT Largura e altura úteis após padding; cada valor ≥ **1** px.
     */
    fun innerMaxDimensionsPx(
        boxWidthPx: Float,
        boxHeightPx: Float,
        paddingLeftPx: Float = 0f,
        paddingRightPx: Float = 0f,
        paddingTopPx: Float = 0f,
        paddingBottomPx: Float = 0f,
    ): Pair<Float, Float> {
        requireFiniteBox(boxWidthPx, boxHeightPx, paddingLeftPx, paddingRightPx, paddingTopPx, paddingBottomPx) {
            "innerMaxDimensionsPx"
        }
        val innerW = (boxWidthPx - paddingLeftPx - paddingRightPx).coerceAtLeast(1f)
        val innerH = (boxHeightPx - paddingTopPx - paddingBottomPx).coerceAtLeast(1f)
        return innerW to innerH
    }

    /**
     * EN Same as [innerMaxDimensionsPx] but horizontal padding uses **start** / **end** (mirrors Compose [PaddingValues] + RTL).
     * PT Igual a [innerMaxDimensionsPx] com padding horizontal **start** / **end** (RTL como no Compose).
     */
    fun innerMaxDimensionsPxRelative(
        boxWidthPx: Float,
        boxHeightPx: Float,
        paddingStartPx: Float = 0f,
        paddingEndPx: Float = 0f,
        paddingTopPx: Float = 0f,
        paddingBottomPx: Float = 0f,
        layoutDirection: Int = 0,
    ): Pair<Float, Float> {
        val padLeft: Float
        val padRight: Float
        if (layoutDirection == 1) {
            padLeft = paddingEndPx
            padRight = paddingStartPx
        } else {
            padLeft = paddingStartPx
            padRight = paddingEndPx
        }
        return innerMaxDimensionsPx(
            boxWidthPx, boxHeightPx,
            padLeft, padRight, paddingTopPx, paddingBottomPx,
        )
    }

    /**
     * EN Uniform padding in **dp** on all sides (Compose [contentPaddingUniformDp] for square / symmetric inset).
     * PT Padding uniforme em **dp** (equivalente a [contentPaddingUniformDp] quando só há inset simétrico).
     */
    fun innerMaxDimensionsPxUniformDp(
        context: AppDimensContext,
        boxWidthPx: Float,
        boxHeightPx: Float,
        uniformPaddingDp: Float,
    ): Pair<Float, Float> {
        require(uniformPaddingDp.isFinite()) { "uniformPaddingDp must be finite, was $uniformPaddingDp" }
        if (uniformPaddingDp <= 0f) {
            return innerMaxDimensionsPx(boxWidthPx, boxHeightPx)
        }
        val d = context.density
        require(d > 0f) { "density must be positive" }
        val p = uniformPaddingDp * d
        return innerMaxDimensionsPx(boxWidthPx, boxHeightPx, p, p, p, p)
    }

    /**
     * EN [ResizeRangePx] from [ResizeBound]s (screen %, fixed dp/sp) — same as Compose [rememberResizeRangePx].
     * PT Intervalo em px a partir de [ResizeBound] (equivalente ao Compose).
     */
    fun rangePx(context: AppDimensContext, min: ResizeBound, max: ResizeBound, step: ResizeBound): ResizeRangePx {
        val res = context
        val cfg = res.configuration
        val d = res.density
        val fs = cfg.fontScale.coerceAtLeast(1e-6f)
        return ResizeRangePx(
            minPx = min.resolveToPx(cfg, d, fs),
            maxPx = max.resolveToPx(cfg, d, fs),
            stepPx = step.resolveToPx(cfg, d, fs),
        ).requireFiniteRange()
    }

    /**
     * EN [minPercent]/[maxPercent] are **0–100** of the chosen inner edge ([basis]); [stepDp] is logical dp → px via density.
     * PT % 0–100 da aresta interna; passo em **dp**.
     */
    fun rangePxPercentOfInnerBox(
        context: AppDimensContext,
        basis: AutoResizePercentBasis,
        minPercent: Number,
        maxPercent: Number,
        stepDp: Float,
        innerWidthPx: Float,
        innerHeightPx: Float,
    ): ResizeRangePx {
        requireFiniteBox(innerWidthPx, innerHeightPx) { "rangePxPercentOfInnerBox(inner)" }
        require(stepDp.isFinite()) { "stepDp must be finite, was $stepDp" }
        val base = when (basis) {
            AutoResizePercentBasis.HEIGHT -> innerHeightPx
            AutoResizePercentBasis.WIDTH -> innerWidthPx
            AutoResizePercentBasis.MIN_SIDE -> min(innerWidthPx, innerHeightPx)
        }
        val minPx = base * percentOfBoxToFactor(minPercent)
        val maxPx = base * percentOfBoxToFactor(maxPercent)
        val d = context.density
        require(d > 0f) { "density must be positive" }
        val stepPx = stepDp.coerceAtLeast(0f) * d
        return ResizeRangePx(minPx, maxPx, stepPx).requireFiniteRange()
    }

    /**
     * EN Font-size range: min/max in px from **0–100** % of inner edge ([basis]); step from **sp** (density + font scale).
     * PT Tamanho de texto: min/max em px por % da aresta interna; passo em **sp**.
     */
    fun rangePxTextSizePercentOfInnerBox(
        context: AppDimensContext,
        basis: AutoResizePercentBasis,
        minPercent: Number,
        maxPercent: Number,
        stepSp: Float,
        innerWidthPx: Float,
        innerHeightPx: Float,
    ): ResizeRangePx {
        requireFiniteBox(innerWidthPx, innerHeightPx) { "rangePxTextSizePercentOfInnerBox(inner)" }
        require(stepSp.isFinite()) { "stepSp must be finite, was $stepSp" }
        val res = context
        val cfg = res.configuration
        val d = res.density
        val fs = cfg.fontScale.coerceAtLeast(1e-6f)
        val base = when (basis) {
            AutoResizePercentBasis.HEIGHT -> innerHeightPx
            AutoResizePercentBasis.WIDTH -> innerWidthPx
            AutoResizePercentBasis.MIN_SIDE -> min(innerWidthPx, innerHeightPx)
        }
        val minPx = base * percentOfBoxToFactor(minPercent)
        val maxPx = base * percentOfBoxToFactor(maxPercent)
        val stepPx = resizeFixedSp(stepSp).resolveToPx(cfg, d, fs)
        return ResizeRangePx(minPx, maxPx, stepPx).requireFiniteRange()
    }

    /** EN Twin of [autoResizeWidthSizePercent]: % of **inner width**. PT % da **largura útil**. */
    fun rangePxPercentOfInnerWidth(
        context: AppDimensContext,
        minPercent: Number,
        maxPercent: Number,
        stepDp: Float,
        innerWidthPx: Float,
        innerHeightPx: Float,
    ): ResizeRangePx = rangePxPercentOfInnerBox(
        context,
        AutoResizePercentBasis.WIDTH,
        minPercent,
        maxPercent,
        stepDp,
        innerWidthPx,
        innerHeightPx,
    )

    /** EN Twin of [autoResizeHeightSizePercent]: % of **inner height**. PT % da **altura útil**. */
    fun rangePxPercentOfInnerHeight(
        context: AppDimensContext,
        minPercent: Number,
        maxPercent: Number,
        stepDp: Float,
        innerWidthPx: Float,
        innerHeightPx: Float,
    ): ResizeRangePx = rangePxPercentOfInnerBox(
        context,
        AutoResizePercentBasis.HEIGHT,
        minPercent,
        maxPercent,
        stepDp,
        innerWidthPx,
        innerHeightPx,
    )

    /** EN Twin of [autoResizeSquareSizePercent]: % of `min(inner width, inner height)`. PT % do **menor lado** útil. */
    fun rangePxPercentOfInnerMinSide(
        context: AppDimensContext,
        minPercent: Number,
        maxPercent: Number,
        stepDp: Float,
        innerWidthPx: Float,
        innerHeightPx: Float,
    ): ResizeRangePx = rangePxPercentOfInnerBox(
        context,
        AutoResizePercentBasis.MIN_SIDE,
        minPercent,
        maxPercent,
        stepDp,
        innerWidthPx,
        innerHeightPx,
    )

    fun fittingPx(range: ResizeRangePx, predicate: ResizeFitPredicate): Float =
        range.resolveFitting { predicate.fits(it) }

    /**
     * EN Largest width in [range] that fits **inner** width (after padding) of the box.
     * PT Maior largura no intervalo que cabe na largura útil.
     */
    fun fittingInnerWidthPx(
        range: ResizeRangePx,
        boxWidthPx: Float,
        boxHeightPx: Float,
        paddingLeftPx: Float = 0f,
        paddingRightPx: Float = 0f,
        paddingTopPx: Float = 0f,
        paddingBottomPx: Float = 0f,
    ): Float {
        val (innerW, _) = innerMaxDimensionsPx(
            boxWidthPx, boxHeightPx,
            paddingLeftPx, paddingRightPx, paddingTopPx, paddingBottomPx,
        )
        return fittingPx(range) { it <= innerW }
    }

    /**
     * EN Largest height in [range] that fits **inner** height.
     * PT Maior altura no intervalo que cabe na altura útil.
     */
    fun fittingInnerHeightPx(
        range: ResizeRangePx,
        boxWidthPx: Float,
        boxHeightPx: Float,
        paddingLeftPx: Float = 0f,
        paddingRightPx: Float = 0f,
        paddingTopPx: Float = 0f,
        paddingBottomPx: Float = 0f,
    ): Float {
        val (_, innerH) = innerMaxDimensionsPx(
            boxWidthPx, boxHeightPx,
            paddingLeftPx, paddingRightPx, paddingTopPx, paddingBottomPx,
        )
        return fittingPx(range) { it <= innerH }
    }

    /**
     * EN Largest size in [range] that fits `min(inner width, inner height)` (square slot).
     * PT Maior medida no intervalo que cabe no menor lado útil (quadrado).
     */
    fun fittingInnerSquareSidePx(
        range: ResizeRangePx,
        boxWidthPx: Float,
        boxHeightPx: Float,
        paddingLeftPx: Float = 0f,
        paddingRightPx: Float = 0f,
        paddingTopPx: Float = 0f,
        paddingBottomPx: Float = 0f,
    ): Float {
        val (innerW, innerH) = innerMaxDimensionsPx(
            boxWidthPx, boxHeightPx,
            paddingLeftPx, paddingRightPx, paddingTopPx, paddingBottomPx,
        )
        val limit = min(innerW, innerH)
        return fittingPx(range) { it <= limit }
    }

    /**
     * EN Fixed **dp** range (like [autoResizeWidthSize] Dp overload) + largest value that fits inner width.
     * PT Intervalo em **dp** + maior valor que cabe na largura útil.
     */
    fun fittingInnerWidthPx(
        context: AppDimensContext,
        minDp: Float,
        maxDp: Float,
        stepDp: Float,
        boxWidthPx: Float,
        boxHeightPx: Float,
        paddingLeftPx: Float = 0f,
        paddingRightPx: Float = 0f,
        paddingTopPx: Float = 0f,
        paddingBottomPx: Float = 0f,
    ): Float {
        val range = rangePx(
            context,
            resizeFixedDp(minDp),
            resizeFixedDp(maxDp),
            resizeFixedDp(stepDp),
        )
        return fittingInnerWidthPx(
            range, boxWidthPx, boxHeightPx,
            paddingLeftPx, paddingRightPx, paddingTopPx, paddingBottomPx,
        )
    }

    /**
     * EN Fixed **dp** range + largest value that fits inner height ([autoResizeHeightSize] twin).
     * PT Intervalo em **dp** + maior valor que cabe na altura útil.
     */
    fun fittingInnerHeightPx(
        context: AppDimensContext,
        minDp: Float,
        maxDp: Float,
        stepDp: Float,
        boxWidthPx: Float,
        boxHeightPx: Float,
        paddingLeftPx: Float = 0f,
        paddingRightPx: Float = 0f,
        paddingTopPx: Float = 0f,
        paddingBottomPx: Float = 0f,
    ): Float {
        val range = rangePx(
            context,
            resizeFixedDp(minDp),
            resizeFixedDp(maxDp),
            resizeFixedDp(stepDp),
        )
        return fittingInnerHeightPx(
            range, boxWidthPx, boxHeightPx,
            paddingLeftPx, paddingRightPx, paddingTopPx, paddingBottomPx,
        )
    }

    /**
     * EN Fixed **dp** range + largest value that fits `min(inner w, inner h)` ([autoResizeSquareSize] twin).
     * PT Intervalo em **dp** + maior valor no menor lado útil.
     */
    fun fittingInnerSquareSidePx(
        context: AppDimensContext,
        minDp: Float,
        maxDp: Float,
        stepDp: Float,
        boxWidthPx: Float,
        boxHeightPx: Float,
        paddingLeftPx: Float = 0f,
        paddingRightPx: Float = 0f,
        paddingTopPx: Float = 0f,
        paddingBottomPx: Float = 0f,
    ): Float {
        val range = rangePx(
            context,
            resizeFixedDp(minDp),
            resizeFixedDp(maxDp),
            resizeFixedDp(stepDp),
        )
        return fittingInnerSquareSidePx(
            range, boxWidthPx, boxHeightPx,
            paddingLeftPx, paddingRightPx, paddingTopPx, paddingBottomPx,
        )
    }

    /**
     * EN % of inner width + fit ([autoResizeWidthSizePercent] twin).
     * PT % da largura útil + fitting.
     */
    fun fittingInnerWidthPercentPx(
        context: AppDimensContext,
        minPercent: Number,
        maxPercent: Number,
        stepDp: Float,
        boxWidthPx: Float,
        boxHeightPx: Float,
        paddingLeftPx: Float = 0f,
        paddingRightPx: Float = 0f,
        paddingTopPx: Float = 0f,
        paddingBottomPx: Float = 0f,
    ): Float {
        val (innerW, innerH) = innerMaxDimensionsPx(
            boxWidthPx, boxHeightPx,
            paddingLeftPx, paddingRightPx, paddingTopPx, paddingBottomPx,
        )
        val range = rangePxPercentOfInnerWidth(
            context, minPercent, maxPercent, stepDp, innerW, innerH,
        )
        return fittingPx(range) { it <= innerW }
    }

    /**
     * EN % of inner height + fit ([autoResizeHeightSizePercent] twin).
     * PT % da altura útil + fitting.
     */
    fun fittingInnerHeightPercentPx(
        context: AppDimensContext,
        minPercent: Number,
        maxPercent: Number,
        stepDp: Float,
        boxWidthPx: Float,
        boxHeightPx: Float,
        paddingLeftPx: Float = 0f,
        paddingRightPx: Float = 0f,
        paddingTopPx: Float = 0f,
        paddingBottomPx: Float = 0f,
    ): Float {
        val (innerW, innerH) = innerMaxDimensionsPx(
            boxWidthPx, boxHeightPx,
            paddingLeftPx, paddingRightPx, paddingTopPx, paddingBottomPx,
        )
        val range = rangePxPercentOfInnerHeight(
            context, minPercent, maxPercent, stepDp, innerW, innerH,
        )
        return fittingPx(range) { it <= innerH }
    }

    /**
     * EN % of min(inner w, h) + fit square side ([autoResizeSquareSizePercent] twin).
     * PT % do menor lado + fitting de quadrado.
     */
    fun fittingInnerSquareSidePercentPx(
        context: AppDimensContext,
        minPercent: Number,
        maxPercent: Number,
        stepDp: Float,
        boxWidthPx: Float,
        boxHeightPx: Float,
        paddingLeftPx: Float = 0f,
        paddingRightPx: Float = 0f,
        paddingTopPx: Float = 0f,
        paddingBottomPx: Float = 0f,
    ): Float {
        val (innerW, innerH) = innerMaxDimensionsPx(
            boxWidthPx, boxHeightPx,
            paddingLeftPx, paddingRightPx, paddingTopPx, paddingBottomPx,
        )
        val limit = min(innerW, innerH)
        val range = rangePxPercentOfInnerMinSide(
            context, minPercent, maxPercent, stepDp, innerW, innerH,
        )
        return fittingPx(range) { it <= limit }
    }
}

fun ResizeRangePx.fittingPx(fits: (candidatePx: Float) -> Boolean): Float =
    resolveFitting(fits)
