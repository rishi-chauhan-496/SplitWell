package com.example.splitbuddy.ui.home_screen.group.group_updating

import com.example.splitbuddy.data.local.model.TripManager

data class GroupUpdateUiState(
    val isLoading: Boolean = false,
    val groupName: String = "",
    val members: List<TripManager> = emptyList(),
    val selectedToRemove: Set<String> = emptySet(),
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false,
    val isMembersRemoved: Boolean = false
)