package com.example.splitbuddy.ui.home_screen.expense.expense_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.data.local.query.ExpenseQuery
import com.example.splitbuddy.data.local.query.ExpenseShareQuery
import com.example.splitbuddy.data.util.toAppError
import com.example.splitbuddy.data.util.toWriteMessage
import com.example.splitbuddy.domain.usecase.expense.DeleteExpenseUseCase
import com.example.splitbuddy.ui.util.SnackbarController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExpenseDetailViewModel(
    private val expenseQuery: ExpenseQuery,
    private val expenseShareQuery: ExpenseShareQuery,
    private val deleteExpenseUseCase: DeleteExpenseUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ExpenseDetailUiState())
    val state: StateFlow<ExpenseDetailUiState> = _state

    fun load(expenseId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val expense = expenseQuery.getExpenseById(expenseId)
                val shares = expenseShareQuery.getSharesByExpenseId(expenseId)
                _state.update {
                    it.copy(isLoading = false, expense = expense, shares = shares)
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                SnackbarController.show(e.toAppError().toWriteMessage())
            }
        }
    }

    fun delete(expenseId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                deleteExpenseUseCase(expenseId)
                _state.update { it.copy(isLoading = false, isDeleted = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                SnackbarController.show(e.toAppError().toWriteMessage())
            }
        }
    }
}