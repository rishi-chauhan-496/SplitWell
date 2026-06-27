package com.app.splitwell.ui.home_screen.group.group_add_member_screen

import com.app.splitwell.domain.model.Friend

data class AddMemberUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val friends: List<Friend> = emptyList(),
    val existingMemberIds: Set<String> = emptySet(),
    val selectedUserIds: Set<String> = emptySet(),
    val error: String? = null,
    val isSaved: Boolean = false
)