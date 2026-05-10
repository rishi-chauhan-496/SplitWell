package com.example.splitbuddy.ui.home_screen.group.group_add_member_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.data.remote.group.AddMembersRequest
import com.example.splitbuddy.data.remote.group.MemberItem
import com.example.splitbuddy.domain.usecase.group.AddMultipleMemberToGroupUseCase
import com.example.splitbuddy.domain.usecase.group.GetGroupMembersUseCase
import com.example.splitbuddy.domain.usecase.user.GetAllUserUseCase
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
                // Load all users and current group members at the same time
                val usersDeferred = async { getAllUserUseCase() }
                val membersDeferred = async { getGroupMembersUseCase(groupId) }

                val users = usersDeferred.await()
                val members = membersDeferred.await()

                // Build a set of userIds already in the group
                val existingIds = members.map { it.userId }.toSet()

                _state.update {
                    it.copy(
                        isLoading = false,
                        users = users,
                        existingMemberIds = existingIds
                    )
                }

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onUserToggle(userId: String) {
        val s = _state.value

        // Don't allow toggling existing members
        if (userId in s.existingMemberIds) return

        val updated = if (userId in s.selectedUserIds) {
            s.selectedUserIds - userId   // deselect
        } else {
            s.selectedUserIds + userId   // select
        }

        _state.update { it.copy(selectedUserIds = updated) }
    }

    fun addMembers(groupId: String) {
        val s = _state.value
        if (s.selectedUserIds.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            try {
                val request = AddMembersRequest(
                    members = s.selectedUserIds.map { MemberItem(userId = it) }
                )

                addMultipleMemberToGroupUseCase(groupId, request)

                _state.update { it.copy(isSaving = false, isSaved = true) }

            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }
}