package com.example.splitbuddy.domain.usecase.expense

import com.example.splitbuddy.data.local.model.Expense
import com.example.splitbuddy.data.remote.expense.ExpenseRequest
import com.example.splitbuddy.data.util.Resource
import com.example.splitbuddy.domain.repository.ExpenseRepository

class CreateExpenseUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(request: ExpenseRequest): Resource<Expense> =
        repository.createExpense(request)
}