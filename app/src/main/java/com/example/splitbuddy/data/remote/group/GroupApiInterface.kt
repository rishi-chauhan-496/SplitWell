package com.example.splitbuddy.data.remote.group

interface GroupApiInterface {

    suspend fun groupCreationApi(request: CreateGroupRequest): GroupResponse
    suspend fun getGroupsByUserId(userId: String): List<GroupResponse>
    suspend fun updateGroup(groupId: String,request: UpdateGroupRequest): GroupResponse
    suspend fun deleteGroup(groupId: String): GroupResponse
    suspend fun addMultipleMemberToGroup(groupId: String, request: AddMembersRequest): List<Member>
    suspend fun updateMembersToGroup(groupId: String, userId: String): Member
    suspend fun removeMembersFromGroup(groupId: String, request: RemoveMembersRequest): List<Member>
}