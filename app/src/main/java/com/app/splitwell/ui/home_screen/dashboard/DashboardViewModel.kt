package com.app.splitwell.ui.home_screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.splitwell.domain.usecase.dashboard.GetDashboardSummaryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getDashboardSummaryUseCase: GetDashboardSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    fun load(userId: String, isRefresh: Boolean = false, silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) {
                _uiState.update {
                    it.copy(isLoading = !isRefresh, isRefreshing = isRefresh)
                }
            }

            val summary = getDashboardSummaryUseCase(userId)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    userName = summary.displayName,
                    totalSpent = summary.totalSpent,
                    youAreOwed = summary.youAreOwed,
                    youOwe = summary.youOwe,
                    recentGroups = summary.recentGroups.map { g ->
                        DashboardGroup(
                            id = g.id,
                            groupName = g.groupName,
                            totalMember = g.totalMember,
                            totalExpense = g.totalExpense,
                            totalAmount = g.totalAmount,
                            createdAt = g.createdAt
                        )
                    },
                    recentExpenses = summary.recentExpenses.map { e ->
                        DashboardExpense(
                            id = e.id,
                            groupId = e.groupId,
                            title = e.title,
                            paidByName = e.paidByName,
                            amount = e.amount,
                            createdAt = e.createdAt
                        )
                    }
                )
            }
        }
    }

    // Silent background poll — same fetch, no spinner
    fun pollRefresh(userId: String) {
        load(userId, silent = true)
    }
}