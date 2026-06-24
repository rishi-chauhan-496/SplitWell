package com.app.splitwell.ui.home_screen.group.group_add_member_screen

import com.app.splitwell.data.local.model.User

data class AddMemberUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val users: List<User> = emptyList(),
    val existingMemberIds: Set<String> = emptySet(),
    val selectedUserIds: Set<String> = emptySet(),
    val error: String? = null,
    val isSaved: Boolean = false
)