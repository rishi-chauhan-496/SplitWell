package com.app.splitwell.domain.usecase.expense

import com.app.splitwell.data.local.model.Expense
import com.app.splitwell.data.util.Resource
import com.app.splitwell.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.StateFlow

class GetAllExpenseByGroupIdUseCase(
    private val repository: ExpenseRepository
) {
    fun observe(): StateFlow<Resource<List<Expense>>> = repository.expensesFlow
    suspend fun load(groupId: String): Resource<List<Expense>> = repository.getAllExpense(groupId)
}