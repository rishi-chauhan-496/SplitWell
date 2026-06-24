package com.app.splitwell.data.sync

import android.content.SharedPreferences
import com.app.splitwell.data.util.SyncInterval
import com.app.splitwell.domain.repository.ExpenseRepository
import com.app.splitwell.domain.repository.GroupRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SyncManager(
    private val groupRepository: GroupRepository,
    private val expenseRepository: ExpenseRepository,
    private val sharedPreferences: SharedPreferences
) {
    // Own coroutine scope — survives ViewModel lifecycle
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var lastSyncTime = 0L

    // ── Called from lifecycle observer ────────────────────────────────────────

    fun syncIfNeeded() {
        val now = System.currentTimeMillis()
        if (now - lastSyncTime > SyncInterval.MIN_SYNC_INTERVAL_MS) {
            syncAll()
            lastSyncTime = now
        }
    }

    // ── Force sync — called on pull-to-refresh ────────────────────────────────

    fun forceSync() {
        lastSyncTime = 0L
        syncAll()
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private fun syncAll() {
        val userId = sharedPreferences.getString("userId", null) ?: return

        scope.launch {
            // Run all syncs in parallel — faster than sequential
            val groupsSync  = async { groupRepository.sync(userId) }
            val expenseSync = async { expenseRepository.sync() }

            groupsSync.await()
            expenseSync.await()
        }
    }
}