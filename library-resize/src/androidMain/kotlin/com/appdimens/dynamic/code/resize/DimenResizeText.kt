/**
 * Author & Developer: Jean Bodenberg
 * GIT: https://github.com/bodenberg/appdimens.git
 *
 * Library: AppDimens (KMP)
 *
 * Description:
 * Android-only text fitting API for DimenResize: uses android.text.TextPaint,
 * StaticLayout and TextUtils for non-Compose (View) text measurement.
 * Kept in androidMain because these APIs do not exist on other targets.
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
package com.appdimens.dynamic.code.resize

import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils

import com.appdimens.dynamic.core.AppDimensContext
import com.appdimens.dynamic.core.AutoResizePercentBasis
import com.appdimens.dynamic.core.ResizeBound
import com.appdimens.dynamic.core.ResizeRangePx
import com.appdimens.dynamic.core.resizeFixedSp

import kotlin.math.roundToInt

/**
 * EN Android-only text fitting: same API as the original `DimenResize` text methods.
 * PT API de texto somente Android: mesma API dos métodos de texto originais de `DimenResize`.
 */
object DimenResizeText {

    /**
     * EN Largest font size (px) in [range] so [text] fits in the inner box (same idea as [autoResizeTextSp]).
     * PT Maior tamanho de fonte em px para o texto caber na área útil.
     *
     * EN Configure [textPaint] (typeface, flags, letterSpacing, etc.) like the target [android.widget.TextView].
     * PT Configure [textPaint] como no [android.widget.TextView] de destino.
     */
    fun fittingTextSpPx(
        text: String,
        range: ResizeRangePx,
        innerWidthPx: Float,
        innerHeightPx: Float,
        textPaint: TextPaint,
        maxLines: Int? = null,
        maxLength: Int? = null,
        softWrap: Boolean = true,
        alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
        includePad: Boolean = true,
    ): Float {
        requireFiniteBox(innerWidthPx, innerHeightPx) { "fittingTextSpPx(inner)" }
        val measureText = resolveAutoResizeTextForMeasure(text, maxLength)
        val effMaxLines = resolveAutoResizeMaxLines(maxLines)
        val maxW = innerWidthPx.roundToInt().coerceAtLeast(1)
        val maxH = innerHeightPx.roundToInt().coerceAtLeast(1)
        return range.resolveFitting { candidatePx ->
            textFitsInnerBox(
                text = measureText,
                basePaint = textPaint,
                textSizePx = candidatePx,
                maxWidthPx = maxW,
                maxHeightPx = maxH,
                maxLines = effMaxLines,
                softWrap = softWrap,
                alignment = alignment,
                includePad = includePad,
            )
        }
    }

    /**
     * EN Twin of [autoResizeTextSp] with min/max/step as **sp** ([Number] overload in Compose).
     * PT Equivalente a [autoResizeTextSp] com min/max/step em **sp**.
     */
    fun fittingTextSpPx(
        context: AppDimensContext,
        text: String,
        minSp: Float,
        maxSp: Float,
        stepSp: Float,
        innerWidthPx: Float,
        innerHeightPx: Float,
        textPaint: TextPaint,
        maxLines: Int? = null,
        maxLength: Int? = null,
        softWrap: Boolean = true,
        alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
        includePad: Boolean = true,
    ): Float {
        val range = DimenResize.rangePx(
            context,
            resizeFixedSp(minSp),
            resizeFixedSp(maxSp),
            resizeFixedSp(stepSp),
        )
        return fittingTextSpPx(
            text = text,
            range = range,
            innerWidthPx = innerWidthPx,
            innerHeightPx = innerHeightPx,
            textPaint = textPaint,
            maxLines = maxLines,
            maxLength = maxLength,
            softWrap = softWrap,
            alignment = alignment,
            includePad = includePad,
        )
    }

