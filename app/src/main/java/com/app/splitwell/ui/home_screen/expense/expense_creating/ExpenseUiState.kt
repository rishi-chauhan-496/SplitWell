package com.app.splitwell.ui.home_screen.expense.expense_creating

import com.app.splitwell.data.local.model.TripManager

enum class SplitMethod { EQUAL, AMOUNT, PERCENT }

data class ExpenseUiState(

    val isLoading: Boolean = false,

    val title: String = "",
    val amount: String = "",
    val description: String = "",
    val paidByUserId: String? = null,
    val paidByUserName: String = "",

    val members: List<TripManager> = emptyList(),

    // Who is checked/included in the split
    // Empty means everyone is included by default
    val includedUserIds: Set<String> = emptySet(),

    val splitMethod: SplitMethod = SplitMethod.EQUAL,
    val splitAmounts: List<String> = emptyList(),
    val percentages: List<String> = emptyList(),

    val splitError: String? = null,
    val titleError: String? = null,
    val amountError: String? = null,
    val paidByError: String? = null,

    val error: String? = null
) {
    val isFormFilled: Boolean
        get() = title.isNotBlank() && amount.isNotBlank() && paidByUserId != null

    // Helper — is this specific user included?
    fun isIncluded(userId: String): Boolean {
        // If set is empty → everyone included by default
        return includedUserIds.isEmpty() || userId in includedUserIds
    }

    // How many people are currently included
    val includedCount: Int
        get() = if (includedUserIds.isEmpty()) members.size
        else includedUserIds.size
}