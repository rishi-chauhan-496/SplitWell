package com.example.splitbuddy.data.repository

import com.example.splitbuddy.data.local.model.Expense
import com.example.splitbuddy.data.local.query.ExpenseQuery
import com.example.splitbuddy.data.local.query.ExpenseShareQuery
import com.example.splitbuddy.data.local.query.UserQuery
import com.example.splitbuddy.data.remote.expense.ExpenseApiInterface
import com.example.splitbuddy.data.remote.expense.ExpenseRequest
import com.example.splitbuddy.data.remote.user.UserApiInterface
import com.example.splitbuddy.domain.repository.ExpenseRepository

class ExpenseRepositoryImpl(
    private val expenseApiInterface: ExpenseApiInterface,
    private val expenseQuery: ExpenseQuery,
    private val expenseShareQuery: ExpenseShareQuery,
    private val userQuery: UserQuery,
    private val userApiInterface: UserApiInterface
) : ExpenseRepository {

    override suspend fun getAllExpense(groupId: String): List<Expense> {

        val remoteData = expenseApiInterface.getAllExpense()

        remoteData.forEach { expenseResponse ->
            try {
                syncUserIfNeeded(expenseResponse.paidByUser)
                expenseQuery.insertExpense(expenseResponse)

                expenseResponse.shares.forEach { share ->
                    try {
                        syncUserIfNeeded(share.userId)
                        expenseShareQuery.insertExpenseShare(
                            share.copy(expenseId = expenseResponse.id)
                        )
                    } catch (_: Exception) { }
                }
            } catch (_: Exception) { }
        }

        return expenseQuery.getExpenseByTripId(groupId)
    }

    override suspend fun createExpense(request: ExpenseRequest): Expense {

        val response = expenseApiInterface.createExpense(request)

        syncUserIfNeeded(response.paidByUser)
        expenseQuery.insertExpense(response)

        response.shares.forEach {
            try {
                syncUserIfNeeded(it.userId)
                expenseShareQuery.insertExpenseShare(
                    it.copy(expenseId = response.id)
                )
            } catch (_: Exception) { }
        }

        return expenseQuery.getExpenseById(response.id)
            ?: throw Exception("Expense not found locally")
    }

    override suspend fun updateExpense(id: String, request: ExpenseRequest): Expense {

        val response = expenseApiInterface.updateExpense(id, request)

        syncUserIfNeeded(response.paidByUser)
        expenseQuery.insertExpense(response)

        response.shares.forEach {
            try {
                syncUserIfNeeded(it.userId)
                expenseShareQuery.insertExpenseShare(
                    it.copy(expenseId = response.id)
                )
            } catch (_: Exception) { }
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
    private suspend fun syncUserIfNeeded(userId: String) {
        try {
            val existing = userQuery.getUser(userId)
            if (existing == null) {
                val user = userApiInterface.getUserById(userId)
                userQuery.insertUser(user)
            }
        } catch (_: Exception) { }
    }
}