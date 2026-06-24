package com.app.splitwell.ui.home_screen.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.splitwell.data.util.toAppError
import com.app.splitwell.data.util.toWriteMessage
import com.app.splitwell.domain.usecase.user.GetUserFriendsUseCase
import com.app.splitwell.ui.util.SnackbarController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FriendListViewModel(
    private val getUserFriendsUseCase: GetUserFriendsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendListUiState())
    val uiState: StateFlow<FriendListUiState> = _uiState

    fun load(ownerID: String, isRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading    = !isRefresh,
                    isRefreshing = isRefresh
                )
            }
            try {
                val friends = getUserFriendsUseCase(ownerID)
                    .map { friend ->
                        FriendItem(               // mapping domain → UI happens here
                            id          = friend.id,
                            userName    = friend.userName,
                            email       = friend.email,
                            displayName = friend.displayName
                        )
                    }
                _uiState.update {
                    it.copy(
                        isLoading    = false,
                        isRefreshing = false,
                        friends      = friends
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
                SnackbarController.show(e.toAppError().toWriteMessage())
            }
        }
    }
}