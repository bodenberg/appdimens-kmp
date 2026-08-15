package com.appdimens.dynamic.core

import com.appdimens.dynamic.common.DpQualifier
import com.appdimens.dynamic.code.sdp
import com.appdimens.dynamic.code.toDynamicScaledDp
import com.appdimens.dynamic.code.toDynamicScaledPx
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalAtomicApi::class)
/**
 * 3.1.8 fast-lane kernels ([DimenCache.resolveSdpPx] and siblings) must be
 * bit-exact against the untouched legacy full path (`toDynamicScaledPx` /
 * `toDynamicScaledDp`) and must follow the event-driven invalidation contract:
 * a configuration change invalidates the fast slot synchronously.
 *
 * PT Os kernels do fast lane 3.1.8 ([DimenCache.resolveSdpPx] e irmãos) devem ser
 * bit-exatos em relação ao caminho completo legado intacto (`toDynamicScaledPx` /
 * `toDynamicScaledDp`) e devem seguir o contrato de invalidação orientada a eventos:
 * uma mudança de configuração invalida o slot rápido sincronamente.
 */
class DimenFastLaneKernelsTest {

    private lateinit var ctx: FakeAppDimensContext

    @BeforeTest
    fun setup() {
        ctx = FakeAppDimensContext(sw = 360, w = 720, h = 800, dpi = 420)
        DimenCache.clearAll()
        DimenCache.isEnabled.store(true)
        DimenCache.invalidateOnConfigChange(ctx.configuration)
    }

    private fun legacyPx(base: Float, qualifier: DpQualifier, ar: Boolean = false): Float =
        base.toDynamicScaledPx(ctx, qualifier, applyAspectRatio = ar)

    private fun legacyDp(base: Float, qualifier: DpQualifier, ar: Boolean = false): Float =
        base.toDynamicScaledDp(ctx, qualifier, applyAspectRatio = ar)

    private val bases = floatArrayOf(1f, 2f, 7f, 16f, 24f, 48f, 100f, 300f, 1024f, 3.5f, 0.75f)

    @Test
    fun sdpKernel_isBitExactWithLegacyPath() {
        for (b in bases) {
            val fast = DimenCache.resolveSdpPx(b, ctx)
            val legacy = legacyPx(b, DpQualifier.SMALL_WIDTH)
            assertEquals(legacy.toRawBits(), fast.toRawBits(), "sdp px base=$b")
            val fastDp = DimenCache.resolveSdpDp(b, ctx)
            val legacyDp = legacyDp(b, DpQualifier.SMALL_WIDTH)
            assertEquals(legacyDp.toRawBits(), fastDp.toRawBits(), "sdp dp base=$b")
        }
    }

    @Test
    fun sdpaKernel_isBitExactWithLegacyPath() {
        for (b in bases) {
            val fast = DimenCache.resolveSdpaPx(b, ctx)
            val legacy = legacyPx(b, DpQualifier.SMALL_WIDTH, ar = true)
            assertEquals(legacy.toRawBits(), fast.toRawBits(), "sdpa px base=$b")
            val fastDp = DimenCache.resolveSdpaDp(b, ctx)
            val legacyDp = legacyDp(b, DpQualifier.SMALL_WIDTH, ar = true)
            assertEquals(legacyDp.toRawBits(), fastDp.toRawBits(), "sdpa dp base=$b")
        }
    }

    @Test
    fun hdpKernel_isBitExactWithLegacyPath() {
        for (b in bases) {
            val fast = DimenCache.resolveHdpPx(b, ctx)
            val legacy = legacyPx(b, DpQualifier.HEIGHT)
            assertEquals(legacy.toRawBits(), fast.toRawBits(), "hdp px base=$b")
            val fastDp = DimenCache.resolveHdpDp(b, ctx)
            val legacyDp = legacyDp(b, DpQualifier.HEIGHT)
            assertEquals(legacyDp.toRawBits(), fastDp.toRawBits(), "hdp dp base=$b")
        }
    }

