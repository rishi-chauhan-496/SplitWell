package com.app.splitwell.domain.usecase.expense

import com.app.splitwell.data.util.Resource
import com.app.splitwell.domain.repository.ExpenseRepository

class DeleteExpenseUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit> =
        repository.deleteExpense(id)
}