    /**
     * EN Same as [fittingTextSpPx] but builds [range] from [ResizeBound]s via [DimenResize.rangePx].
     * PT Idem, construindo o intervalo com [DimenResize.rangePx].
     */
    fun fittingTextSpPx(
        context: AppDimensContext,
        text: String,
        min: ResizeBound,
        max: ResizeBound,
        innerWidthPx: Float,
        innerHeightPx: Float,
        textPaint: TextPaint,
        step: ResizeBound = resizeFixedSp(1f),
        maxLines: Int? = null,
        maxLength: Int? = null,
        softWrap: Boolean = true,
        alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
        includePad: Boolean = true,
    ): Float {
        val range = DimenResize.rangePx(context, min, max, step)
        return fittingTextSpPx(
            text = text,
            range = range,
            innerWidthPx = innerWidthPx,
            innerHeightPx = innerHeightPx,
            textPaint = textPaint,
            maxLines = maxLines,
            maxLength = maxLength,
            softWrap = softWrap,
            alignment = alignment,
            includePad = includePad,
        )
    }

    /**
     * EN Percent-of-inner-box text range + [fittingTextSpPx] (Compose [autoResizeTextSpPercent] twin).
     * PT Intervalo % da caixa + fitting — equivalente ao [autoResizeTextSpPercent].
     */
    fun fittingTextSpPercentPx(
        context: AppDimensContext,
        text: String,
        basis: AutoResizePercentBasis,
        minPercent: Number,
        maxPercent: Number,
        stepSp: Float,
        innerWidthPx: Float,
        innerHeightPx: Float,
        textPaint: TextPaint,
        maxLines: Int? = null,
        maxLength: Int? = null,
        softWrap: Boolean = true,
        alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
        includePad: Boolean = true,
    ): Float {
        val range = DimenResize.rangePxTextSizePercentOfInnerBox(
            context = context,
            basis = basis,
            minPercent = minPercent,
            maxPercent = maxPercent,
            stepSp = stepSp,
            innerWidthPx = innerWidthPx,
            innerHeightPx = innerHeightPx,
        )
        return fittingTextSpPx(
            text = text,
            range = range,
            innerWidthPx = innerWidthPx,
            innerHeightPx = innerHeightPx,
            textPaint = textPaint,
            maxLines = maxLines,
            maxLength = maxLength,
            softWrap = softWrap,
            alignment = alignment,
            includePad = includePad,
        )
    }
}

private fun textFitsInnerBox(
    text: String,
    basePaint: TextPaint,
    textSizePx: Float,
    maxWidthPx: Int,
    maxHeightPx: Int,
    maxLines: Int,
    softWrap: Boolean,
    alignment: Layout.Alignment,
    includePad: Boolean,
): Boolean {
    if (!textSizePx.isFinite() || textSizePx <= 0f) return false
    val p = TextPaint(basePaint)
    p.textSize = textSizePx
    if (!softWrap) {
        val w = p.measureText(text)
        val fm = p.fontMetrics
        val h = if (fm != null) {
            fm.descent - fm.ascent
        } else {
            // JVM unit tests (Android stubs) may return null from getFontMetrics().
            // Use line height ≈ text size so a square box test (e.g. maxHeight == max font px) still fits.
            textSizePx
        }
        return w <= maxWidthPx + 0.5f && h <= maxHeightPx + 0.5f
    }
    val builder = StaticLayout.Builder.obtain(text, 0, text.length, p, maxWidthPx)
        .setAlignment(alignment)
        .setIncludePad(includePad)
    if (maxLines != Int.MAX_VALUE) {
        builder.setMaxLines(maxLines)
        builder.setEllipsize(TextUtils.TruncateAt.END)
    }
    val layout = builder.build()
    if (layout.height > maxHeightPx + 0.5f) return false
    if (maxLines != Int.MAX_VALUE) {
        if (layout.lineCount > maxLines) return false
        val last = layout.lineCount - 1
        if (last >= 0 && layout.getEllipsisCount(last) > 0) return false
    }
    return true
}

private fun requireFiniteBox(vararg values: Float, name: () -> String) {
    for (v in values) {
        require(v.isFinite()) { "${name()}: expected finite value, was $v" }
    }
}