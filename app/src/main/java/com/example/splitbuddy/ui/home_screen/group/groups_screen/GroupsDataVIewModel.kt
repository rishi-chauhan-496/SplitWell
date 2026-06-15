package com.example.splitbuddy.ui.home_screen.group.groups_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.data.local.model.Trip
import com.example.splitbuddy.data.util.AppError
import com.example.splitbuddy.data.util.Resource
import com.example.splitbuddy.data.util.toMessage
import com.example.splitbuddy.domain.usecase.expense.GetAllExpenseByGroupIdUseCase
import com.example.splitbuddy.domain.usecase.group.GetAllGroupsUseCase
import com.example.splitbuddy.domain.usecase.group.GetGroupMembersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class GroupsDataViewModel(
    private val getAllGroupsUseCase: GetAllGroupsUseCase,
    private val getGroupMembersUseCase: GetGroupMembersUseCase,
    private val getAllExpenseByGroupIdUseCase: GetAllExpenseByGroupIdUseCase
) : ViewModel() {

    private var isUserRefreshing = false
    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState: StateFlow<GroupsUiState> = _uiState

    fun init(userId: String) {
        // Show local data immediately
        viewModelScope.launch {
            getAllGroupsUseCase.observe().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        isUserRefreshing = false
                        val summaries = buildSummaries(resource.data)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                groups    = summaries,
                                error     = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        isUserRefreshing = false
                        val summaries = buildSummaries(resource.data ?: emptyList())
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                groups    = summaries,
                                error     = if (resource.error is AppError.NetworkError) null
                                else resource.error.toMessage()
                            )
                        }
                    }
                    is Resource.Loading -> {
                        if (!isUserRefreshing) {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }
            }
        }

        // Trigger initial sync
        viewModelScope.launch {
            getAllGroupsUseCase.sync(userId)
        }
    }

    fun refresh(userId: String) {
        isUserRefreshing = true
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            getAllGroupsUseCase.sync(userId)
            isUserRefreshing = false
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    private suspend fun buildSummaries(trips: List<Trip>): List<GroupSummary> {
        return trips.map { trip ->
            val members  = getGroupMembersUseCase(trip.id)
            val expenseList = when (val expenses = getAllExpenseByGroupIdUseCase.load(trip.id)) {
                is Resource.Success -> expenses.data ?: emptyList()
                is Resource.Error   -> expenses.data ?: emptyList()
                else                -> emptyList()
            }
            GroupSummary(
                id           = trip.id,
                groupName    = trip.tripTitle,
                totalMember  = members.size,
                totalExpense = expenseList.size,
                totalAmount  = expenseList.sumOf { it.amount },
                createdAt    = trip.createdAt
            )
        }
    }
}