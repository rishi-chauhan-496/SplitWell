package com.example.splitbuddy.ui.home_screen.group.group_updating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.data.remote.group.RemoveMembersRequest
import com.example.splitbuddy.data.remote.group.UpdateGroupRequest
import com.example.splitbuddy.data.util.Resource
import com.example.splitbuddy.data.util.toWriteMessage
import com.example.splitbuddy.domain.usecase.group.DeleteGroupUseCase
import com.example.splitbuddy.domain.usecase.group.GetGroupMembersUseCase
import com.example.splitbuddy.domain.usecase.group.GetGroupUseCase
import com.example.splitbuddy.domain.usecase.group.RemoveMembersFromGroupUseCase
import com.example.splitbuddy.domain.usecase.group.UpdateGroupUseCase
import com.example.splitbuddy.ui.util.SnackbarController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupUpdatingViewModel(
    private val getGroupUseCase: GetGroupUseCase,
    private val getGroupMembersUseCase: GetGroupMembersUseCase,
    private val updateGroupUseCase: UpdateGroupUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val removeMembersFromGroupUseCase: RemoveMembersFromGroupUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupUpdateUiState())
    val uiState: StateFlow<GroupUpdateUiState> = _uiState

    private var isLoaded = false

    // ── Field update ──────────────────────────────────────────────────────────

    fun onNameChange(name: String) {
        _uiState.update { it.copy(groupName = name) }
    }

    // ── Toggle member selection for removal ───────────────────────────────────

    fun onMemberToggle(userId: String) {
        val current = _uiState.value.selectedToRemove
        val updated = if (userId in current) current - userId else current + userId
        _uiState.update { it.copy(selectedToRemove = updated) }
    }

    // ── Load group info + members ─────────────────────────────────────────────

    fun load(groupId: String) {
        if (groupId.isBlank() || isLoaded) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val group   = getGroupUseCase(groupId)
                val members = getGroupMembersUseCase(groupId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        groupName = group?.tripTitle ?: "",
                        members   = members
                    )
                }
                isLoaded = true

            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                SnackbarController.show("Failed to load group")
            }
        }
    }

    // ── Update group name ─────────────────────────────────────────────────────

    fun update(groupId: String) {
        val groupName = _uiState.value.groupName

        if (groupId.isBlank() || groupName.isBlank()) {
            viewModelScope.launch {
                SnackbarController.show("Group name cannot be empty")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = updateGroupUseCase(
                groupId,
                UpdateGroupRequest(groupTitle = groupName)
            )) {
                is Resource.Success -> _uiState.update {
                    it.copy(isLoading = false, isUpdated = true)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    SnackbarController.show(result.error.toWriteMessage())
                }
                else -> {}
            }
        }
    }

    // ── Remove selected members ───────────────────────────────────────────────

    fun removeMembers(groupId: String) {
        val userIds = _uiState.value.selectedToRemove.toList()
        if (userIds.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = removeMembersFromGroupUseCase(
                groupId,
                RemoveMembersRequest(userIds = userIds)
            )) {
                is Resource.Success -> {
                    // Refresh members list after removal
                    val updatedMembers = getGroupMembersUseCase(groupId)
                    _uiState.update {
                        it.copy(
                            isLoading       = false,
                            members         = updatedMembers,
                            selectedToRemove = emptySet(),   // clear selection
                            isMembersRemoved = true
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    SnackbarController.show(result.error.toWriteMessage())
                }
                else -> {}
            }
        }
    }

    // ── Delete group ──────────────────────────────────────────────────────────

    fun delete(groupId: String) {
        if (groupId.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = deleteGroupUseCase(groupId)) {
                is Resource.Success -> _uiState.update {
                    it.copy(isLoading = false, isDeleted = true)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    SnackbarController.show(result.error.toWriteMessage())
                }
                else -> {}
            }
        }
    }
}