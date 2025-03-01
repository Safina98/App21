package com.example.app21try6.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object DatabaseBackupHelper {

    fun shareDatabaseBackup(context: Context, onCompletion: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val zipFile = withContext(Dispatchers.IO) {
                    zipDatabaseFiles(context, "vendible_table")
                }
                val fileUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", zipFile)
                val shareIntent: Intent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    type = "application/zip"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share database file"))
            } catch (e: Exception) {
                Log.e("ZipDB", "Error sharing database file: ${e.localizedMessage}", e)
            } finally {
                onCompletion()
            }
        }
    }

    fun zipDatabaseFiles(context: Context, databaseName: String): File {
        val dbPath = context.getDatabasePath(databaseName).absolutePath
        val walPath = "$dbPath-wal"
        val shmPath = "$dbPath-shm"
        val zipFile = File(context.externalCacheDir, "database_backup.zip")

        ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
            addFileToZip(zipOut, File(dbPath))
            addFileToZip(zipOut, File(walPath))
            addFileToZip(zipOut, File(shmPath))
        }
        return zipFile
    }

    private fun addFileToZip(zipOut: ZipOutputStream, file: File) {
        FileInputStream(file).use { fis ->
            val zipEntry = ZipEntry(file.name)
            zipOut.putNextEntry(zipEntry)
            fis.copyTo(zipOut)
            zipOut.closeEntry()
        }
    }

    fun importZipFile(context: Context, resultLauncher: ActivityResultLauncher<String>) {
        try {
            resultLauncher.launch("application/zip")
        } catch (e: Exception) {
            Toast.makeText(context, "Error selecting file", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleZipImportResult(context: Context, uri: Uri?, onCompletion: () -> Unit) {
        if (uri != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val tempFile = readFileFromUri(context, uri)
                if (tempFile?.exists() == true) {
                    try {
                        extractZipFile(context, tempFile)
                    } catch (e: IOException) {
                        Log.e("ZipDB", "Error extracting zip file", e)
                    }
                }
                withContext(Dispatchers.Main) {
                    onCompletion()
                }
            }
        } else {
            Log.i("ZipDB", "No file selected")
            onCompletion()
        }
    }

    private fun readFileFromUri(context: Context, uri: Uri): File? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val tempFile = File.createTempFile("imported_db", ".zip", context.cacheDir)
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                tempFile
            }
        } catch (e: Exception) {
            Log.e("FileReadError", "Exception while reading file from URI: ${e.localizedMessage}", e)
            null
        }
    }

    private fun extractZipFile(context: Context, zipFile: File) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ZipInputStream(FileInputStream(zipFile)).use { zipInputStream ->
                    var zipEntry: ZipEntry? = zipInputStream.nextEntry
                    while (zipEntry != null) {
                        val outputFile = File(context.getDatabasePath(zipEntry.name).parent, zipEntry.name)
                        if (zipEntry.isDirectory) {
                            outputFile.mkdirs()
                        } else {
                            FileOutputStream(outputFile).use { outputStream ->
                                val buffer = ByteArray(1024)
                                var length: Int
                                while (zipInputStream.read(buffer).also { length = it } > 0) {
                                    outputStream.write(buffer, 0, length)
                                }
                            }
                        }
                        zipEntry = zipInputStream.nextEntry
                    }
                }
                Log.i("ZipDB", "Files extracted successfully")
            } catch (e: IOException) {
                Log.e("ZipDB", "Error extracting zip file", e)
            }
        }
    }
}
