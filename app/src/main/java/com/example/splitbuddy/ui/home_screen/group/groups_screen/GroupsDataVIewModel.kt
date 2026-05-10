package com.example.splitbuddy.ui.home_screen.group.groups_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.domain.usecase.expense.GetAllExpenseByGroupIdUseCase
import com.example.splitbuddy.domain.usecase.group.GetAllGroupsUseCase
import com.example.splitbuddy.domain.usecase.group.GetGroupMembersUseCase
import com.example.splitbuddy.domain.usecase.user.GetAllUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class GroupsDataViewModel(
    private val getGroupsUseCase: GetAllGroupsUseCase,
    private val getAllUserUseCase: GetAllUserUseCase,
    private val getGroupMembersUseCase: GetGroupMembersUseCase,
    private val getAllExpenseByGroupIdUseCase: GetAllExpenseByGroupIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState: StateFlow<GroupsUiState> = _uiState

    fun loadGroups(userId: String) {
        viewModelScope.launch {
            _uiState.value = GroupsUiState(isLoading = true)

            try {

                getAllUserUseCase()

                val trips = getGroupsUseCase(userId)

                val groupList = trips.mapNotNull { trip ->
                    trip?.let {

                        val members = getGroupMembersUseCase(it.id)

                        val expenses = try {
                            getAllExpenseByGroupIdUseCase(it.id)
                        } catch (_: Exception) {
                            emptyList()
                        }

                        val totalAmount = expenses.sumOf { exp ->
                            exp.amount
                        }

                        val totalExpense = expenses.size

                        GroupSummary(
                            id = it.id,
                            groupName = it.tripTitle,
                            totalMember = members.size,
                            totalExpense = totalExpense,
                            totalAmount = totalAmount,
                            createdAt    = it.createdAt
                        )
                    }
                }

                _uiState.value = GroupsUiState(
                    groups = groupList
                )

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = GroupsUiState(
                    error = e.message
                )
            }
        }
    }
}