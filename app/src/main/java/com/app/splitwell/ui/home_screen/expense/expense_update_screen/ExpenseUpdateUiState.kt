package com.app.splitwell.ui.home_screen.expense.expense_update_screen

import com.app.splitwell.data.local.model.TripManager
import com.app.splitwell.ui.home_screen.expense.expense_creating.SplitMethod

data class ExpenseUpdateUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,

    // Basic fields
    val title: String = "",
    val amount: String = "",
    val description: String = "",
    val paidByUserId: String = "",
    val paidByUserName: String = "",

    // Members for dropdown + split cards
    val members: List<TripManager> = emptyList(),

    // Split
    val splitMethod: SplitMethod = SplitMethod.EQUAL,
    val splitAmounts: List<String> = emptyList(),
    val percentages: List<String> = emptyList(),
    val includedUserIds: Set<String> = emptySet(),

    // Errors
    val titleError: String? = null,
    val amountError: String? = null,
    val splitError: String? = null,

    val isUpdated: Boolean = false,

    val error: String? = null
) {
    fun isIncluded(userId: String): Boolean =
        includedUserIds.isEmpty() || userId in includedUserIds

    val includedCount: Int
        get() = if (includedUserIds.isEmpty()) members.size
        else includedUserIds.size
}