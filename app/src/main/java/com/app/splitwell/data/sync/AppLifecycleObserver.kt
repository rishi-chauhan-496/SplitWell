package com.app.splitwell.data.sync

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class AppLifecycleObserver(
    private val syncManager: SyncManager
) : DefaultLifecycleObserver {

    override fun onResume(owner: LifecycleOwner) {
        syncManager.syncIfNeeded()
    }
}