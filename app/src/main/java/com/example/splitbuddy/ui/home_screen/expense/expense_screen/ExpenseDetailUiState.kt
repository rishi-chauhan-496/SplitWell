package com.example.splitbuddy.ui.home_screen.expense.expense_screen

import com.example.splitbuddy.data.local.model.Expense
import com.example.splitbuddy.data.local.model.ExpenseShare

data class ExpenseDetailUiState(
    val isLoading: Boolean = false,
    val expense: Expense? = null,
    val shares: List<ExpenseShare> = emptyList(),
    val isDeleted: Boolean = false,
    val error: String? = null
)
