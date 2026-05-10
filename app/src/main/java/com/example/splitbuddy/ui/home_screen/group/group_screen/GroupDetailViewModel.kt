package com.example.splitbuddy.ui.home_screen.group.group_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.domain.usecase.expense.GetAllExpenseByGroupIdUseCase
import com.example.splitbuddy.domain.usecase.group.GetGroupMembersUseCase
import com.example.splitbuddy.domain.usecase.group.GetGroupUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GroupDetailViewModel(
    private val getAllExpenseByGroupIdUseCase: GetAllExpenseByGroupIdUseCase,
    private val getGroupUseCase: GetGroupUseCase,
    private val getGroupMembersUseCase: GetGroupMembersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupDetailUiState())
    val uiState: StateFlow<GroupDetailUiState> = _uiState

    fun load(groupId: String) {
        viewModelScope.launch {
            _uiState.value = GroupDetailUiState(isLoading = true)

            try {
                // Run all 3 calls at the same time using async
                val expensesDeferred = async { getAllExpenseByGroupIdUseCase(groupId) }
                val groupDeferred = async { getGroupUseCase(groupId) }
                val membersDeferred = async { getGroupMembersUseCase(groupId) }

                val expenses = expensesDeferred.await()
                val group = groupDeferred.await()
                val members = membersDeferred.await()

                _uiState.value = GroupDetailUiState(
                    groupName = group?.tripTitle ?: "",
                    memberCount = members.size,
                    expenses = expenses,
                    totalAmount = expenses.sumOf { it.amount }
                )

            } catch (e: Exception) {
                _uiState.value = GroupDetailUiState(error = e.message)
            }
        }
    }
}