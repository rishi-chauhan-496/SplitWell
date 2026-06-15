package com.example.splitbuddy.domain.usecase.expense

import com.example.splitbuddy.data.local.model.ExpenseShare
import com.example.splitbuddy.data.local.query.ExpenseShareQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetExpenseSharesByExpenseIdUseCase(
    private val expenseShareQuery: ExpenseShareQuery
) {
    suspend operator fun invoke(expenseId: String): List<ExpenseShare> =
        withContext(Dispatchers.IO) {
            expenseShareQuery.getSharesByExpenseId(expenseId)
        }
}