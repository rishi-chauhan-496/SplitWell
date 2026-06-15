package com.example.splitbuddy.ui.home_screen.settlement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.domain.usecase.user.GetUserByIdUseCase
import com.example.splitbuddy.data.remote.settlement.SettlementRequest
import com.example.splitbuddy.data.util.toAppError
import com.example.splitbuddy.data.util.toWriteMessage
import com.example.splitbuddy.domain.usecase.group.GetGroupMembersUseCase
import com.example.splitbuddy.domain.usecase.settlement.CreateSettlementUseCase
import com.example.splitbuddy.domain.usecase.settlement.DeleteSettlementUseCase
import com.example.splitbuddy.domain.usecase.settlement.GetGroupBalancesUseCase
import com.example.splitbuddy.domain.usecase.settlement.GetGroupSettlementsUseCase
import com.example.splitbuddy.ui.util.SnackbarController
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettlementViewModel(
    private val getGroupBalancesUseCase: GetGroupBalancesUseCase,
    private val createSettlementUseCase: CreateSettlementUseCase,
    private val deleteSettlementUseCase: DeleteSettlementUseCase,
    private val getGroupMembersUseCase: GetGroupMembersUseCase,
    private val getGroupSettlementsUseCase: GetGroupSettlementsUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettlementUiState())
    val uiState: StateFlow<SettlementUiState> = _uiState

    private var currentGroupId = ""

    fun load(groupId: String, isRefresh: Boolean = false) {
        currentGroupId = groupId
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading    = !isRefresh,
                    isRefreshing = isRefresh
                )
            }
            try {
                val membersDeferred     = async { getGroupMembersUseCase(groupId) }
                val suggestionsDeferred = async { getGroupBalancesUseCase(groupId) }
                val settlementsDeferred = async { getGroupSettlementsUseCase(groupId) }

                val members     = membersDeferred.await()
                val suggestions = suggestionsDeferred.await()
                val settlements = settlementsDeferred.await()

                val nameMap = members.associate { m ->
                    m.userId to m.userName.ifBlank { m.userId }
                }

                val paidPairsMap = settlements
                    .associate { "${it.fromUserId}|${it.toUserId}" to it.id }

                val items = suggestions.map { s ->
                    val fromName = nameMap[s.fromUserId]
                        ?: getUserByIdUseCase(s.fromUserId) ?: s.fromUserId
                    val toName = nameMap[s.toUserId]
                        ?: getUserByIdUseCase(s.toUserId) ?: s.toUserId

                    val pairKey      = "${s.fromUserId}|${s.toUserId}"
                    val settlementId = paidPairsMap[pairKey]

                    SuggestionItem(
                        fromUserId   = s.fromUserId,
                        fromName     = fromName,
                        toUserId     = s.toUserId,
                        toName       = toName,
                        amount       = s.amount,
                        isPaid       = settlementId != null,
                        settlementId = settlementId
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading    = false,
                        isRefreshing = false,
                        suggestions  = items
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
                SnackbarController.show(e.toAppError().toWriteMessage())
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
                _uiState.update {
                    it.copy(isSaving = false, confirmDialog = null, isSettlementRecorded = true)
                }
                _uiState.update { it.copy(isSettlementRecorded = false) }
                load(currentGroupId)

            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
                SnackbarController.show(e.toAppError().toWriteMessage())
            }
        }
    }

    // User taps [Paid ✓] — open unsettle confirmation dialog
    fun onUnsettleClick(item: SuggestionItem) {
        val settlementId = item.settlementId ?: return
        _uiState.update {
            it.copy(
                unsettleDialog = UnsettleDialogState(
                    settlementId = settlementId,
                    fromName     = item.fromName,
                    toName       = item.toName,
                    amount       = item.amount
                )
            )
        }
    }

    fun onDismissUnsettleDialog() {
        _uiState.update { it.copy(unsettleDialog = null) }
    }

    fun onConfirmUnsettle() {
        val dialog = _uiState.value.unsettleDialog ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            try {
                deleteSettlementUseCase(dialog.settlementId)
                _uiState.update { it.copy(isSaving = false, unsettleDialog = null) }
                load(currentGroupId)    // reload — card goes back to [Mark Paid]

            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
                SnackbarController.show(e.toAppError().toWriteMessage())
            }
        }
    }
}