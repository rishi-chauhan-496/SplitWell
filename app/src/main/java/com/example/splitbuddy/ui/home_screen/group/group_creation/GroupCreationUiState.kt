package com.example.splitbuddy.ui.home_screen.group.group_creation

import com.example.splitbuddy.data.local.model.User

data class GroupCreationUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val selectedUserIds: Set<String> = emptySet(),
    val groupName: String = "",
    val error: String? = null,
    val success: Boolean = false
)