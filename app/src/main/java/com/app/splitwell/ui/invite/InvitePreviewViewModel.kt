package com.app.splitwell.ui.invite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.splitwell.data.util.toAppError
import com.app.splitwell.data.util.toWriteMessage
import com.app.splitwell.domain.usecase.invite.AcceptInviteLinkUseCase
import com.app.splitwell.domain.usecase.invite.PreviewInviteLinkUseCase
import com.app.splitwell.ui.util.SnackbarController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InvitePreviewViewModel(
    private val previewInviteLinkUseCase: PreviewInviteLinkUseCase,
    private val acceptInviteLinkUseCase: AcceptInviteLinkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InvitePreviewUiState())
    val uiState: StateFlow<InvitePreviewUiState> = _uiState

    fun load(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val preview = previewInviteLinkUseCase(token)

                // Build creator display name
                val creatorName = preview.creator.firstName
                    .ifBlank { preview.creator.username }

                _uiState.update {
                    it.copy(
                        isLoading     = false,
                        groupId       = preview.group.id,
                        groupName     = preview.group.groupTitle,
                        memberCount   = preview.group.memberCount,
                        createdByName = creatorName,
                        expiresAt     = preview.expiresAt,
                        isExpired     = preview.isExpired || !preview.isActive
                    )
                }
            } catch (e: Exception) {
                Log.e("InvitePreview", "Failed to load invite for token=$token", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error     = "Invalid or expired invite link"
                    )
                }
            }
        }
    }

    fun acceptInvite(token: String, userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAccepting = true) }

            try {
                acceptInviteLinkUseCase(token, userId)
                // We already know the groupId from the preview load above —
                // no need to trust the accept response's shape just to navigate.
                _uiState.update {
                    it.copy(isAccepting = false, joinedGroupId = it.groupId)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isAccepting = false) }
                SnackbarController.show(e.toAppError().toWriteMessage())
            }
        }
    }
}