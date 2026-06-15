package com.example.splitbuddy.data.remote.invite

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InviteApiService {

    @POST("groups/{groupId}/invite-links")
    suspend fun createInviteLink(
        @Path("groupId") groupId: String,
        @Body request: CreateInviteLinkRequest
    ): InviteLinkResponse

    // Preview link before accepting
    @GET("invite-links/{token}")
    suspend fun previewInviteLink(
        @Path("token") token: String
    ): InvitePreviewResponse

    // Accept invite → join group
    @POST("invite-links/{token}/accept")
    suspend fun acceptInviteLink(
        @Path("token") token: String,
        @Body request: AcceptInviteRequest
    ): AcceptInviteResponse
}