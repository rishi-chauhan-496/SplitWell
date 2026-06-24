package com.app.splitwell.data.repository

import com.app.splitwell.data.local.model.Expense
import com.app.splitwell.data.local.query.ExpenseQuery
import com.app.splitwell.data.local.query.ExpenseShareQuery
import com.app.splitwell.data.local.query.UserQuery
import com.app.splitwell.data.remote.expense.ExpenseApiInterface
import com.app.splitwell.data.remote.expense.ExpenseRequest
import com.app.splitwell.data.remote.user.UserApiInterface
import com.app.splitwell.data.util.Resource
import com.app.splitwell.data.util.toAppError
import com.app.splitwell.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ExpenseRepositoryImpl(
    private val expenseApiInterface: ExpenseApiInterface,
    private val expenseQuery: ExpenseQuery,
    private val expenseShareQuery: ExpenseShareQuery,
    private val userQuery: UserQuery,
    private val userApiInterface: UserApiInterface
) : ExpenseRepository {

    // ── Observable Flow ───────────────────────────────────────────────────────

    private val _expensesFlow = MutableStateFlow<Resource<List<Expense>>>(
        Resource.Success(emptyList())
    )
    override val expensesFlow: StateFlow<Resource<List<Expense>>> = _expensesFlow

    // Current groupId being observed — set when group screen opens
    private var currentGroupId: String = ""

    // ── Sync ──────────────────────────────────────────────────────────────────

    override suspend fun sync() {
        if (currentGroupId.isBlank()) return
        try {
            val remoteData = expenseApiInterface.getExpensesByGroupId(currentGroupId)

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

            val freshIds = remoteData.map { it.id }.toSet()
            val removedIds = expenseQuery.deleteMissing(currentGroupId, freshIds)
            removedIds.forEach { expenseShareQuery.deleteByExpenseId(it) }

            _expensesFlow.value = Resource.Success(
                expenseQuery.getExpenseByTripId(currentGroupId)
            )

        } catch (e: Exception) {
            _expensesFlow.value = Resource.Error(
                error = e.toAppError(),
                data  = expenseQuery.getExpenseByTripId(currentGroupId)
            )
        }
    }

    override suspend fun getAllExpense(groupId: String): Resource<List<Expense>> {
        currentGroupId = groupId

        try {
            val remoteData = expenseApiInterface.getExpensesByGroupId(groupId)

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

            val freshIds = remoteData.map { it.id }.toSet()
            val removedIds = expenseQuery.deleteMissing(groupId, freshIds)
            removedIds.forEach { expenseShareQuery.deleteByExpenseId(it) }

            val fresh = expenseQuery.getExpenseByTripId(groupId)
            _expensesFlow.value = Resource.Success(fresh)
            return Resource.Success(fresh)

        } catch (e: Exception) {
            val local = expenseQuery.getExpenseByTripId(groupId)
            _expensesFlow.value = Resource.Error(error = e.toAppError(), data = local)
            return Resource.Error(error = e.toAppError(), data = local)
        }
    }

    // ── Write operations ──────────────────────────────────────────────────────

    override suspend fun createExpense(request: ExpenseRequest): Resource<Expense> {
        return try {
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

            // Refresh flow
            _expensesFlow.value = Resource.Success(
                expenseQuery.getExpenseByTripId(currentGroupId)
            )

            Resource.Success(
                expenseQuery.getExpenseById(response.id)
                    ?: throw Exception("Expense not found locally")
            )
        } catch (e: Exception) {
            Resource.Error(error = e.toAppError())
        }
    }

    override suspend fun updateExpense(id: String, request: ExpenseRequest): Resource<Expense> {
        return try {
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

            _expensesFlow.value = Resource.Success(
                expenseQuery.getExpenseByTripId(currentGroupId)
            )

            Resource.Success(
                expenseQuery.getExpenseById(response.id)
                    ?: throw Exception("Expense not found locally")
            )
        } catch (e: Exception) {
            Resource.Error(error = e.toAppError())
        }
    }

    override suspend fun deleteExpense(id: String): Resource<Unit> {
        return try {
            expenseApiInterface.deleteExpense(id)

            val existing = expenseQuery.getExpenseById(id)
            if (existing != null) {
                expenseQuery.updateExpense(
                    existing.copy(
                        isDeleted = true,
                        updatedAt = System.currentTimeMillis().toString()
                    )
                )
            }

            _expensesFlow.value = Resource.Success(
                expenseQuery.getExpenseByTripId(currentGroupId)
            )

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(error = e.toAppError())
        }
    }

    // ── Private ───────────────────────────────────────────────────────────────

    private suspend fun syncUserIfNeeded(userId: String) {
        try {
            if (userQuery.getUser(userId) == null) {
                val user = userApiInterface.getUserById(userId)
                userQuery.insertUser(user)
            }
        } catch (_: Exception) { }
    }
}