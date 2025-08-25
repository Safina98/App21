package com.example.app21try6.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.app21try6.database.tables.SuplierTable
import com.example.app21try6.database.VendibleDatabase


class UpdateSummaryProductIdWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        Log.i("WorkerProbs", "UpdateSummaryProductId starting")
        return try {
            performUpdate() // Directly call the suspend function
            Log.i("WorkerProbs", "Finnised")
            Result.success()

        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
    private suspend fun performUpdate() {
        val database = VendibleDatabase.getInstance(applicationContext) // Get Room database instance
        val suplierDao=database.suplierDao

        try {
            // Get all entries from trans_detail_table

            // For each entry, update trans_detail_date to the new format
            val suplierDummy= listOf<SuplierTable>(
                SuplierTable(1,"Mitra Jaya","Jakarta"),
                SuplierTable(2,"Polystar","Jakarta"),
                SuplierTable(3,"Busa Yerry","Surabaya"),
                SuplierTable(4,"Vision","Jakarta"),
                SuplierTable(5,"PT. SIMNU","Surabaya"),
                SuplierTable(6,"Aneka Lancar","Makassar"),
                SuplierTable(7,"Sentral Logam","Makassar"),
                SuplierTable(8,"Toko Utama","Makassar"),
                SuplierTable(9,"Bali Jaya","Makassar"),
            )

            // Update all entries with the new date format
            suplierDao.insert(suplierDummy)
        }catch (e:Exception){
            Log.i("WorkerProbs","worker Failed $e")
        }

    }
}