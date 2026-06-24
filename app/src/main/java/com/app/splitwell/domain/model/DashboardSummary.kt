package com.app.splitwell.domain.model

data class DashboardSummary(
    val displayName: String?,
    val totalSpent: Double,
    val youOwe: Double,
    val youAreOwed: Double,
    val recentGroups: List<GroupSummary>,
    val recentExpenses: List<ExpenseSummary>
)

data class GroupSummary(
    val id: String,
    val groupName: String,
    val totalMember: Int,
    val totalExpense: Int,
    val totalAmount: Double,
    val createdAt: String
)

data class ExpenseSummary(
    val id: String,
    val groupId: String,
    val title: String,
    val paidByName: String,
    val amount: Double,
    val createdAt: String
)