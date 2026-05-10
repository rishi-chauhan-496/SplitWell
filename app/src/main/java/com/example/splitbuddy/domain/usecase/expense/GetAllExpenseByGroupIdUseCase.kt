package com.example.splitbuddy.domain.usecase.expense

import com.example.splitbuddy.data.local.model.Expense
import com.example.splitbuddy.domain.repository.ExpenseRepository

class GetAllExpenseByGroupIdUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(groupId: String): List<Expense> {
        return repository.getAllExpense(groupId)
    }
}