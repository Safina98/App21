package com.example.app21try6.utils

import android.util.Log
import com.example.app21try6.database.cloud.CategoryCloud
import com.example.app21try6.database.cloud.RealtimeDatabaseSync
import com.example.app21try6.database.daos.BrandDao
import com.example.app21try6.database.daos.CategoryDao

// 📁 SyncManager.kt
class SyncManager(
    private val categoryDao: CategoryDao,
    private val brandDao: BrandDao,
    private val rdbSync: RealtimeDatabaseSync // Your existing singleton
) {

    // A list of functions, where each function handles the sync for one table
    private val syncRoutines: List<suspend () -> Unit> = listOf(
        { syncCategories() },
        { syncBrands() },
        // ... 16 more single-line calls ...
    )

    // Run all sync routines sequentially
    suspend fun syncAllUnsyncedData() {
        Log.d("SyncManager", "Starting full sync routine.")
        syncRoutines.forEach { it.invoke() }
        Log.d("SyncManager", "Finished full sync routine.")
    }

    // ------------------------------------------------------------------
    // Example: A dedicated, reusable sync function for one table
    // ------------------------------------------------------------------
    // 📁 SyncManager.kt (Example for Category Sync Routine)

    private suspend fun syncCategories() {
        val unsyncedList = categoryDao.getAllNeedsSync()

        for (category in unsyncedList) {
            try {
                val cloudObject = CategoryCloud(categoryName = category.category_name)

                // 1. CALL THE SUSPENDING FUNCTION
                rdbSync.uploadSuspended( // <-- Using the new function
                    tableName = "category_table",
                    cloudId = category.categoryCloudId.toString(),
                    cloudObject = cloudObject
                )

                // 2. ONLY MARK AS SYNCED IF THE UPLOAD SUCCEEDED
                categoryDao.markAsSynced(category.categoryCloudId)

            } catch (e: Exception) {
                // If the upload failed (e.g., no internet), the exception is caught here.
                // We just log it and move to the next item. The Worker will catch this failure
                // and return Result.retry() to re-schedule the entire job.
                Log.w("SyncManager", "Upload failed for category ${category.categoryCloudId}: ${e.message}")
            }
        }
    }
// Repeat this logic for syncBrands() and all other 16 tables.

    private suspend fun syncBrands() {
        // ... similar logic for BrandDao ...
    }
}