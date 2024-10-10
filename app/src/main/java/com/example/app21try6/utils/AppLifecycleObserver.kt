package com.example.app21try6.utils

import android.os.SystemClock
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.app21try6.MainActivity

class AppLifecycleObserver(private val activity: MainActivity) : DefaultLifecycleObserver {
    private var backgroundTime: Long = 0

    override fun onStop(owner: LifecycleOwner) {
        // Record the time when the app goes to the background
        backgroundTime = SystemClock.elapsedRealtime()
    }

    override fun onStart(owner: LifecycleOwner) {
        // Check if the app has been in the background for over an hour
        if (backgroundTime != 0L && SystemClock.elapsedRealtime() - backgroundTime >= 3600000) {
            // Close the app if it has been in the background for one hour
            activity.finishAffinity()
        }
        backgroundTime = 0
    }
}
