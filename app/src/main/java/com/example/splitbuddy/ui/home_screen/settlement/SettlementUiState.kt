package com.example.splitbuddy.ui.home_screen.settlement

data class SettlementUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isSaving: Boolean = false,
    val isSettlementRecorded: Boolean = false,
    val suggestions: List<SuggestionItem> = emptyList(),
    val confirmDialog: ConfirmDialogState? = null,
    val unsettleDialog: UnsettleDialogState? = null,
    val error: String? = null
)

data class SuggestionItem(
    val fromUserId: String,
    val fromName: String,
    val toUserId: String,
    val toName: String,
    val amount: Double,
    val isPaid: Boolean, // true = grayed out [Paid ✓], false = active [Mark Paid]
    val settlementId: String? = null
)

data class UnsettleDialogState(
    val settlementId: String,
    val fromName: String,
    val toName: String,
    val amount: Double
)

data class ConfirmDialogState(
    val fromUserId: String,
    val toUserId: String,
    val fromName: String,
    val toName: String,
    val amount: Double,
    val note: String = ""
)