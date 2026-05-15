package com.example.splitbuddy.ui.home_screen.dashboard

data class DashboardUiState(
    val isLoading: Boolean = false,
    val userName: String = "",

    val totalSpent: Double = 0.0,
    val youAreOwed: Double = 0.0,
    val youOwe: Double = 0.0,

    val recentGroups: List<DashboardGroup> = emptyList(),

    val recentExpenses: List<DashboardExpense> = emptyList()
)

data class DashboardGroup(
    val id: String,
    val groupName: String,
    val totalMember: Int,
    val totalExpense: Int,
    val totalAmount: Double,
    val createdAt: String
)

data class DashboardExpense(
    val title: String,
    val paidByName: String,
    val amount: Double,
    val createdAt: String
)