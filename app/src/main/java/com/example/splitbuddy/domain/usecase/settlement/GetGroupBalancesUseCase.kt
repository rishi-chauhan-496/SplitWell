package com.example.splitbuddy.domain.usecase.settlement

import com.example.splitbuddy.data.local.query.ExpenseQuery
import com.example.splitbuddy.data.local.query.ExpenseShareQuery
import com.example.splitbuddy.domain.calculator.SettlementCalculator
import com.example.splitbuddy.domain.model.SettlementSuggestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetGroupBalancesUseCase(
    private val expenseQuery: ExpenseQuery,
    private val expenseShareQuery: ExpenseShareQuery,
    private val calculator: SettlementCalculator
) {

    suspend operator fun invoke(groupId: String): List<SettlementSuggestion> =
        withContext(Dispatchers.IO) {

            // Query 1 — all expenses for the group
            val expenses = expenseQuery.getExpenseByTripId(groupId)

            // Query 2 — ALL shares for the group in one shot (no loop!)
            val allShares = expenseShareQuery.getSharesByGroupId(groupId)

            // Group shares by expenseId in memory — zero DB calls
            val sharesByExpense = allShares.groupBy { it.expenseId }

            // Calculate + simplify
            val netBalances = calculator.calculateNetBalances(expenses, sharesByExpense)
            calculator.simplifyDebts(netBalances)
        }
}