/**
 * EN Thin Android entry: hosts the common KMP [BenchlabScreen] and wires the
 *    report export to MediaStore (Documents/BenchLab). `AUTO_START=true`
 *    triggers the benchmark immediately (headless automation).
 * PT Entry Android fino: hospeda a [BenchlabScreen] KMP comum e liga a
 *    exportação do relatório ao MediaStore (Documents/BenchLab).
 *    `AUTO_START=true` dispara o benchmark imediatamente (automação headless).
 */
package com.example.benchlab

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.appdimens.dynamic.core.AppDimensProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BenchlabActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val autoStart = intent.getBooleanExtra("AUTO_START", false)
        setContent {
            AppDimensProvider {
                BenchlabScreen(
                    autoStart = autoStart,
                    reportSaver = { report -> saveReportToMediaStore(applicationContext, report) },
                )
            }
        }
    }
}

/** EN Saves the report via MediaStore (Documents/BenchLab). PT Salva o relatório via MediaStore. */
private suspend fun saveReportToMediaStore(
    context: android.content.Context,
    report: String,
): String = withContext(Dispatchers.IO) {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val fileName = "benchlab_report_${timestamp}.txt"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val values = ContentValues().apply {
            put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
            put(MediaStore.Files.FileColumns.MIME_TYPE, "text/plain")
            put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/BenchLab")
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), values)
        uri?.let {
            resolver.openOutputStream(it)?.use { os ->
                os.write(report.toByteArray())
            }
        }
        "Relatório salvo em Documents/BenchLab"
    } else {
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "BenchLab")
        dir.mkdirs()
        val file = File(dir, fileName)
        FileOutputStream(file).use { it.write(report.toByteArray()) }
        "Relatório salvo em ${file.absolutePath}"
    }
}
