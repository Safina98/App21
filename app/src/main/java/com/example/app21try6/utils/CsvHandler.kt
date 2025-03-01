package com.example.app21try6.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader

class CsvHandler(private val context: Context) {

    fun importCSVStock(resultLauncher: ActivityResultLauncher<Intent>) {
        val fileIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "text/*"
        }
        try {
            resultLauncher.launch(fileIntent)
        } catch (e: FileNotFoundException) {
            Toast.makeText(context, "Import Gagal", Toast.LENGTH_SHORT).show()
            Log.e("CSV Import", "Error reading CSV: $e")
        }
    }

    fun exportStockCSV(fileName: String, writeCsv: (File) -> Unit) {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "$fileName.csv")
        Log.i("FilePath", "Exporting to: ${file.path}")

        writeCsv(file) // Call the provided function to write the CSV data

        val photoURI: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, photoURI)
            type = "text/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            context.startActivity(Intent.createChooser(shareIntent, "Share CSV File"))
        } catch (e: Exception) {
            Log.e("CSV Export", "Error sharing file: $e")
        }
    }

    fun handleCsvResult(result: ActivityResult, processCsvData: (List<List<String>>) -> Unit) {
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            var isFirstLine = true
            val tokensList = mutableListOf<List<String>>()

            try {
                data?.data?.let { uri ->
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        BufferedReader(InputStreamReader(inputStream)).use { reader ->
                            reader.forEachLine { line ->
                                if (!isFirstLine) {
                                    tokensList.add(line.split(","))
                                }
                                isFirstLine = false
                            }
                        }
                    }
                }

                processCsvData(tokensList) // Call the provided function to handle CSV data
            } catch (e: Exception) {
                Toast.makeText(context, "Import Gagal", Toast.LENGTH_SHORT).show()
                Log.e("CSV Import", "Error reading CSV: $e")
            }
        }
    }
}
