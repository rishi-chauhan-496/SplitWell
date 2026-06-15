package com.example.splitbuddy.data.remote.invite

import com.example.splitbuddy.data.remote.RetrofitInstance

class InviteApiInterfaceImpl : InviteApiInterface {

    private val api = RetrofitInstance.inviteApi

    override suspend fun createInviteLink(
        groupId: String,
        request: CreateInviteLinkRequest
    ): InviteLinkResponse = api.createInviteLink(groupId, request)

    override suspend fun previewInviteLink(
        token: String
    ): InvitePreviewResponse = api.previewInviteLink(token)

    override suspend fun acceptInviteLink(
        token: String,
        request: AcceptInviteRequest
    ): AcceptInviteResponse = api.acceptInviteLink(token, request)
}