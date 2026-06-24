package com.app.splitwell.ui.home_screen.expense.expense_creating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.splitwell.data.local.model.TripManager
import com.app.splitwell.data.remote.expense.ExpenseRequest
import com.app.splitwell.data.remote.expense.ShareRequest
import com.app.splitwell.data.util.Resource
import com.app.splitwell.data.util.toWriteMessage
import com.app.splitwell.domain.usecase.expense.CreateExpenseUseCase
import com.app.splitwell.domain.usecase.group.GetGroupMembersUseCase
import com.app.splitwell.ui.util.SnackbarController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt

class ExpenseViewModel(
    private val getGroupMembersUseCase: GetGroupMembersUseCase,
    private val createExpenseUseCase: CreateExpenseUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ExpenseUiState())
    val state: StateFlow<ExpenseUiState> = _state

    fun init(groupId: String) {
        viewModelScope.launch {
            val members = getGroupMembersUseCase(groupId)
            _state.update {
                it.copy(
                    members = members,
                    paidByUserId = members.firstOrNull()?.userId,
                    paidByUserName = members.firstOrNull()?.userName ?: "",
                    // Everyone included by default
                    includedUserIds = members.map { m -> m.userId }.toSet()
                )
            }
        }
    }

    // ── Screen 1 field updates ────────────────────────────────────────────────

    fun onTitleChange(value: String) {
        _state.update { it.copy(title = value, titleError = null) }
    }

    fun onAmountChange(value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
            _state.update { it.copy(amount = value, amountError = null) }
        }
    }

    fun onDescriptionChange(value: String) {
        _state.update { it.copy(description = value) }
    }

    fun onPaidByChange(userId: String) {
        val name = _state.value.members.find { it.userId == userId }?.userName ?: userId
        _state.update {
            it.copy(paidByUserId = userId, paidByUserName = name, paidByError = null)
        }
    }

    // ── Screen 2 — User inclusion toggle ─────────────────────────────────────

    fun onUserToggle(userId: String) {
        val s = _state.value

        val updated = if (userId in s.includedUserIds) {
            // Must keep at least 1 person included
            if (s.includedUserIds.size <= 1) return
            s.includedUserIds - userId
        } else {
            s.includedUserIds + userId
        }

        _state.update { it.copy(includedUserIds = updated) }

        // For equal split — recalculate immediately when inclusion changes
        if (_state.value.splitMethod == SplitMethod.EQUAL) {
            recalculateEqual()
        }
    }

    // ── Screen 2 — Split method tab switch ───────────────────────────────────

    fun onSplitMethodChange(method: SplitMethod) {
        val s = _state.value
        val total = s.amount.toDoubleOrNull() ?: 0.0
        val count = s.members.size
        if (count == 0) return

        val equalPercent = "%.2f".format(100.0 / s.includedCount.coerceAtLeast(1))

        when (method) {
            SplitMethod.AMOUNT -> {
                val amounts = s.splitAmounts.ifEmpty {
                    buildEqualAmounts(total, s.members, s.includedUserIds, s.paidByUserId)
                }

                _state.update {
                    it.copy(splitMethod = method, splitAmounts = amounts, splitError = null)
                }
            }

            SplitMethod.PERCENT -> {
                val percents = s.percentages.ifEmpty {
                    s.members.map { member ->
                        if (s.isIncluded(member.userId)) equalPercent else "0.00"
                    }
                }

                _state.update {
                    it.copy(splitMethod = method, percentages = percents, splitError = null)
                }
            }

            SplitMethod.EQUAL -> {
                _state.update { it.copy(splitMethod = method, splitError = null) }
                recalculateEqual()
            }
        }
    }

    // ── Screen 2 — Per-member edits ──────────────────────────────────────────

    fun onSplitAmountChange(index: Int, value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
            val updated = _state.value.splitAmounts.toMutableList()
            if (index < updated.size) {
                updated[index] = value
                _state.update { it.copy(splitAmounts = updated, splitError = null) }
            }
        }
    }

    fun onPercentChange(index: Int, value: String) {
        if (value.isEmpty() || value.matches(Regex("^\\d*\\.?\\d*$"))) {
            val updated = _state.value.percentages.toMutableList()
            if (index < updated.size) {
                updated[index] = value
                _state.update { it.copy(percentages = updated, splitError = null) }
            }
        }
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    fun goToSplitScreen(): Boolean {
        if (!validate()) return false

        val s = _state.value
        val total = s.amount.toDouble()
        if (s.members.isEmpty()) return false

        val amounts = buildEqualAmounts(total, s.members, s.includedUserIds, s.paidByUserId)
        _state.update { it.copy(splitAmounts = amounts) }
        return true
    }

    fun goToPreviewScreen(): Boolean {
        val s = _state.value
        val total = s.amount.toDoubleOrNull() ?: 0.0

        return when (s.splitMethod) {

            SplitMethod.EQUAL -> true

            SplitMethod.AMOUNT -> {
                // Only validate included members
                val assignedTotal = s.members.mapIndexed { index, member ->
                    if (s.isIncluded(member.userId))
                        s.splitAmounts.getOrElse(index) { "0.00" }.toDoubleOrNull() ?: 0.0
                    else 0.0
                }.sum()

                val diff = abs(assignedTotal - total)
                if (diff > 0.01) {
                    _state.update {
                        it.copy(
                            splitError = "Total assigned ₹${"%.2f".format(assignedTotal)} doesn't match ₹${"%.2f".format(total)}"
                        )
                    }
                    false
                } else {
                    _state.update { it.copy(splitError = null) }
                    true
                }
            }

            SplitMethod.PERCENT -> {
                // Only validate included members
                val totalPercent = s.members.mapIndexed { index, member ->
                    if (s.isIncluded(member.userId))
                        s.percentages.getOrElse(index) { "0.00" }.toDoubleOrNull() ?: 0.0
                    else 0.0
                }.sum()

                val diff = abs(totalPercent - 100.0)
                if (diff > 0.01) {
                    _state.update {
                        it.copy(
                            splitError = "Total percentage is ${"%.2f".format(totalPercent)}% — must equal 100%"
                        )
                    }
                    false
                } else {
                    // Convert percents → amounts only for included members
                    val computedAmounts = s.members.mapIndexed { index, member ->
                        if (s.isIncluded(member.userId)) {
                            val pct = s.percentages.getOrElse(index) { "0.00" }.toDoubleOrNull() ?: 0.0
                            "%.2f".format(pct / 100.0 * total)
                        } else "0.00"
                    }
                    _state.update { it.copy(splitAmounts = computedAmounts, splitError = null) }
                    true
                }
            }
        }
    }

    // ── Save ─────────────────────────────────────────────────────────────────

    fun saveExpense(groupId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val s = _state.value

            val shares = s.members.mapIndexed { index, member ->
                val included = s.isIncluded(member.userId)
                if (!included) return@mapIndexed null
                ShareRequest(
                    userId      = member.userId,
                    shareAmount = String.format(
                        java.util.Locale.US, "%.2f",
                        s.splitAmounts.getOrElse(index) { "0.00" }.toDoubleOrNull() ?: 0.0
                    ),
                    isIncluded   = true,
                    sharePercent = if (s.splitMethod == SplitMethod.PERCENT) {
                        String.format(
                            java.util.Locale.US, "%.2f",
                            s.percentages.getOrElse(index) { "0.00" }.toDoubleOrNull() ?: 0.0
                        )
                    } else null
                )
            }.filterNotNull()

            val request = ExpenseRequest(
                title = s.title,
                description = s.description.ifBlank { null },
                amount = "%.2f".format(s.amount.toDoubleOrNull() ?: 0.0),
                splitMethod = if (s.splitMethod == SplitMethod.PERCENT) "PERCENTAGE"
                else s.splitMethod.name,
                paidByUser = s.paidByUserId!!,
                groupId = groupId,
                shares = shares
            )

            when (val result = createExpenseUseCase(request)) {
                is Resource.Success -> _state.value = ExpenseUiState()
                is Resource.Error   -> {
                    _state.update { it.copy(isLoading = false) }
                    SnackbarController.show(result.error.toWriteMessage())
                }
                else -> { }
            }
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun recalculateEqual() {
        val s = _state.value
        val total = s.amount.toDoubleOrNull() ?: 0.0
        val amounts = buildEqualAmounts(total, s.members, s.includedUserIds, s.paidByUserId)
        _state.update { it.copy(splitAmounts = amounts) }
    }

    private fun buildEqualAmounts(
        total: Double,
        members: List<TripManager>,
        includedUserIds: Set<String>,
        payerUserId: String?
    ): List<String> {
        val includedCount = includedUserIds.size.coerceAtLeast(1)

        // Base amount per person — floored to avoid going over total
        val base = floor(total / includedCount * 100) / 100

        // How much is left after giving everyone the base amount
        val totalGiven = base * includedCount
        val remainder = ((total - totalGiven) * 100).roundToInt() / 100.0

        // Find payer's index to give them the remainder
        val payerIndex = members.indexOfFirst { it.userId == payerUserId }

        return members.mapIndexed { index, member ->
            when {
                member.userId !in includedUserIds -> "0.00"
                index == payerIndex -> "%.2f".format(base + remainder)
                else -> "%.2f".format(base)
            }
        }
    }

    private fun validate(): Boolean {
        val s = _state.value
        val titleError = if (s.title.isBlank()) "Title required" else null
        val amountError = when {
            s.amount.isBlank() -> "Amount required"
            s.amount.toDoubleOrNull() == null -> "Enter a valid number"
            s.amount.toDouble() <= 0 -> "Amount must be greater than 0"
            else -> null
        }
        val paidByError = if (s.paidByUserId == null) "Select who paid" else null

        _state.update {
            it.copy(
                titleError = titleError,
                amountError = amountError,
                paidByError = paidByError
            )
        }
        return titleError == null && amountError == null && paidByError == null
    }
}