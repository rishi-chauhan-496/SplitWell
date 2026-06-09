package com.example.splitbuddy.ui.home_screen.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.data.util.toAppError
import com.example.splitbuddy.data.util.toWriteMessage
import com.example.splitbuddy.domain.usecase.user.GetUserFriendsUseCase
import com.example.splitbuddy.ui.util.SnackbarController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FriendListViewModel(
    private val getUserFriendsUseCase: GetUserFriendsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendListUiState())
    val uiState: StateFlow<FriendListUiState> = _uiState

    fun load(ownerID: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val friends = getUserFriendsUseCase(ownerID)
                _uiState.update {
                    it.copy(isLoading = false, friends = friends)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                SnackbarController.show(e.toAppError().toWriteMessage())
            }
        }
    }
}