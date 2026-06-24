package com.app.splitwell.data.remote.group

import retrofit2.http.*

interface GroupApiService {

    // Create group
    @POST("groups")
    suspend fun createGroup(
        @Body request: CreateGroupRequest
    ): GroupResponse

    // Get all groups by userId
    @GET("groups/user/{userId}")
    suspend fun getGroupsByUserId(
        @Path("userId") userId: String
    ): List<GroupResponse>

    // Update group
    @PATCH("groups/{groupId}")
    suspend fun updateGroup(
        @Path("groupId") groupId: String,
        @Body request: UpdateGroupRequest
    ): GroupResponse

    // Delete group
    @DELETE("groups/{groupId}")
    suspend fun deleteGroup(
        @Path("groupId") groupId: String
    ): GroupResponse

    // Add multiple member to group
    @POST("groups/{groupId}/members/bulk")
    suspend fun addMembersToGroup(
        @Path("groupId") groupId: String,
        @Body request: AddMembersRequest
    ): List<Member>

    //Update member to group
    @DELETE("groups/{groupId}/members/{memberId}")
    suspend fun updateMember(
        @Path("groupId") groupId: String,
        @Path("memberId") memberId: String
    ): Member

    // Remove multiple members from group
    @HTTP(method = "DELETE", path = "groups/{groupId}/members/bulk", hasBody = true)
    suspend fun removeMembersFromGroup(
        @Path("groupId") groupId: String,
        @Body request: RemoveMembersRequest
    ): List<Member>

    // Get single group by id — used to refresh one group's data, not the whole list
    @GET("groups/{groupId}")
    suspend fun getGroupById(
        @Path("groupId") groupId: String
    ): GroupResponse
}