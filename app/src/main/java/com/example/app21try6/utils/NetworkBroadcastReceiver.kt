package com.example.app21try6.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NetworkReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        CloudSyncManager.startCloudSync(context)
    }
}
