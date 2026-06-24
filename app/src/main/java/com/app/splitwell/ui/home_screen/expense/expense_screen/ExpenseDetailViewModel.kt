package com.app.splitwell.ui.home_screen.expense.expense_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.splitwell.domain.usecase.expense.GetExpenseByIdUseCase
import com.app.splitwell.domain.usecase.expense.GetExpenseSharesByExpenseIdUseCase
import com.app.splitwell.data.util.toAppError
import com.app.splitwell.data.util.toWriteMessage
import com.app.splitwell.domain.usecase.expense.DeleteExpenseUseCase
import com.app.splitwell.ui.util.SnackbarController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExpenseDetailViewModel(
    private val getExpenseByIdUseCase: GetExpenseByIdUseCase,
    private val getExpenseSharesByExpenseIdUseCase: GetExpenseSharesByExpenseIdUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ExpenseDetailUiState())
    val state: StateFlow<ExpenseDetailUiState> = _state

    fun load(expenseId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val expense = getExpenseByIdUseCase(expenseId)
                val shares = getExpenseSharesByExpenseIdUseCase(expenseId)
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