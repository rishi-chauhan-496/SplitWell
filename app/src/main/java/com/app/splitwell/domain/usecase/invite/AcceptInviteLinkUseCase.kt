package com.app.splitwell.domain.usecase.invite

import com.app.splitwell.data.remote.invite.AcceptInviteRequest
import com.app.splitwell.data.remote.invite.InviteApiInterface

class AcceptInviteLinkUseCase(
    private val inviteApiInterface: InviteApiInterface
) {
    // We don't depend on the response body's shape here — the groupId
    // needed for navigation is already known from the preview call.
    // A call that doesn't throw means the invite was accepted.
    suspend operator fun invoke(token: String, userId: String) {
        inviteApiInterface.acceptInviteLink(
            token   = token,
            request = AcceptInviteRequest(userId = userId)
        )
    }
}