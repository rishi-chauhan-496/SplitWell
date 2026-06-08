package com.example.splitbuddy.domain.usecase.expense

import com.example.splitbuddy.data.local.model.Expense
import com.example.splitbuddy.data.util.Resource
import com.example.splitbuddy.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.StateFlow

class GetAllExpenseByGroupIdUseCase(
    private val repository: ExpenseRepository
) {
    fun observe(): StateFlow<Resource<List<Expense>>> = repository.expensesFlow
    suspend fun load(groupId: String): Resource<List<Expense>> = repository.getAllExpense(groupId)
}