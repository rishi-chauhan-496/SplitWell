package com.example.splitbuddy.data.remote.expense

import com.example.splitbuddy.data.remote.RetrofitInstance

class ExpenseApiInterfaceImpl: ExpenseApiInterface {

    private val api = RetrofitInstance.expenseApi

    override suspend fun createExpense(request: ExpenseRequest): ExpenseResponse {
        val result = api.createExpense(request)
        return result
    }

    override suspend fun getAllExpense(): List<ExpenseResponse> {
        val result = api.getAllExpense()
        return result
    }

    override suspend fun getExpenseById(id: String): ExpenseResponse {
        val result = api.getExpenseById(id)
        return result
    }

    override suspend fun updateExpense(id: String, request: ExpenseRequest): ExpenseResponse {
        val result = api.updateExpense(id, request)
        return result
    }

    override suspend fun deleteExpense(id: String): ExpenseResponse {
        val result = api.deleteExpense(id)
        return result
    }
}