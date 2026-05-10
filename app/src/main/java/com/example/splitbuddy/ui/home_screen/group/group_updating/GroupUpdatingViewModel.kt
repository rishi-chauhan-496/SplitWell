package com.example.splitbuddy.ui.home_screen.group.group_updating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.data.remote.group.UpdateGroupRequest
import com.example.splitbuddy.domain.usecase.group.DeleteGroupUseCase
import com.example.splitbuddy.domain.usecase.group.GetGroupUseCase
import com.example.splitbuddy.domain.usecase.group.UpdateGroupUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GroupUpdatingViewModel(
    private val getGroupUseCase: GetGroupUseCase,
    private val updateGroupUseCase: UpdateGroupUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupUpdateUiState())
    val uiState: StateFlow<GroupUpdateUiState> = _uiState

    private var isLoaded = false

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(groupName = name)
    }

    fun load(groupId: String) {
        if (groupId.isBlank() || isLoaded) return

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val group = getGroupUseCase(groupId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    groupName = group?.tripTitle ?: ""
                )

                isLoaded = true

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Something went wrong"
                )
            }
        }
    }

    fun update(groupId: String, groupName: String) {

        if (groupId.isBlank() || groupName.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Group name cannot be empty"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                updateGroupUseCase(groupId, UpdateGroupRequest(groupName))

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isUpdated = true
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Update failed"
                )
            }
        }
    }

    fun delete(groupId: String) {
        if (groupId.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                deleteGroupUseCase(groupId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isDeleted = true
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Delete failed"
                )
            }
        }
    }
}