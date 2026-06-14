package com.example.splitbuddy.ui.home_screen.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.data.local.query.UserQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendListViewModel(
    private val userQuery: UserQuery
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendListUiState())
    val uiState: StateFlow<FriendListUiState> = _uiState

    fun load(ownerID: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // DB read must happen on IO thread, not main thread
            val allUsers = withContext(Dispatchers.IO) {
                userQuery.getALLUser()
            }

            val friends = allUsers
                .filter { it.id != ownerID && !it.isDeleted }   // exclude self + deleted
                .map { user ->
                    FriendItem(
                        id          = user.id,
                        userName    = user.userName,
                        email       = user.email,
                        // If firstName is blank, fall back to userName for the avatar
                        displayName = user.firstName.ifBlank { user.userName }
                    )
                }

            _uiState.update {
                it.copy(isLoading = false, friends = friends)
            }
        }
    }
}