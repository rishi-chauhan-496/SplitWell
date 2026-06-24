package com.app.splitwell.domain.usecase.expense

import com.app.splitwell.data.local.model.ExpenseShare
import com.app.splitwell.data.local.query.ExpenseShareQuery
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