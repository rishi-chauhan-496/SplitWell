package com.example.splitbuddy.domain.usecase.invite

import com.example.splitbuddy.data.remote.invite.AcceptInviteRequest
import com.example.splitbuddy.data.remote.invite.InviteApiInterface

class AcceptInviteLinkUseCase(
    private val inviteApiInterface: InviteApiInterface
) {
    // Returns groupId so screen can navigate to GroupScreen after joining
    suspend operator fun invoke(token: String, userId: String): String {
        val response = inviteApiInterface.acceptInviteLink(
            token   = token,
            request = AcceptInviteRequest(userId = userId)
        )
        return response.groupId
    }
}