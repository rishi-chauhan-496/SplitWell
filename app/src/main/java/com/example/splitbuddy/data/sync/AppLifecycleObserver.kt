package com.example.splitbuddy.data.sync

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class AppLifecycleObserver(
    private val syncManager: SyncManager
) : DefaultLifecycleObserver {

    override fun onResume(owner: LifecycleOwner) {
        syncManager.syncIfNeeded()
    }
}