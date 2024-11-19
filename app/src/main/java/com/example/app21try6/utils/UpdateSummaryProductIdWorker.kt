package com.example.app21try6.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.app21try6.DETAILED_DATE_FORMATTER
import com.example.app21try6.SIMPLE_DATE_FORMATTER
import com.example.app21try6.database.DateTypeConverter
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.formatRupiah

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
        val transactionDetailDao = database.transDetailDao

        try {
            // Get all entries from trans_detail_table
            val transactionDetails = transactionDetailDao.getAllTransDetail()

            // For each entry, update trans_detail_date to the new format
            val updatedDetails = transactionDetails.map { transaction ->
                transaction.trans_detail_date?.let { oldDate ->
                    // Convert old date to string in the new format
                    val formattedDateString = DateTypeConverter.fromDate(oldDate)

                    // Parse the formatted string back to a Date object
                    val newFormattedDate = DateTypeConverter.toDate(formattedDateString)

                    // Update transaction with new formatted Date
                    transaction.copy(trans_detail_date = newFormattedDate)
                } ?: transaction
            }

            // Update all entries with the new date format
            transactionDetailDao.updateTransactions(updatedDetails)
        }catch (e:Exception){

        }

    }
}