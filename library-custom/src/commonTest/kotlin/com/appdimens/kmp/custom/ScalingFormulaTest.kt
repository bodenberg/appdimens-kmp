package com.appdimens.kmp.custom

import kotlin.math.pow
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ScalingFormulaTest {

    private val phone = ScreenSnapshot(widthDp = 411f, heightDp = 891f, smallestWidthDp = 411f, density = 2.625f)
    private val tablet = ScreenSnapshot(
        widthDp = 1280f, heightDp = 800f, smallestWidthDp = 800f, density = 2f,
        isLandscape = true, device = DeviceClass.TABLET,
    )

    @AfterTest
    fun tearDown() = ScalingStrategies.resetForTest()

    @Test
    fun textFormula_isTheFinalValue() {
        val s = scalingFormula("base * pow(sw / 360, 0.8)")
        assertEquals((16.0 * (411.0 / 360.0).pow(0.8)).toFloat(), s.scale(16f, phone), 0f)
        assertEquals(16f + 2f, scalingFormula("base + 2").scale(16f, phone), 0f)
        assertEquals(16f * 411f / 300f, scalingFormula("base * ratio").scale(16f, phone), 1e-4f) // ratio = sw / 300, like sdp
        assertEquals(16f * 0.9f, scalingFormula("base * (tablet ? 0.9 : 1)").scale(16f, tablet), 1e-6f)
    }

    @Test
    fun proportionalFormula_staysOnTheFastPath_andMatchesTheSlowPathBitForBit() {
        for (src in listOf("base", "base * pow(sw / 360, 0.8)", "(sw / 360) * base", "base * (tablet ? 0.9 : 1.1)")) {
            val fast = scalingFormula(src)
            assertFalse(fast.isValueDependent, "$src should be screen-only")
            // Same text, but not proportional (`+ 0` forces the per-value path): identical numbers.
            val slow = scalingFormula("$src + 0")
            assertTrue(slow.isValueDependent)
            for (screen in listOf(phone, tablet)) for (base in listOf(1f, 16f, 48f, -8f, 0.5f)) {
                assertEquals(slow.scale(base, screen), fast.scale(base, screen), 0f)
            }
        }
        // Reads the value inside E → not proportional.
        assertTrue(scalingFormula("base * min(base, 20)").isValueDependent)
        assertTrue(scalingFormula("max(base * sw / 360, base)").isValueDependent)
        // `(base * sw) / 360` is evaluated as written (per value), so it is not on the fast path.
        assertTrue(scalingFormula("base * sw / 360").isValueDependent)
    }

    @Test
    fun proportionalFormula_insideTheDslAndJson_isScreenOnlyToo() {
        val dsl = scalingStrategy("p", register = false) { formula("base * (sw / 360)") }
        assertFalse(dsl.isValueDependent)
        val json = ScalingStrategies.loadJson("""{"strategies":[{"id":"j","formula":"base * 1.1"}]}""").getValue("j")
        assertFalse(json.isValueDependent)
        assertEquals(16f * 1.1f, json.scale(16f, phone), 1e-5f)
        assertTrue(scalingStrategy("v", register = false) { formula("round(scaled)") }.isValueDependent)
    }

    @Test
    fun textFormula_isSharedPerText_unlessIdIsGiven() {
        assertSame(scalingFormula("base * 2"), scalingFormula("base * 2"))
        assertSame(ScalingStrategies.formula("base * 2"), scalingFormula("base * 2"))
        assertEquals("formula:base * 2", scalingFormula("base * 2").id)
        val named = scalingFormula("base * 2", id = "double")
        assertNotSame(named, scalingFormula("base * 2"))
        assertSame(named, ScalingStrategies["double"]) // an id registers by default
    }

    @Test
    fun codeFormula_hasNoLimits() {
        val s = scalingFormula { base * pow(sw / 360f, 0.8f) + if (landscape) 2f else 0f }
        assertEquals(16f * (411f / 360f).pow(0.8f), s.scale(16f, phone), 1e-5f)
        assertEquals(16f * (800f / 360f).pow(0.8f) + 2f, s.scale(16f, tablet), 1e-4f)
        assertTrue(s.isValueDependent)
        assertTrue(s.id.startsWith("formula#"))
        assertTrue(scalingFormula { base }.id != s.id)
        val named = scalingFormula(id = "hud") { base * 3f }
        assertSame(named, ScalingStrategies["hud"])
    }

    @Test
    fun programmableFactor_isScreenOnly_andMultipliesEveryValue() {
        val product = CustomScaleStrategy { (width / 360f).coerceIn(0.85f, 1.80f) }
        assertFalse(product.isValueDependent)
        assertEquals(411f / 360f, product.factor(phone), 1e-6f)
        assertEquals(1.8f, product.factor(tablet), 0f)
        assertEquals(16f * 411f / 360f, product.scale(16f, phone), 1e-5f)
        assertTrue(product.id.startsWith("scale#"))
        val named = CustomScaleStrategy(id = "product") { 1.25f }
        assertSame(named, ScalingStrategies["product"])
        assertEquals(20f, named.scale(16f, phone), 0f)
        val e = assertFailsWith<IllegalArgumentException> { CustomScaleStrategy { Float.NaN }.factor(phone) }
        assertTrue(e.message!!.contains("non-finite"), e.message)
    }

    @Test
    fun codeScope_matchesTheTextLanguage() {
        val pairs = listOf(
            scalingFormula { base * pow(sw / 360f, 0.8f) } to "base * pow(sw / 360, 0.8)",
            scalingFormula { base * ratio } to "base * ratio",
            scalingFormula { base * (if (tablet) 0.9f else 1f) * clamp(width / 360f, 0.85f, 1.6f) } to
                "base * (tablet ? 0.9 : 1) * clamp(width / 360, 0.85, 1.6)",
            scalingFormula { round(base * lerp(1f, aspectRatio, 0.1f)) } to "round(base * lerp(1, aspectRatio, 0.1))",
        )
        for ((code, text) in pairs) for (screen in listOf(phone, tablet)) for (base in listOf(1f, 16f, 48f)) {
            assertEquals(scalingFormula(text).scale(base, screen), code.scale(base, screen), 1e-4f, text)
        }
        val tvScreen = ScreenSnapshot(960f, 540f, 540f, 2f, isLandscape = true, device = DeviceClass.TV)
        val factor = CustomScaleStrategy { if (tv) height / 540f else ratio }
        assertEquals(1f, factor.factor(tvScreen), 0f)
        assertEquals(411f / 300f, factor.factor(phone), 1e-6f)
    }

    @Test
    fun reviewFixes_rangeMustBeAnObject_andIgnoreMultiWindowSkipsTheFormula() {
        val e = assertFailsWith<IllegalArgumentException> {
            ScalingStrategies.loadJson("""{"strategies":[{"id":"x","rules":[{"when":{"width":600},"set":{"multiplier":2}}]}]}""")
        }
        assertTrue(e.message!!.contains("must be an object"), e.message)
        val s = scalingStrategy("mw", register = false) { ignoreMultiWindow = true; formula("base * 3") }
        assertEquals(16f, s.scale(16f, phone.copy(isMultiWindow = true)), 0f)
        assertEquals(48f, s.scale(16f, phone), 0f)
    }

    @Test
    fun reviewFixes_malformedJsonFailsLoudly_andLoadIsAtomic() {
        assertTrue(assertFailsWith<IllegalArgumentException> { ScalingStrategies.loadJson("""{"strategies":[{"id":"x","rules":[{"when":"tablet","set":{"multiplier":2}}]}]}""") }.message!!.contains("when must be an object"))
        assertTrue(assertFailsWith<IllegalArgumentException> { ScalingStrategies.loadJson("""{"strategies":[{"id":"x","ignoreMultiWindow":"true"}]}""") }.message!!.contains("must be a boolean"))
        assertTrue(assertFailsWith<IllegalArgumentException> { ScalingStrategies.loadJson("""{"strategies":[{"id":"x","clamp":1.5}]}""") }.message!!.contains("clamp must be an object"))
        assertTrue(assertFailsWith<IllegalArgumentException> { scalingStrategy("neg", register = false) { clampValue(max = -1) } }.message!!.contains("maxValue must be >= 0"))
        assertTrue(assertFailsWith<IllegalArgumentException> { ScalingStrategies.loadJson("""{"default":"missing","strategies":[{"id":"atomic","multiplier":2}]}""") }.message!!.contains("missing"))
        assertTrue(ScalingStrategies["atomic"] == null)   // nothing registered by the failed load
        // withAxis is cached per source instance, not per id.
        val a = scalingStrategy("same", register = false) { multiply(1f) }
        val b = scalingStrategy("same", register = false) { multiply(2f) }
        assertEquals(1f, a.withAxis(ScalingAxis.HEIGHT).config.multiplier, 0f)
        assertEquals(2f, b.withAxis(ScalingAxis.HEIGHT).config.multiplier, 0f)
        assertSame(b.withAxis(ScalingAxis.HEIGHT), b.withAxis(ScalingAxis.HEIGHT))
        // A sensitivity-only rule patch survives export + import.
        val s = ScalingStrategies.loadJson(
            """{"strategies":[{"id":"sens","aspectRatio":true,"rules":[{"when":{"device":"tablet"},"set":{"sensitivity":0.01}}]}]}""",
        ).getValue("sens")
        val again = ScalingStrategies.loadJson("""{"strategies":[${s.toJson()}]}""").getValue("sens")
        assertEquals(s.factor(tablet), again.factor(tablet), 0f)
    }

    @Test
    fun textFormula_errorsAreReportedEarly() {
        val e = assertFailsWith<IllegalArgumentException> { scalingFormula("base * sww") }
        assertTrue(e.message!!.contains("unknown variable 'sww'"), e.message)
    }

    @Test
    fun fontScale_isFollowedForText() {
        val big = phone.copy(fontScale = 1.5f)
        assertEquals(16f * 2f * 1.5f, scalingFormula("base * 2").resolve(big).scaleSpDp(16f), 1e-5f)
    }

    @Test
    fun explain_showsTheFormula() {
        assertTrue(scalingFormula("base * 2").explain(phone).endsWith(" then Formula(base * 2)"))
        assertTrue(scalingFormula { base }.explain(phone).endsWith(" then transform"))
    }
}
