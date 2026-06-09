package com.example.splitbuddy.ui.home_screen.settlement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.data.local.query.SettlementQuery
import com.example.splitbuddy.data.local.query.UserQuery
import com.example.splitbuddy.data.remote.settlement.SettlementRequest
import com.example.splitbuddy.data.util.toAppError
import com.example.splitbuddy.data.util.toWriteMessage
import com.example.splitbuddy.domain.usecase.group.GetGroupMembersUseCase
import com.example.splitbuddy.domain.usecase.settlement.CreateSettlementUseCase
import com.example.splitbuddy.domain.usecase.settlement.GetGroupBalancesUseCase
import com.example.splitbuddy.ui.util.SnackbarController
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettlementViewModel(
    private val getGroupBalancesUseCase: GetGroupBalancesUseCase,
    private val createSettlementUseCase: CreateSettlementUseCase,
    private val getGroupMembersUseCase: GetGroupMembersUseCase,
    private val settlementQuery: SettlementQuery,
    private val userQuery: UserQuery
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettlementUiState())
    val uiState: StateFlow<SettlementUiState> = _uiState

    private var currentGroupId = ""

    fun load(groupId: String) {
        currentGroupId = groupId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Run all three in parallel
                val membersDeferred     = async { getGroupMembersUseCase(groupId) }
                val suggestionsDeferred = async { getGroupBalancesUseCase(groupId) }
                val settlementsDeferred = async { settlementQuery.getSettlementByTrip(groupId) }

                val members     = membersDeferred.await()
                val suggestions = suggestionsDeferred.await()
                val settlements = settlementsDeferred.await()

                // userId → display name from group members
                val nameMap = members.associate { m ->
                    m.userId to m.userName.ifBlank { m.userId }
                }

                // Build a set of already-paid pairs for quick lookup
                // Key = "fromUserId|toUserId"
                val paidPairs = settlements
                    .filter { !it.isDeleted }
                    .map    { "${it.fromUserId}|${it.toUserId}" }
                    .toSet()

                // Map suggestions → SuggestionItem, mark isPaid from settlement table
                val items = suggestions.map { s ->
                    val fromName = nameMap[s.fromUserId]
                        ?: userQuery.getUser(s.fromUserId)?.userName
                        ?: s.fromUserId
                    val toName = nameMap[s.toUserId]
                        ?: userQuery.getUser(s.toUserId)?.userName
                        ?: s.toUserId

                    SuggestionItem(
                        fromUserId = s.fromUserId,
                        fromName   = fromName,
                        toUserId   = s.toUserId,
                        toName     = toName,
                        amount     = s.amount,
                        isPaid     = "${s.fromUserId}|${s.toUserId}" in paidPairs
                    )
                }

                _uiState.update {
                    it.copy(isLoading = false, suggestions = items)
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load")
                }
            }
        }
    }

    fun onMarkPaidClick(item: SuggestionItem) {
        _uiState.update {
            it.copy(
                confirmDialog = ConfirmDialogState(
                    fromUserId = item.fromUserId,
                    toUserId   = item.toUserId,
                    fromName   = item.fromName,
                    toName     = item.toName,
                    amount     = item.amount
                )
            )
        }
    }

    fun onNoteChange(note: String) {
        _uiState.update { state ->
            state.copy(confirmDialog = state.confirmDialog?.copy(note = note))
        }
    }

    fun onDismissDialog() {
        _uiState.update { it.copy(confirmDialog = null) }
    }

    fun onConfirmSettlement() {
        val dialog = _uiState.value.confirmDialog ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            try {
                createSettlementUseCase(
                    SettlementRequest(
                        groupId    = currentGroupId,
                        fromUserId = dialog.fromUserId,
                        toUserId   = dialog.toUserId,
                        // API expects String e.g. "100.00"
                        amount     = "%.2f".format(dialog.amount),
                        note       = dialog.note.ifBlank { null }
                    )
                )
                _uiState.update { it.copy(isSaving = false, confirmDialog = null, isSettlementRecorded = true) }
                load(currentGroupId)

            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
                SnackbarController.show(e.toAppError().toWriteMessage())
            }
        }
    }
}