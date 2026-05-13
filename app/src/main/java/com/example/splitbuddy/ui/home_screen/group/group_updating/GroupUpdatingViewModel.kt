package com.example.splitbuddy.ui.home_screen.group.group_updating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.data.remote.group.UpdateGroupRequest
import com.example.splitbuddy.data.util.Resource
import com.example.splitbuddy.data.util.toWriteMessage
import com.example.splitbuddy.domain.usecase.group.DeleteGroupUseCase
import com.example.splitbuddy.domain.usecase.group.GetGroupUseCase
import com.example.splitbuddy.domain.usecase.group.UpdateGroupUseCase
import com.example.splitbuddy.ui.util.SnackbarController
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
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val group = getGroupUseCase(groupId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    groupName = group?.tripTitle ?: ""
                )
                isLoaded = true
            } catch (_: Exception) {
                // Load from local DB — read error not shown as Snackbar
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun update(groupId: String, groupName: String) {
        if (groupId.isBlank() || groupName.isBlank()) {
            viewModelScope.launch {
                SnackbarController.show("Group name cannot be empty")
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            when (val result = updateGroupUseCase(
                groupId,
                UpdateGroupRequest(groupTitle = groupName)  // ← explicit field name
            )) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isUpdated = true
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    SnackbarController.show(result.error.toWriteMessage())
                }
                else -> {}
            }
        }
    }

    fun delete(groupId: String) {
        if (groupId.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            when (val result = deleteGroupUseCase(groupId)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isDeleted = true
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    SnackbarController.show(result.error.toWriteMessage())
                }
                else -> {}
            }
        }
    }
}