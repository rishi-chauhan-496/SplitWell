package com.app.splitwell.ui.home_screen.group.group_add_member_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.splitwell.data.remote.group.AddMembersRequest
import com.app.splitwell.data.remote.group.MemberItem
import com.app.splitwell.data.util.Resource
import com.app.splitwell.data.util.toWriteMessage
import com.app.splitwell.domain.usecase.group.AddMultipleMemberToGroupUseCase
import com.app.splitwell.domain.usecase.group.GetGroupMembersUseCase
import com.app.splitwell.domain.usecase.user.GetAllUserUseCase
import com.app.splitwell.ui.util.SnackbarController
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddMemberViewModel(
    private val getAllUserUseCase: GetAllUserUseCase,
    private val getGroupMembersUseCase: GetGroupMembersUseCase,
    private val addMultipleMemberToGroupUseCase: AddMultipleMemberToGroupUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AddMemberUiState())
    val state: StateFlow<AddMemberUiState> = _state

    fun load(groupId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Run both in parallel
                val usersDeferred   = async { getAllUserUseCase() }
                val membersDeferred = async { getGroupMembersUseCase(groupId) }

                val users   = usersDeferred.await()
                val members = membersDeferred.await()

                val existingIds = members.map { it.userId }.toSet()

                _state.update {
                    it.copy(
                        isLoading         = false,
                        users             = users,
                        existingMemberIds = existingIds,
                        // If offline and local DB has no users — show message
                        error             = if (users.isEmpty()) "No users found. Connect to internet to load users." else null
                    )
                }

            } catch (_: Exception) {
                // getGroupMembersUseCase reads local DB — unlikely to fail
                // but handle gracefully
                _state.update {
                    it.copy(
                        isLoading = false,
                        error     = "Failed to load. Please try again."
                    )
                }
            }
        }
    }

    fun onUserToggle(userId: String) {
        val s = _state.value
        if (userId in s.existingMemberIds) return

        val updated = if (userId in s.selectedUserIds)
            s.selectedUserIds - userId
        else
            s.selectedUserIds + userId

        _state.update { it.copy(selectedUserIds = updated) }
    }

    fun addMembers(groupId: String) {
        val s = _state.value
        if (s.selectedUserIds.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            val request = AddMembersRequest(
                members = s.selectedUserIds.map { MemberItem(userId = it) }
            )

            when (val result = addMultipleMemberToGroupUseCase(groupId, request)) {
                is Resource.Success -> {
                    _state.update { it.copy(isSaving = false, isSaved = true) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isSaving = false) }
                    SnackbarController.show(result.error.toWriteMessage())
                }
                else -> {}
            }
        }
    }
}