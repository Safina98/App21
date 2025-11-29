package com.example.app21try6.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.app21try6.database.VendibleDatabase
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class UniversalSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val db = VendibleDatabase.getInstance(context)

    override suspend fun doWork(): Result {
        val correctUrl="https://onlineapp21-2679d-default-rtdb.asia-southeast1.firebasedatabase.app/"
        val firebase = FirebaseDatabase
            .getInstance(correctUrl)
            .reference

        // Example for Brand (repeat for all tables)
        val brands = db.brandDao.getPendingSync()
/*
        for (b in brands) {
            firebase.child("brand_table")
                .child(b.cloudId!!)
                .setValue(b)
                .await()

            db.brandDao.markSynced(b.brand_id)
        }

 */

        // Do the same for:
        // categoryDao, productDao, subDao, summaryDao, etc…

        return Result.success()
    }
}
