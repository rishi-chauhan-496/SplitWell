package com.example.splitbuddy.ui.home_screen.group.group_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.data.util.Resource
import com.example.splitbuddy.domain.usecase.expense.GetAllExpenseByGroupIdUseCase
import com.example.splitbuddy.domain.usecase.group.GetGroupMembersUseCase
import com.example.splitbuddy.domain.usecase.group.GetGroupUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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
            _uiState.update { it.copy(isLoading = true) }

            // Collect expense flow
            getAllExpenseByGroupIdUseCase.observe().collect { resource ->
                val expenses = when (resource) {
                    is Resource.Success -> resource.data
                    is Resource.Error   -> resource.data ?: emptyList()
                    is Resource.Loading -> emptyList()
                }

                val group   = getGroupUseCase(groupId)
                val members = getGroupMembersUseCase(groupId)

                _uiState.update { it ->
                    it.copy(
                        isLoading   = false,
                        isRefreshing = false,
                        groupName   = group?.tripTitle ?: "",
                        memberCount = members.size,
                        expenses    = expenses,
                        totalAmount = expenses.sumOf { it.amount }
                    )
                }
            }
        }

        // Load expenses
        viewModelScope.launch {
            getAllExpenseByGroupIdUseCase.load(groupId)
        }
    }

    fun refresh(groupId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            getAllExpenseByGroupIdUseCase.load(groupId)
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
}