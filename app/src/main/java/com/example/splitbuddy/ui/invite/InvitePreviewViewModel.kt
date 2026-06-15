package com.example.splitbuddy.ui.invite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.data.util.toAppError
import com.example.splitbuddy.data.util.toWriteMessage
import com.example.splitbuddy.domain.usecase.invite.AcceptInviteLinkUseCase
import com.example.splitbuddy.domain.usecase.invite.PreviewInviteLinkUseCase
import com.example.splitbuddy.ui.util.SnackbarController
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
                        groupName     = preview.group.groupTitle,
                        memberCount   = preview.group.memberCount,
                        createdByName = creatorName,
                        expiresAt     = preview.expiresAt,
                        isExpired     = preview.isExpired || !preview.isActive
                    )
                }
            } catch (e: Exception) {
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
                val groupId = acceptInviteLinkUseCase(token, userId)
                // Set joinedGroupId → screen observes this and navigates
                _uiState.update {
                    it.copy(isAccepting = false, joinedGroupId = groupId)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isAccepting = false) }
                SnackbarController.show(e.toAppError().toWriteMessage())
            }
        }
    }
}