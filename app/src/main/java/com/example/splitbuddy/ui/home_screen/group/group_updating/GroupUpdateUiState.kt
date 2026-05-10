package com.example.splitbuddy.ui.home_screen.group.group_updating

data class GroupUpdateUiState(
    val isLoading: Boolean = false,
    val groupName: String = "",
    val error: String? = null,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false
)