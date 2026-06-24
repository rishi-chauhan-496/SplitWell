package com.app.splitwell.ui.home_screen.expense.expense_update_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.splitwell.domain.usecase.expense.GetExpenseByIdUseCase
import com.app.splitwell.domain.usecase.expense.GetExpenseSharesByExpenseIdUseCase
import com.app.splitwell.data.remote.expense.ExpenseRequest
import com.app.splitwell.data.remote.expense.ShareRequest
import com.app.splitwell.data.util.toAppError
import com.app.splitwell.data.util.toWriteMessage
import com.app.splitwell.domain.usecase.expense.UpdateExpenseUseCase
import com.app.splitwell.domain.usecase.group.GetGroupMembersUseCase
import com.app.splitwell.ui.home_screen.expense.expense_creating.SplitMethod
import com.app.splitwell.ui.util.SnackbarController
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt

class ExpenseUpdateViewModel(
    private val getExpenseByIdUseCase: GetExpenseByIdUseCase,
    private val getExpenseSharesByExpenseIdUseCase: GetExpenseSharesByExpenseIdUseCase,
    private val getGroupMembersUseCase: GetGroupMembersUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ExpenseUpdateUiState())
    val state: StateFlow<ExpenseUpdateUiState> = _state

    fun load(expenseId: String, groupId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val expenseDeferred = async { getExpenseByIdUseCase(expenseId) }
                val sharesDeferred  = async { getExpenseSharesByExpenseIdUseCase(expenseId) }
                val membersDeferred = async { getGroupMembersUseCase(groupId) }

                val expense = expenseDeferred.await()
                val shares  = sharesDeferred.await()
                val members = membersDeferred.await()

                if (expense == null) {
                    _state.update { it.copy(isLoading = false, error = "Expense not found") }
                    return@launch
                }

                // Pre-fill split data from existing shares
                val splitMethod = when (expense.splitMethod) {
                    "PERCENTAGE" -> SplitMethod.PERCENT
                    "AMOUNT"     -> SplitMethod.AMOUNT
                    else         -> SplitMethod.EQUAL
                }

                // Map share amounts/percents to match member order
                val splitAmounts = members.map { member ->
                    val share = shares.find { it.userId == member.userId }
                    "%.2f".format(share?.sharedAmount ?: 0.0)
                }

                val percentages = members.map { member ->
                    val share = shares.find { it.userId == member.userId }
                    "%.2f".format(share?.sharedPercent ?: 0.0)
                }

                val includedUserIds = shares
                    .filter { it.isIncluded }
                    .map { it.userId }
                    .toSet()

                _state.update {
                    it.copy(
                        isLoading      = false,
                        title          = expense.title,
                        amount         = expense.amount.toString(),
                        description    = expense.description ?: "",
                        paidByUserId   = expense.paidByUser,
                        paidByUserName = expense.paidByUserName,
                        members        = members,
                        splitMethod    = splitMethod,
                        splitAmounts   = splitAmounts,
                        percentages    = percentages,
                        includedUserIds = includedUserIds
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // ── Basic field updates ───────────────────────────────────────────────────

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
        _state.update { it.copy(paidByUserId = userId, paidByUserName = name) }
    }

    // ── Split method ──────────────────────────────────────────────────────────

    fun onSplitMethodChange(method: SplitMethod) {
        val s = _state.value
        s.amount.toDoubleOrNull() ?: 0.0

        when (method) {
            SplitMethod.EQUAL -> {
                _state.update { it.copy(splitMethod = method, splitError = null) }
                recalculateEqual()
            }
            SplitMethod.AMOUNT -> {
                _state.update { it.copy(splitMethod = method, splitError = null) }
            }
            SplitMethod.PERCENT -> {
                val equalPct = "%.2f".format(100.0 / s.includedCount.coerceAtLeast(1))
                val percents = if (s.percentages.all { it == "0.00" }) {
                    s.members.map { m ->
                        if (s.isIncluded(m.userId)) equalPct else "0.00"
                    }
                } else s.percentages
                _state.update {
                    it.copy(splitMethod = method, percentages = percents, splitError = null)
                }
            }
        }
    }

    fun onUserToggle(userId: String) {
        val s = _state.value
        if (s.includedUserIds.size <= 1 && userId in s.includedUserIds) return
        val updated = if (userId in s.includedUserIds)
            s.includedUserIds - userId else s.includedUserIds + userId
        _state.update { it.copy(includedUserIds = updated) }
        if (_state.value.splitMethod == SplitMethod.EQUAL) recalculateEqual()
    }

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

    // ── Save ─────────────────────────────────────────────────────────────────

    fun update(expenseId: String, groupId: String) {

        val s = _state.value

        val titleError = if (s.title.isBlank()) "Title required" else null
        val amountError = when {
            s.amount.isBlank()                -> "Amount required"
            s.amount.toDoubleOrNull() == null -> "Enter a valid number"
            s.amount.toDouble() <= 0          -> "Must be greater than 0"
            else                              -> null
        }

        if (titleError != null || amountError != null) {
            _state.update { it.copy(titleError = titleError, amountError = amountError) }
            return
        }

        if (!validateSplit()) return

        // ✅ Read FRESH state after validateSplit() may have updated splitAmounts
        val fresh = _state.value

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                val total = fresh.amount.toDoubleOrNull() ?: 0.0

                // Only send included members — excluded ones must not be sent (API requires positive amounts)
                val shares = fresh.members.mapIndexed { index, member ->
                    val included = fresh.isIncluded(member.userId)
                    if (!included) return@mapIndexed null
                    ShareRequest(
                        userId      = member.userId,
                        shareAmount = String.format(
                            java.util.Locale.US, "%.2f",
                            fresh.splitAmounts.getOrElse(index) { "0.00" }.toDoubleOrNull() ?: 0.0
                        ),
                        isIncluded   = true,
                        sharePercent = if (fresh.splitMethod == SplitMethod.PERCENT) {
                            String.format(
                                java.util.Locale.US, "%.2f",
                                fresh.percentages.getOrElse(index) { "0.00" }.toDoubleOrNull() ?: 0.0
                            )
                        } else null
                    )
                }.filterNotNull()  // removes null (excluded) entries

                val request = ExpenseRequest(
                    title       = fresh.title,
                    description = fresh.description.ifBlank { null },
                    amount      = String.format(java.util.Locale.US, "%.2f", total),
                    splitMethod = if (fresh.splitMethod == SplitMethod.PERCENT) "PERCENTAGE"
                    else fresh.splitMethod.name,
                    paidByUser  = fresh.paidByUserId,
                    groupId     = groupId,
                    shares      = shares
                )
                updateExpenseUseCase(expenseId, request)
                _state.update { it.copy(isSaving = false, isUpdated = true) }

            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false) }
                SnackbarController.show(e.toAppError().toWriteMessage())
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
        members: List<com.app.splitwell.data.local.model.TripManager>,
        includedUserIds: Set<String>,
        payerUserId: String
    ): List<String> {
        val count  = includedUserIds.size.coerceAtLeast(1)
        val base   = floor(total / count * 100) / 100
        val given  = base * count
        val remainder = ((total - given) * 100).roundToInt() / 100.0
        val payerIndex = members.indexOfFirst { it.userId == payerUserId }

        return members.mapIndexed { index, member ->
            when {
                member.userId !in includedUserIds -> "0.00"
                index == payerIndex               -> "%.2f".format(base + remainder)
                else                              -> "%.2f".format(base)
            }
        }
    }

    private fun validateSplit(): Boolean {
        val s = _state.value
        val total = s.amount.toDoubleOrNull() ?: 0.0

        return when (s.splitMethod) {
            SplitMethod.EQUAL -> true
            SplitMethod.AMOUNT -> {
                val assigned = s.members.mapIndexed { i, m ->
                    if (s.isIncluded(m.userId))
                        s.splitAmounts.getOrElse(i) { "0.00" }.toDoubleOrNull() ?: 0.0
                    else 0.0
                }.sum()
                val diff = abs(assigned - total)
                if (diff > 0.01) {
                    _state.update {
                        it.copy(splitError = "Total ₹${"%.2f".format(assigned)} doesn't match ₹${"%.2f".format(total)}")
                    }
                    false
                } else true
            }
            SplitMethod.PERCENT -> {
                val pct = s.members.mapIndexed { i, m ->
                    if (s.isIncluded(m.userId))
                        s.percentages.getOrElse(i) { "0.00" }.toDoubleOrNull() ?: 0.0
                    else 0.0
                }.sum()
                val diff = abs(pct - 100.0)
                if (diff > 0.01) {
                    _state.update {
                        it.copy(splitError = "Total ${"%.2f".format(pct)}% — must equal 100%")
                    }
                    false
                } else {
                    val computed = s.members.mapIndexed { i, m ->
                        if (s.isIncluded(m.userId))
                            "%.2f".format(
                                (s.percentages.getOrElse(i) { "0.00" }.toDoubleOrNull() ?: 0.0) / 100.0 * total
                            )
                        else "0.00"
                    }
                    _state.update { it.copy(splitAmounts = computed) }
                    true
                }
            }
        }
    }
}