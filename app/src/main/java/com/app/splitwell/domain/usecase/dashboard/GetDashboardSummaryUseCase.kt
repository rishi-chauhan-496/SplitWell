package com.app.splitwell.domain.usecase.dashboard

import com.app.splitwell.data.local.query.UserQuery
import com.app.splitwell.data.util.Resource
import com.app.splitwell.domain.model.DashboardSummary
import com.app.splitwell.domain.model.ExpenseSummary
import com.app.splitwell.domain.model.GroupSummary
import com.app.splitwell.domain.usecase.expense.GetAllExpenseByGroupIdUseCase
import com.app.splitwell.domain.usecase.group.GetAllGroupsUseCase
import com.app.splitwell.domain.usecase.group.GetGroupMembersUseCase
import com.app.splitwell.domain.usecase.settlement.GetGroupBalancesUseCase
import com.app.splitwell.domain.usecase.settlement.GetGroupSettlementsUseCase
import kotlinx.coroutines.flow.first

class GetDashboardSummaryUseCase(
    private val userQuery: UserQuery,
    private val getAllGroupsUseCase: GetAllGroupsUseCase,
    private val getGroupMembersUseCase: GetGroupMembersUseCase,
    private val getAllExpenseByGroupIdUseCase: GetAllExpenseByGroupIdUseCase,
    private val getGroupBalancesUseCase: GetGroupBalancesUseCase,
    private val getGroupSettlementsUseCase: GetGroupSettlementsUseCase
) {
    suspend operator fun invoke(userId: String): DashboardSummary {

        val user = userQuery.getUser(userId)
        val displayName = user?.firstName?.ifBlank { user.userName }?.takeIf { it.isNotBlank() }

        val groupsResource = getAllGroupsUseCase.observe()
            .first { it !is Resource.Loading }

        val allGroups = when (groupsResource) {
            is Resource.Success -> groupsResource.data
            is Resource.Error -> groupsResource.data ?: emptyList()
            else -> emptyList()
        }

        val groupData = allGroups.map { trip ->
            val members = getGroupMembersUseCase(trip.id)
            val expenses = when (val expRes = getAllExpenseByGroupIdUseCase.load(trip.id)) {
                is Resource.Success -> expRes.data
                is Resource.Error -> expRes.data ?: emptyList()
                else -> emptyList()
            }

            val group = GroupSummary(
                id = trip.id,
                groupName = trip.tripTitle,
                totalMember = members.size,
                totalExpense = expenses.size,
                totalAmount = expenses.sumOf { it.amount },
                createdAt = trip.createdAt
            )
            Pair(group, expenses)
        }

        val recentGroups = groupData
            .sortedByDescending { it.first.createdAt }
            .take(2)
            .map { it.first }

        val recentExpenses = groupData
            .flatMap { it.second }
            .sortedByDescending { it.createdAt }
            .take(3)
            .map { expense ->
            ExpenseSummary(
                id = expense.id,
                groupId = expense.tripId,
                title = expense.title,
                paidByName = expense.paidByUserName,
                amount = expense.amount,
                createdAt = expense.createdAt
            )
        }

        val totalSpent = groupData.flatMap { it.second }.sumOf { it.amount }

        var youOwe = 0.0
        var youAreOwed = 0.0

        for (group in allGroups) {
            val suggestions = getGroupBalancesUseCase(group.id)
            val settlements = getGroupSettlementsUseCase(group.id)
            val paidPairs = settlements
                .map { "${it.fromUserId}|${it.toUserId}" }
                .toSet()

            for (suggestion in suggestions) {
                if ("${suggestion.fromUserId}|${suggestion.toUserId}" in paidPairs) continue
                when (userId) {
                    suggestion.fromUserId -> youOwe += suggestion.amount
                    suggestion.toUserId -> youAreOwed += suggestion.amount
                }
            }
        }

        return DashboardSummary(
            displayName = displayName,
            totalSpent = totalSpent,
            youOwe = youOwe,
            youAreOwed = youAreOwed,
            recentGroups = recentGroups,
            recentExpenses = recentExpenses
        )
    }
}