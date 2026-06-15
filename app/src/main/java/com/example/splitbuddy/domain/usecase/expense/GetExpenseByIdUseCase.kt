package com.example.splitbuddy.domain.usecase.expense

import com.example.splitbuddy.data.local.model.Expense
import com.example.splitbuddy.data.local.query.ExpenseQuery
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