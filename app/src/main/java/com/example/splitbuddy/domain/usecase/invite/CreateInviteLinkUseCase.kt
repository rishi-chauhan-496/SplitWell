package com.example.splitbuddy.domain.usecase.invite

import com.example.splitbuddy.data.remote.invite.CreateInviteLinkRequest
import com.example.splitbuddy.data.remote.invite.InviteApiInterface

class CreateInviteLinkUseCase(
    private val inviteApiInterface: InviteApiInterface
) {
    suspend operator fun invoke(
        groupId: String,
        createdByUserId: String
    ): String {
        // Returns just the inviteUrl — that's all the screen needs
        val response = inviteApiInterface.createInviteLink(
            groupId = groupId,
            request = CreateInviteLinkRequest(createdByUserId = createdByUserId)
        )
        return response.inviteUrl
    }
}