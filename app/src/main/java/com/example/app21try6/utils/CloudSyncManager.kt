package com.example.app21try6.utils

import android.content.Context
import androidx.work.*

object CloudSyncManager {

    fun startCloudSync(context: Context) {
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
