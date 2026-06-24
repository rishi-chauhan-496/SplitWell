package com.app.splitwell.ui.home_screen.group.group_creation

import com.app.splitwell.data.local.model.User

data class GroupCreationUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val selectedUserIds: Set<String> = emptySet(),
    val groupName: String = "",
    val error: String? = null,
    val success: Boolean = false
)