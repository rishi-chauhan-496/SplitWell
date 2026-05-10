package com.example.splitbuddy.domain.repository

import com.example.splitbuddy.data.local.model.Expense
import com.example.splitbuddy.data.remote.expense.ExpenseRequest


interface ExpenseRepository {

    suspend fun createExpense(request: ExpenseRequest): Expense
    suspend fun getAllExpense(groupId: String): List<Expense>
    suspend fun updateExpense(
        id: String,
        request: ExpenseRequest
    ): Expense
    suspend fun deleteExpense(id: String)
}