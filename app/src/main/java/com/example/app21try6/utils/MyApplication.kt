package com.example.app21try6.utils

import android.app.Application
import android.preference.PreferenceManager
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MyApplication: Application()  {
    override fun onCreate() {
        super.onCreate()
        val wmbPreference = PreferenceManager.getDefaultSharedPreferences(this)
        val isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true)
        if (isFirstRun) {
            scheduleOneTimeMigrateDB()
            //scheduleOneTimeUpdateMerkWarna()
            markFirstRunCompleted() // Set the flag after scheduling
        }

    }
    private fun markFirstRunCompleted() {
        val wmbPreference = PreferenceManager.getDefaultSharedPreferences(this)
        wmbPreference.edit().putBoolean("FIRSTRUN", false).apply()
    }
    private fun scheduleOneTimeUpdateMerkWarna() {
        val workManager = WorkManager.getInstance(this)

        // Create a OneTimeWorkRequest for the worker
        val workRequest = OneTimeWorkRequestBuilder<UpdateCustomerIdWorker>()
            .setInitialDelay(1, TimeUnit.SECONDS) // Optional: Adjust delay if needed
            .build()

        // Enqueue unique work, ensuring it runs only once
        workManager.enqueueUniqueWork(
            "UpdateCustomerIdWorker",
            ExistingWorkPolicy.KEEP, // This ensures the worker runs only once
            workRequest
        )
    }
    private fun scheduleOneTimeMigrateDB() {
        val workManager = WorkManager.getInstance(this)

        // Create a OneTimeWorkRequest for the worker
        val workRequest = OneTimeWorkRequestBuilder<UpdateCustomerIdWorker>()
            .setInitialDelay(1, TimeUnit.SECONDS) // Optional: Adjust delay if needed
            .build()

        // Enqueue unique work, ensuring it runs only once
        workManager.enqueueUniqueWork(
            "UpdateCustomerIdWorker",
            ExistingWorkPolicy.KEEP, // This ensures the worker runs only once
            workRequest
        )
    }
}