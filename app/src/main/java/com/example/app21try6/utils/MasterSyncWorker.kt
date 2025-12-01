package com.example.app21try6.utils

// 📁 MasterSyncWorker.kt (Corrected Implementation)
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.cloud.RealtimeDatabaseSync

// Import your database components, SyncManager, and RealtimeDatabaseSync

class MasterSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    // IMPORTANT: Replace 'YourApplicationClass' with the actual name of your Application class.
    private val syncManager: SyncManager by lazy {
        // 1. Get the reference to your local Room database
        val appDatabase = VendibleDatabase.getInstance(appContext)

        // 2. Instantiate and return the SyncManager
        // This is the LAST expression in the block, so it is the return value.
        SyncManager(
            categoryDao = appDatabase.categoryDao, // Assuming your database has this method
            brandDao = appDatabase.brandDao,
            rdbSync = RealtimeDatabaseSync
        )
    }

    override suspend fun doWork(): Result {
        // ... (rest of your doWork() function remains the same)
        return try {
            syncManager.syncAllUnsyncedData()
            Result.success()
        } catch (e: Exception) {
            Log.e("MasterSyncWorker", "Sync failed, requesting retry.", e)
            Result.retry()
        }
    }
}