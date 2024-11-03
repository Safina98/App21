package com.example.app21try6.utils

import android.app.Application
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.app21try6.MainActivity
import java.util.concurrent.TimeUnit

class MyApplication: Application()  {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        val wmbPreference = PreferenceManager.getDefaultSharedPreferences(this)
        val isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true)
        val mainActivity = MainActivity()  // Replace this with a valid reference to your main activity
        val observer = AppLifecycleObserver(mainActivity)

        ProcessLifecycleOwner.get().lifecycle.addObserver(observer)
        updateSummaryProductId()
        // removeDuplicatesTransSum()
       // if (isFirstRun) {
            //scheduleOneTimeMigrateDB()
            //scheduleOneTimeUpdateMerkWarna()
           // markFirstRunCompleted() // Set the flag after scheduling
        //}
    }
    private fun markFirstRunCompleted() {
        val wmbPreference = PreferenceManager.getDefaultSharedPreferences(this)
        wmbPreference.edit().putBoolean("FIRSTRUN", false).apply()
    }
    private fun updateSummaryProductId() {
        val workManager = WorkManager.getInstance(this)

        // Create a OneTimeWorkRequest for the worker
        val workRequest = OneTimeWorkRequestBuilder<UpdateSummaryProductIdWorker>()
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