package com.example.splitbuddy.data.repository

import com.example.splitbuddy.data.local.model.Expense
import com.example.splitbuddy.data.local.query.ExpenseQuery
import com.example.splitbuddy.data.local.query.ExpenseShareQuery
import com.example.splitbuddy.data.remote.expense.ExpenseApiInterface
import com.example.splitbuddy.data.remote.expense.ExpenseRequest
import com.example.splitbuddy.domain.repository.ExpenseRepository

class ExpenseRepositoryImpl(
    private val expenseApiInterface: ExpenseApiInterface,
    private val expenseQuery: ExpenseQuery,
    private val expenseShareQuery: ExpenseShareQuery
) : ExpenseRepository {

    override suspend fun getAllExpense(groupId: String): List<Expense> {

        val remoteData = expenseApiInterface.getAllExpense()

        remoteData.forEach { expenseResponse ->

            // Save expense
            expenseQuery.insertExpense(expenseResponse)

            // Save shares
            expenseResponse.shares.forEach { share ->
                val shareWithExpenseId = share.copy(
                    expenseId = expenseResponse.id
                )
                expenseShareQuery.insertExpenseShare(shareWithExpenseId)
            }
        }

        return expenseQuery.getExpenseByTripId(groupId)
    }

    override suspend fun createExpense(request: ExpenseRequest): Expense {

        val response = expenseApiInterface.createExpense(request)

        expenseQuery.insertExpense(response)

        response.shares.forEach {
            expenseShareQuery.insertExpenseShare(
                it.copy(expenseId = response.id)
            )
        }

        // Fetch from DB (since no mapper)
        return expenseQuery.getExpenseById(response.id)
            ?: throw Exception("Expense not found locally")
    }

    override suspend fun updateExpense(id: String, request: ExpenseRequest): Expense {

        val response = expenseApiInterface.updateExpense(id, request)

        expenseQuery.insertExpense(response)

        response.shares.forEach {
            expenseShareQuery.insertExpenseShare(
                it.copy(expenseId = response.id)
            )
        }

        return expenseQuery.getExpenseById(response.id)
            ?: throw Exception("Expense not found locally")
    }

    override suspend fun deleteExpense(id: String) {

        // Call backend (soft delete)
        expenseApiInterface.deleteExpense(id)

        // Get existing expense from DB
        val existing = expenseQuery.getExpenseById(id)
            ?: return

        // Mark as deleted locally
        val updatedExpense = existing.copy(
            isDeleted = true,
            updatedAt = System.currentTimeMillis().toString()
        )

        // Reuse update function
        expenseQuery.updateExpense(updatedExpense)
    }
}