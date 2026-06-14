package com.example.splitbuddy.ui.home_screen.friend

data class FriendListUiState(
    val isLoading: Boolean = false,
    val friends: List<FriendItem> = emptyList()
)

data class FriendItem(
    val id: String,
    val userName: String,
    val email: String,
    val displayName: String   // firstName + lastName, used for the avatar initial
)