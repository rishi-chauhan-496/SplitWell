package com.example.splitbuddy.ui.home_screen.group.group_creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.data.remote.group.CreateGroupRequest
import com.example.splitbuddy.domain.usecase.group.CreateGroupUseCase
import com.example.splitbuddy.domain.usecase.user.GetAllUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupCreationViewModel(
    private val createGroupUseCase: CreateGroupUseCase,
    private val getAllUserUseCase: GetAllUserUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(GroupCreationUiState())
    val uiState: StateFlow<GroupCreationUiState> = _uiState

    init {
        getAllUsers()
    }

    private fun getAllUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            runCatching { getAllUserUseCase() }
                .onSuccess { users ->
                    _uiState.update {
                        it.copy(
                            users = users,
                            isLoading = false
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { state ->
                        state.copy(
                            error = throwable.message,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onGroupNameChange(name: String) {
        _uiState.update { it.copy(groupName = name) }
    }

    fun onUserSelected(userId: String) {
        _uiState.update { state ->
            val newSet = state.selectedUserIds.toMutableSet()

            if (newSet.contains(userId)) newSet.remove(userId)
            else newSet.add(userId)

            state.copy(selectedUserIds = newSet)
        }
    }

    fun createGroup(managerId: String) {
        val state = _uiState.value

        viewModelScope.launch {
            val request = CreateGroupRequest(
                groupTitle = state.groupName,
                managerUserId = managerId,
                memberUserIds = state.selectedUserIds.toList()
            )

            runCatching { createGroupUseCase(request) }
                .onSuccess {
                    _uiState.update { it.copy(success = true) }
                }
                .onFailure { throwable ->
                    _uiState.update { state ->
                        state.copy(
                            error = throwable.message,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(success = false) }
    }
}