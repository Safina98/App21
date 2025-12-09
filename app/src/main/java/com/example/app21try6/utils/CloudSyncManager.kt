package com.example.app21try6.utils

import android.content.Context
import android.util.Log
import androidx.work.*

object CloudSyncManager {

    fun startCloudSync(context: Context) {
        Log.i("SyncManager","startCloudSync called")
        val work = OneTimeWorkRequestBuilder<UniversalSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueue(work)
    }
}
