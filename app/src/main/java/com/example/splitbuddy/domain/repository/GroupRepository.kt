package com.example.splitbuddy.domain.repository

import com.example.splitbuddy.data.local.model.Trip
import com.example.splitbuddy.data.local.model.TripManager
import com.example.splitbuddy.data.remote.group.AddMembersRequest
import com.example.splitbuddy.data.remote.group.CreateGroupRequest
import com.example.splitbuddy.data.remote.group.Member
import com.example.splitbuddy.data.remote.group.UpdateGroupRequest

interface GroupRepository {

    suspend fun groupCreation(request: CreateGroupRequest): Trip?
    suspend fun getAllGroups(userId: String): List<Trip>
    suspend fun getGroup(groupId: String): Trip?
    suspend fun updateGroup(groupId: String,request: UpdateGroupRequest): Boolean
    suspend fun deleteGroup(groupId: String): Boolean
    suspend fun getGroupMemberByGroupId(groupId: String): List<TripManager>
    suspend fun updateGroupMemberByGroupId(groupId: String, userId: String): Boolean
    suspend fun addMultipleMemberToGroup(groupId: String, body: AddMembersRequest): Boolean
}