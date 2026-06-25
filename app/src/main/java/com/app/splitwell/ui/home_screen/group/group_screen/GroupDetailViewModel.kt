package com.app.splitwell.ui.home_screen.group.group_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.splitwell.data.util.Resource
import com.app.splitwell.domain.usecase.expense.GetAllExpenseByGroupIdUseCase
import com.app.splitwell.domain.usecase.group.GetGroupMembersUseCase
import com.app.splitwell.domain.usecase.group.GetGroupUseCase
import com.app.splitwell.data.util.toAppError
import com.app.splitwell.data.util.toWriteMessage
import com.app.splitwell.domain.usecase.group.RefreshGroupUseCase
import com.app.splitwell.domain.usecase.invite.CreateInviteLinkUseCase
import com.app.splitwell.ui.util.SnackbarController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupDetailViewModel(
    private val getAllExpenseByGroupIdUseCase: GetAllExpenseByGroupIdUseCase,
    private val getGroupUseCase: GetGroupUseCase,
    private val getGroupMembersUseCase: GetGroupMembersUseCase,
    private val createInviteLinkUseCase: CreateInviteLinkUseCase,
    private val refreshGroupUseCase: RefreshGroupUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupDetailUiState())
    val uiState: StateFlow<GroupDetailUiState> = _uiState

    // Guards against the manual pull-to-refresh and the background poll
    // running at the same time and racing to update isRefreshing.
    private var isFetching = false

    fun load(groupId: String, ownerID: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Collect expense flow
            getAllExpenseByGroupIdUseCase.observe().collect { resource ->
                val expenses = when (resource) {
                    is Resource.Success -> resource.data
                    is Resource.Error -> resource.data ?: emptyList()
                    is Resource.Loading -> emptyList()
                }

                val group = getGroupUseCase(groupId)
                val members = getGroupMembersUseCase(groupId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        groupName = group?.tripTitle ?: "",
                        memberCount = members.size,
                        expenses = expenses,
                        totalAmount = expenses.sumOf { it.amount }
                    )
                }
            }
        }

        // Initial load — refresh group + members from network, then expenses
        viewModelScope.launch {
            fetchAndCheck(groupId, ownerID)
        }
    }

    // Pull-to-refresh — user-initiated, shows the spinner
    fun refresh(groupId: String, ownerID: String) {
        if (isFetching) return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            fetchAndCheck(groupId, ownerID)
        }
    }

    // Silent background poll — same fetch, no spinner
    fun pollRefresh(groupId: String, ownerID: String) {
        if (isFetching) return
        viewModelScope.launch {
            fetchAndCheck(groupId, ownerID)
        }
    }

    private suspend fun fetchAndCheck(groupId: String, ownerID: String) {
        isFetching = true
        try {
            refreshGroupUseCase(groupId)
            getAllExpenseByGroupIdUseCase.load(groupId)
            checkAvailability(groupId, ownerID)
        } finally {
            isFetching = false
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    // After a refresh, the group's member list locally reflects whatever the
    // server just sent. If you're not in it anymore, you've either been
    // removed or the group's gone — either way, this screen shouldn't keep
    // showing it.
    private suspend fun checkAvailability(groupId: String, ownerID: String) {
        if (_uiState.value.isUnavailable) return

        val stillMember = getGroupMembersUseCase(groupId).any { it.userId == ownerID }
        if (!stillMember) {
            _uiState.update { it.copy(isUnavailable = true) }
            SnackbarController.show("This group is no longer available")
        }
    }

    // Called when user taps the invite button
    fun createInvite(groupId: String, ownerID: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingInvite = true) }
            try {
                val url = createInviteLinkUseCase(groupId, ownerID)
                _uiState.update {
                    it.copy(isGeneratingInvite = false, inviteUrl = url)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isGeneratingInvite = false) }
                SnackbarController.show(e.toAppError().toWriteMessage())
            }
        }
    }

    // Called after share sheet is shown — clears the URL so it doesn't re-trigger
    fun onInviteShared() {
        _uiState.update { it.copy(inviteUrl = null) }
    }
}