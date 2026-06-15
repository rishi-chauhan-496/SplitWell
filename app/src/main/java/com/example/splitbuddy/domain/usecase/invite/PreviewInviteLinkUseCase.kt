package com.example.splitbuddy.domain.usecase.invite

import com.example.splitbuddy.data.remote.invite.InviteApiInterface
import com.example.splitbuddy.data.remote.invite.InvitePreviewResponse

class PreviewInviteLinkUseCase(
    private val inviteApiInterface: InviteApiInterface
) {
    suspend operator fun invoke(token: String): InvitePreviewResponse {
        return inviteApiInterface.previewInviteLink(token)
    }
}