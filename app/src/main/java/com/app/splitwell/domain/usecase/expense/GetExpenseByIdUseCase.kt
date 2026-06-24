package com.app.splitwell.domain.usecase.expense

import com.app.splitwell.data.local.model.Expense
import com.app.splitwell.data.local.query.ExpenseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetExpenseByIdUseCase(
    private val expenseQuery: ExpenseQuery
) {
    suspend operator fun invoke(expenseId: String): Expense? =
        withContext(Dispatchers.IO) {
            expenseQuery.getExpenseById(expenseId)
        }
}