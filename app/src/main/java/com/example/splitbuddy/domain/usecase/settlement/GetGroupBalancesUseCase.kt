package com.example.splitbuddy.domain.usecase.settlement

import com.example.splitbuddy.data.local.query.ExpenseQuery
import com.example.splitbuddy.data.local.query.ExpenseShareQuery
import com.example.splitbuddy.domain.calculator.SettlementCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetGroupBalancesUseCase(
    private val expenseQuery: ExpenseQuery,
    private val expenseShareQuery: ExpenseShareQuery,
    private val calculator: SettlementCalculator
) {

    suspend operator fun invoke(groupId: String): List<SettlementSuggestion> =
        withContext(Dispatchers.IO) {

            // 1. Load all expenses for the group
            val expenses = expenseQuery.getExpenseByTripId(groupId)

            // 2. Load shares for every expense — grouped by expenseId
            val sharesByExpense = expenses.associate { expense ->
                expense.id to expenseShareQuery.getSharesByExpenseId(expense.id)
            }

            // 3. Calculate net balances
            val netBalances = calculator.calculateNetBalances(expenses, sharesByExpense)

            // 4. Simplify into minimum transactions
            calculator.simplifyDebts(netBalances)
        }
}