package com.example.app21try6.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.cloud.RealtimeDatabaseSync
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class UniversalSyncWorker(
    val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val db = VendibleDatabase.getInstance(context)

    override suspend fun doWork(): Result {
        Log.i("SyncManager","startCloudSync called")
        val correctUrl="https://onlineapp21-2679d-default-rtdb.asia-southeast1.firebasedatabase.app/"
        val firebase = FirebaseDatabase
            .getInstance(correctUrl)
            .reference
        RealtimeDatabaseSync.startConnectionListener(applicationContext)
        // Now safe to initialize everything else
        RealtimeDatabaseSync.init(context)
        Thread {
            Thread {
                val db = VendibleDatabase.getInstance(context)
                RealtimeDatabaseSync.startSyncAllTables(
                    brandDao    = db.brandDao,
                    categoryDao = db.categoryDao,
                    productDao = db.productDao
                )
            }.start()
        }.start()


        //  val categries=db.categoryDao.getPendingSync()


        // Example for Brand (repeat for all tables)
       // val brands = db.brandDao.getPendingSync()
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