    @Test
    fun wdpKernel_isBitExactWithLegacyPath() {
        for (b in bases) {
            val fast = DimenCache.resolveWdpPx(b, ctx)
            val legacy = legacyPx(b, DpQualifier.WIDTH)
            assertEquals(legacy.toRawBits(), fast.toRawBits(), "wdp px base=$b")
            val fastDp = DimenCache.resolveWdpDp(b, ctx)
            val legacyDp = legacyDp(b, DpQualifier.WIDTH)
            assertEquals(legacyDp.toRawBits(), fastDp.toRawBits(), "wdp dp base=$b")
        }
    }

    @Test
    fun kernelResults_matchPublicExtensionEntries() {
        val fast = DimenCache.resolveSdpPx(16f, ctx)
        val viaExtension = 16.sdp(ctx)
        assertEquals(fast.toRawBits(), viaExtension.toRawBits(), "16.sdp(ctx) must hit the same kernel")
        val fastDp = DimenCache.resolveSdpaDp(16f, ctx)
        val viaExtensionDp = 16f.toDynamicScaledDp(ctx, DpQualifier.SMALL_WIDTH, applyAspectRatio = true)
        assertEquals(fastDp.toRawBits(), viaExtensionDp.toRawBits(), "toDynamicScaledDp(ar) must hit the same kernel")
    }

    @Test
    fun configChange_invalidatesFastSlotSynchronously() {
        DimenCache.resolveSdpPx(16f, ctx)
        DimenCache.invalidateOnConfigChange(ctx.configuration)
        assertTrue(
            DimenCache.fastWindowSlot.load() === DimenCache.EMPTY_FAST_WINDOW_SLOT,
            "invalidateOnConfigChange must reset the fast slot to the empty sentinel"
        )

        ctx.sw = 800
        ctx.w = 1440
        ctx.dpi = 560
        DimenCache.invalidateOnConfigChange(ctx.configuration)

        val fast = DimenCache.resolveSdpPx(16f, ctx)
        val expected = 16f * (800f * DimenCache.INV_BASE_RATIO) * (560f / 160f)
        assertEquals(expected, fast, 0f, "kernel must rebuild with the new snapshot")
        val legacy = legacyPx(16f, DpQualifier.SMALL_WIDTH)
        assertEquals(legacy.toRawBits(), fast.toRawBits(), "kernel and legacy must stay bit-exact after the change")
    }

    @Test
    fun newContextIdentity_rebuildsMetricsAfterResize() {
        // EN Desktop/web/iOS/macOS AppDimensProvider builds the AppDimensContext
        //    from the LIVE window configuration with `remember(configuration)`: on a
        //    resize the provider produces a NEW context instance. The fast-window
        //    slot stores the OLD context identity, so the identity check must miss
        //    and metrics must rebuild with the new snapshot — this is how those
        //    platforms self-heal without registerConfigurationListener (which is a
        //    no-op outside Android).
        // PT O AppDimensProvider de desktop/web/iOS/macOS constrói o
        //    AppDimensContext da configuração VIVA da janela com
        //    `remember(configuration)`: num resize o provider produz uma NOVA
        //    instância de contexto. O fast-window-slot guarda a identidade antiga,
        //    então a checagem de identidade deve errar e as métricas devem ser
        //    reconstruídas com o novo snapshot — é assim que essas plataformas se
        //    auto-curam sem registerConfigurationListener (no-op fora do Android).
        val first = DimenCache.resolveSdpPx(16f, ctx)
        // Simulate a provider rebuild after a window resize: same metrics values as
        // before, but a brand-new context instance (new identity).
        val resized = FakeAppDimensContext(sw = 800, w = 1440, h = 900, dpi = 560)
        val fast = DimenCache.resolveSdpPx(16f, resized)
        val expected = 16f * (800f * DimenCache.INV_BASE_RATIO) * (560f / 160f)
        assertEquals(expected, fast, 0f, "new context identity must trigger a metrics rebuild")
        assertTrue(
            first != fast,
            "metrics must differ after the resize (old sw=360,dpi=420 vs new sw=800,dpi=560)"
        )
        // And the new identity becomes the fast slot owner: subsequent calls are fast.
        val fast2 = DimenCache.resolveSdpPx(16f, resized)
        assertEquals(fast, fast2, 0f, "repeat calls on the new context must hit the fast lane")
    }
}