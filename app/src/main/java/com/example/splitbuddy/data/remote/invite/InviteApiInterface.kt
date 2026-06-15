package com.example.splitbuddy.data.remote.invite

interface InviteApiInterface {
    suspend fun createInviteLink(
        groupId: String,
        request: CreateInviteLinkRequest
    ): InviteLinkResponse
    suspend fun previewInviteLink(token: String): InvitePreviewResponse
    suspend fun acceptInviteLink(
        token: String,
        request: AcceptInviteRequest
    ): AcceptInviteResponse
}