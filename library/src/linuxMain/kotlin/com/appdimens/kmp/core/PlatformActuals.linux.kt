/**
 * Linux (linuxX64 + linuxArm64) platform actuals.
 *
 * EN Kotlin/Native ships no Linux windowing API (no X11/Wayland bindings in the
 *    stdlib), so there is no ambient default window handle. `defaultPlatformContext()`
 *    returns `null`, matching the Android contract (a Context is always required):
 *    callers build their own [AppDimensContext] from a [ScreenConfiguration] or use
 *    the [DimenMetrics] overloads directly. The full code (non-Compose) API is
 *    available; the Compose layer is not, because Compose Multiplatform does not
 *    publish `ui`/`foundation` artifacts for Linux native targets.
 * PT Kotlin/Native não traz API de janela para Linux (sem bindings X11/Wayland na
 *    stdlib), então não há handle de janela padrão. `defaultPlatformContext()` retorna
 *    `null` (mesmo contrato do Android: Context é sempre obrigatório). A API code
 *    (não-Compose) completa está disponível; a camada Compose não, pois o Compose
 *    Multiplatform não publica artefatos `ui`/`foundation` para targets Linux nativos.
 */
package com.appdimens.kmp.core

actual fun defaultPlatformContext(): AppDimensContext? = null
