package com.example.splitbuddy.data.remote.expense

interface ExpenseApiInterface {

    suspend fun createExpense(request: ExpenseRequest): ExpenseResponse

    suspend fun getAllExpense(): List<ExpenseResponse>

    suspend fun getExpenseById(id: String): ExpenseResponse

    suspend fun updateExpense(id: String, request: ExpenseRequest): ExpenseResponse

    suspend fun deleteExpense(id: String): ExpenseResponse
}