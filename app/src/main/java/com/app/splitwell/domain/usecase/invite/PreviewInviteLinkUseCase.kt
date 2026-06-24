package com.app.splitwell.domain.usecase.invite

import com.app.splitwell.data.remote.invite.InviteApiInterface
import com.app.splitwell.data.remote.invite.InvitePreviewResponse

class PreviewInviteLinkUseCase(
    private val inviteApiInterface: InviteApiInterface
) {
    suspend operator fun invoke(token: String): InvitePreviewResponse {
        return inviteApiInterface.previewInviteLink(token)
    }
}