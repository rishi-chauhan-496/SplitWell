package com.example.splitbuddy.domain.usecase.expense

import com.example.splitbuddy.data.local.model.Expense
import com.example.splitbuddy.data.remote.expense.ExpenseRequest
import com.example.splitbuddy.domain.repository.ExpenseRepository

class UpdateExpenseUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(id: String, request: ExpenseRequest): Expense {
        return repository.updateExpense(id, request)
    }
}