package com.example.splitbuddy.domain.usecase.expense

import com.example.splitbuddy.data.util.Resource
import com.example.splitbuddy.domain.repository.ExpenseRepository

class DeleteExpenseUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit> =
        repository.deleteExpense(id)
}