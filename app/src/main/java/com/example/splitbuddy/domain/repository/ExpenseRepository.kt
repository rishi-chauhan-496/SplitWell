package com.example.splitbuddy.domain.repository

import com.example.splitbuddy.data.local.model.Expense
import com.example.splitbuddy.data.remote.expense.ExpenseRequest
import com.example.splitbuddy.data.util.Resource
import kotlinx.coroutines.flow.StateFlow

interface ExpenseRepository {

    // ── Observable ────────────────────────────────────────────────────────────
    val expensesFlow: StateFlow<Resource<List<Expense>>>

    // ── Sync ──────────────────────────────────────────────────────────────────
    suspend fun sync()

    // ── Write operations ──────────────────────────────────────────────────────
    suspend fun getAllExpense(groupId: String): Resource<List<Expense>>
    suspend fun createExpense(request: ExpenseRequest): Resource<Expense>
    suspend fun updateExpense(id: String, request: ExpenseRequest): Resource<Expense>
    suspend fun deleteExpense(id: String): Resource<Unit>
}