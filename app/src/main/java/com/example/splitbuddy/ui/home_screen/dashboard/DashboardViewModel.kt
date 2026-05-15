package com.example.splitbuddy.ui.home_screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.data.local.query.UserQuery
import com.example.splitbuddy.data.util.Resource
import com.example.splitbuddy.domain.usecase.expense.GetAllExpenseByGroupIdUseCase
import com.example.splitbuddy.domain.usecase.group.GetAllGroupsUseCase
import com.example.splitbuddy.domain.usecase.group.GetGroupMembersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val userQuery: UserQuery,
    private val getAllGroupsUseCase: GetAllGroupsUseCase,
    private val getGroupMembersUseCase: GetGroupMembersUseCase,
    private val getAllExpenseByGroupIdUseCase: GetAllExpenseByGroupIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    fun load(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // STEP 1: Load user's first name from local DB
            // userQuery.getUser() is the same call ProfileEditViewModel uses
            val user = userQuery.getUser(userId)
            val displayName = user?.firstName?.ifBlank { user.userName } ?: "there"

            // STEP 2: Load all groups — observe() gives a Flow, so we use
            // first { } to grab only the first non-Loading emission
            val groupsResource = getAllGroupsUseCase.observe()
                .first { it !is Resource.Loading }

            val allGroups = when (groupsResource) {
                is Resource.Success -> groupsResource.data
                is Resource.Error   -> groupsResource.data ?: emptyList()
                else                -> emptyList()
            }

            // STEP 3: For each group, load members + expenses
            // This builds a pair: (DashboardGroup info, list of expenses)
            val groupData = allGroups.map { trip ->
                val members  = getGroupMembersUseCase(trip.id)
                val expRes   = getAllExpenseByGroupIdUseCase.load(trip.id)
                val expenses = (expRes as? Resource.Success)?.data ?: emptyList()

                val group = DashboardGroup(
                    id           = trip.id,
                    groupName    = trip.tripTitle,
                    totalMember  = members.size,
                    totalExpense = expenses.size,
                    totalAmount  = expenses.sumOf { it.amount },
                    createdAt    = trip.createdAt
                )
                Pair(group, expenses)
            }

            // STEP 4: Take 2 most recent groups (sorted by date, newest first)
            val recentGroups = groupData
                .sortedByDescending { it.first.createdAt }
                .take(2)
                .map { it.first }

            // STEP 5: Take 3 most recent expenses across ALL groups
            val recentExpenses = groupData
                .flatMap { it.second }           // combine all expenses into one list
                .sortedByDescending { it.createdAt }
                .take(3)
                .map { expense ->
                    DashboardExpense(
                        title      = expense.title,
                        paidByName = expense.paidByUserName,
                        amount     = expense.amount,
                        createdAt  = expense.createdAt
                    )
                }

            // STEP 6: Total spent = sum of all expenses
            // youAreOwed and youOwe are 0.0 for now — we'll add real data later
            val totalSpent = groupData.flatMap { it.second }.sumOf { it.amount }

            _uiState.update {
                it.copy(
                    isLoading      = false,
                    userName       = displayName,
                    totalSpent     = totalSpent,
                    youAreOwed     = 0.0,
                    youOwe         = 0.0,
                    recentGroups   = recentGroups,
                    recentExpenses = recentExpenses
                )
            }
        }
    }
}