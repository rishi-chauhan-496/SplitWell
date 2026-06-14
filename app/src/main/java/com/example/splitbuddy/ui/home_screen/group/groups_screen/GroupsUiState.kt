package com.example.splitbuddy.ui.home_screen.group.groups_screen

data class GroupsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val groups: List<GroupSummary> = emptyList(),
    val error: String? = null
)

data class GroupSummary(
    val id: String,
    val groupName: String,
    val totalMember: Int,
    val totalExpense: Int,
    val totalAmount: Double,
    val createdAt: String = ""
)