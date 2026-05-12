package com.example.splitbuddy.ui.home_screen.group.group_screen

import com.example.splitbuddy.data.local.model.Expense

data class GroupDetailUiState(
    val isLoading: Boolean = false,
    val groupName: String = "",
    val memberCount: Int = 0,
    val expenses: List<Expense> = emptyList(),
    val totalAmount: Double = 0.0,
    val isOffline: Boolean = false,
    val error: String? = null
)