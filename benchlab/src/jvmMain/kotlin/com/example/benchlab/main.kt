/**
 * EN Desktop (JVM) entry point — Compose Desktop window running the full
 *    2-way benchmark. The report export writes a .txt file to
 *    `~/BenchLab/benchlab_report_*.txt`.
 * PT Entry point Desktop (JVM) — janela Compose Desktop com o benchmark 2-vias
 *    completo. A exportação do relatório grava um .txt em
 *    `~/BenchLab/benchlab_report_*.txt`.
 */
package com.example.benchlab

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.appdimens.kmp.core.AppDimensProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun main() = application {
    // EN Headless automation: `AUTO_START=true` (env or -D system property)
    //    runs the benchmark immediately, like the Android entry. The report is
    //    still written to ~/BenchLab.
    // PT Automação headless: `AUTO_START=true` (env ou -D system property)
    //    executa o benchmark imediatamente, como o entry Android. O relatório
    //    continua sendo gravado em ~/BenchLab.
    val autoStart = System.getProperty("AUTO_START") == "true"
            || System.getenv("AUTO_START") == "true"
    Window(
        onCloseRequest = ::exitApplication,
        title = "BenchLab — AppDimens KMP × Lib #2",
    ) {
        AppDimensProvider {
            BenchlabScreen(
                autoStart = autoStart,
                reportSaver = ::saveReportToFileDesktop,
            )
        }
    }
}

/** EN Writes [report] to ~/BenchLab. PT Grava [report] em ~/BenchLab. */
private suspend fun saveReportToFileDesktop(report: String): String = withContext(Dispatchers.IO) {
    val dir = File(System.getProperty("user.home"), "BenchLab").apply { mkdirs() }
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val file = File(dir, "benchlab_report_${timestamp}.txt")
    file.writeText(report)
    "Relatório salvo em ${file.absolutePath}"
}
