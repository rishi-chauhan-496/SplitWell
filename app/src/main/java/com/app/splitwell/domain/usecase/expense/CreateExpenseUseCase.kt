package com.app.splitwell.domain.usecase.expense

import com.app.splitwell.data.local.model.Expense
import com.app.splitwell.data.remote.expense.ExpenseRequest
import com.app.splitwell.data.util.Resource
import com.app.splitwell.domain.repository.ExpenseRepository

class CreateExpenseUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(request: ExpenseRequest): Resource<Expense> =
        repository.createExpense(request)
}