package com.example.app21try6.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.app21try6.database.TransactionSummary
import com.example.app21try6.database.VendibleDatabase

class DeleteMultipleTransumWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) { override suspend fun doWork(): Result {
    Log.i("WorkerProbs", "DeleteMultipleTranssum starting")
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
        val database = VendibleDatabase.getInstance(applicationContext)

        val transSumDao = database.transSumDao
        val allTransSum = transSumDao.getAllTransSumList()
        val uniqueTransMap = mutableMapOf<String, TransactionSummary>()
        Log.i("WorkerProbs","all ${allTransSum.size}")
        // Populate the map with unique entries
        for (trans in allTransSum) {
            if (!uniqueTransMap.containsKey(trans.ref)) {
                uniqueTransMap[trans.ref] = trans
            }
        }

        // Get the unique transactions
        val uniqueTransSumList = uniqueTransMap.values.toList()
        Log.i("WorkerProbs","unique ${uniqueTransSumList.size}")
        val duplicates = allTransSum.filter { trans ->
            uniqueTransSumList.none { uniqueTrans -> uniqueTrans.ref == trans.ref }
        }

// Log the size of duplicates
        Log.i("WorkerProbs", "Duplicate count: ${duplicates.size}")
    }

}