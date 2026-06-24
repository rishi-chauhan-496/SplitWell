package com.app.splitwell.ui.home_screen.group.group_creation

import com.app.splitwell.domain.model.Friend

data class GroupCreationUiState(
    val isLoading: Boolean = false,
    val friends: List<Friend> = emptyList(),
    val selectedUserIds: Set<String> = emptySet(),
    val groupName: String = "",
    val error: String? = null,
    val success: Boolean = false
)