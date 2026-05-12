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
import kotlinx.coroutines.flow.update
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

    fun update(groupId: String, name: String) {
        viewModelScope.launch {
            when (val result = updateGroupUseCase(groupId, UpdateGroupRequest(groupTitle = name))) {
                is Resource.Success -> _uiState.update { it.copy(isUpdated = true) }
                is Resource.Error   -> SnackbarController.show(result.error.toWriteMessage())
                else -> {}
            }
        }
    }

    fun delete(groupId: String) {
        viewModelScope.launch {
            when (val result = deleteGroupUseCase(groupId)) {
                is Resource.Success -> _uiState.update { it.copy(isDeleted = true) }
                is Resource.Error   -> SnackbarController.show(result.error.toWriteMessage())
                else -> {}
            }
        }
    }
}