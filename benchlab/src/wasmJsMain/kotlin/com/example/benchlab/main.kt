/**
 * EN Web (Wasm) entry point — runs the full 2-way benchmark in the browser.
 *    The report export downloads a .txt file via a Blob URL.
 * PT Entry point Web (Wasm) — roda o benchmark 2-vias completo no navegador.
 *    A exportação do relatório baixa um arquivo .txt via Blob URL.
 */
package com.example.benchlab

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.appdimens.kmp.core.AppDimensProvider
import kotlin.js.JsString
import kotlin.js.toJsArray
import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob

/**
 * EN Auto-starts the benchmark when the page is opened (same headless
 *    behaviour as `adb shell am start --ez AUTO_START true` on Android).
 * PT Dispara o benchmark automaticamente ao abrir a página (mesmo
 *    comportamento headless do Android com `--ez AUTO_START true`).
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport("composeApp") {
        AppDimensProvider {
            BenchlabScreen(
                autoStart = true,
                reportSaver = ::downloadReport,
            )
        }
    }
}

/** EN Downloads [report] as benchlab_report.txt. PT Baixa [report] como benchlab_report.txt. */
private suspend fun downloadReport(report: String): String {
    val blob = Blob(arrayOf<JsAny?>(report as JsString).toJsArray())
    val url = URL.createObjectURL(blob)
    val anchor = document.createElement("a").unsafeCast<HTMLAnchorElement>()
    anchor.href = url
    anchor.download = "benchlab_report.txt"
    anchor.click()
    return "Relatório baixado (benchlab_report.txt)"
}
