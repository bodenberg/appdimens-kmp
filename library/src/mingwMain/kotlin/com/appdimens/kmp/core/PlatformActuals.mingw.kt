/**
 * Windows (mingwX64) platform actuals.
 *
 * EN Kotlin/Native ships no Win32 windowing bindings in the stdlib, so there is no
 *    ambient default window handle. `defaultPlatformContext()` returns `null`, matching
 *    the Android contract (a Context is always required): callers build their own
 *    [AppDimensContext] from a [ScreenConfiguration] or use the [DimenMetrics]
 *    overloads directly. The full code (non-Compose) API is available; the Compose
 *    layer is not, because Compose Multiplatform does not publish `ui`/`foundation`
 *    artifacts for MinGW targets.
 * PT Kotlin/Native não traz bindings Win32 na stdlib, então não há handle de janela
 *    padrão. `defaultPlatformContext()` retorna `null` (mesmo contrato do Android).
 *    A API code (não-Compose) completa está disponível; a camada Compose não, pois o
 *    Compose Multiplatform não publica artefatos `ui`/`foundation` para MinGW.
 */
package com.appdimens.kmp.core

actual fun defaultPlatformContext(): AppDimensContext? = null
