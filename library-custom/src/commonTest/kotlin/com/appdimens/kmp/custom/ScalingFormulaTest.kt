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
        val s = scalingFormula { base, screen -> base * (screen.smallestWidthDp / 360f).pow(0.8f) + if (screen.isLandscape) 2f else 0f }
        assertEquals(16f * (411f / 360f).pow(0.8f), s.scale(16f, phone), 1e-5f)
        assertEquals(16f * (800f / 360f).pow(0.8f) + 2f, s.scale(16f, tablet), 1e-4f)
        assertTrue(s.isValueDependent)
        assertTrue(s.id.startsWith("formula#"))
        assertTrue(scalingFormula { b, _ -> b }.id != s.id)
        val named = scalingFormula(id = "hud") { base, _ -> base * 3f }
        assertSame(named, ScalingStrategies["hud"])
    }

    @Test
    fun programmableFactor_isScreenOnly_andMultipliesEveryValue() {
        val product = CustomScaleStrategy { screen -> (screen.widthDp / 360f).coerceIn(0.85f, 1.80f) }
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
        assertTrue(scalingFormula { b, _ -> b }.explain(phone).endsWith(" then transform"))
    }
}
