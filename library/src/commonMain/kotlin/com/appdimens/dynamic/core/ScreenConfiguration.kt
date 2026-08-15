/**
 * KMP common screen snapshot — platform-neutral replacement for the Android
 * `android.content.res.Configuration` reads used by the library.
 *
 * A dimension must be a pure function of the window in which it is rendered.
 * Keeping these values together prevents a calculation from observing a mix of
 * old and new configuration fields while a window is being resized.
 */
package com.appdimens.dynamic.core

/**
 * EN Immutable snapshot of the window fields that drive dimension resolution.
 * All platforms (Android / JVM desktop / iOS) produce this from their native
 * window state; the rest of the library never touches platform APIs.
 *
 * PT Snapshot imutável dos campos de janela que dirigem a resolução de dimensões.
 * Todas as plataformas (Android / JVM desktop / iOS) produzem isto a partir do
 * estado nativo da janela; o resto da biblioteca nunca toca em APIs de plataforma.
 */
data class ScreenConfiguration(
    val screenWidthDp: Int,
    val screenHeightDp: Int,
    val smallestScreenWidthDp: Int,
    val densityDpi: Int,
    val fontScale: Float,
    val orientation: Int,
    val uiMode: Int,
) {
    companion object {
        /** Matches `Configuration.ORIENTATION_UNDEFINED`. */
        const val ORIENTATION_UNDEFINED = 0

        /** Matches `Configuration.ORIENTATION_PORTRAIT`. */
        const val ORIENTATION_PORTRAIT = 1

        /** Matches `Configuration.ORIENTATION_LANDSCAPE`. */
        const val ORIENTATION_LANDSCAPE = 2

        /** EN Synthetic baseline window (300×533 @ 160 dpi) used by tests and defaults. */
        val DEFAULT: ScreenConfiguration = ScreenConfiguration(
            screenWidthDp = 300,
            screenHeightDp = 533,
            smallestScreenWidthDp = 300,
            densityDpi = 160,
            fontScale = 1f,
            orientation = ORIENTATION_UNDEFINED,
            uiMode = 0,
        )
    }
}