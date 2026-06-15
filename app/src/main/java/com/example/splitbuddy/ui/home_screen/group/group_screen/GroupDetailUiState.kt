package com.example.splitbuddy.ui.home_screen.group.group_screen

import com.example.splitbuddy.data.local.model.Expense

data class GroupDetailUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val groupName: String = "",
    val memberCount: Int = 0,
    val expenses: List<Expense> = emptyList(),
    val totalAmount: Double = 0.0,
    val error: String? = null,
    val isGeneratingInvite: Boolean = false,   // ← loading state for invite button
    val inviteUrl: String? = null              // ← set when invite link is ready to share
)