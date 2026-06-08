package com.example.splitbuddy.domain.repository

import com.example.splitbuddy.data.local.model.Trip
import com.example.splitbuddy.data.local.model.TripManager
import com.example.splitbuddy.data.remote.group.AddMembersRequest
import com.example.splitbuddy.data.remote.group.CreateGroupRequest
import com.example.splitbuddy.data.remote.group.UpdateGroupRequest
import com.example.splitbuddy.data.util.Resource
import kotlinx.coroutines.flow.StateFlow

interface GroupRepository {

    // ── Observable — UI collects this ─────────────────────────────────────────
    val groupsFlow: StateFlow<Resource<List<Trip>>>

    // ── Sync — SyncManager calls this ────────────────────────────────────────
    suspend fun sync(userId: String)

    // ── Write operations — return Resource ───────────────────────────────────
    suspend fun groupCreation(request: CreateGroupRequest): Resource<Trip>
    suspend fun getGroup(groupId: String): Trip?
    suspend fun updateGroup(groupId: String, request: UpdateGroupRequest): Resource<Boolean>
    suspend fun deleteGroup(groupId: String): Resource<Boolean>
    suspend fun getGroupMemberByGroupId(groupId: String): List<TripManager>
    suspend fun updateGroupMemberByGroupId(groupId: String, userId: String): Resource<Boolean>
    suspend fun addMultipleMemberToGroup(groupId: String, body: AddMembersRequest): Resource<Boolean>
}