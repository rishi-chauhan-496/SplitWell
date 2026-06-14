package com.example.splitbuddy.ui.home_screen.group.groups_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.splitbuddy.R
import com.example.splitbuddy.ui.components.ScreenStateWrapper
import com.example.splitbuddy.ui.home_screen.group.GroupListCard
import com.example.splitbuddy.ui.theme.Primary
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    userId: String,
    onNext: (String) -> Unit,
    onCreate: () -> Unit
) {
    val viewModel: GroupsDataViewModel = koinViewModel()
    val state = viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.init(userId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            PullToRefreshBox(
                isRefreshing = state.value.isRefreshing,
                onRefresh = { viewModel.refresh(userId) }
            ) {
                ScreenStateWrapper(
                    isLoading = state.value.isLoading,
                    error = state.value.error?.let {
                        stringResource(R.string.error_something_went_wrong, it)
                    },
                    isEmpty = state.value.groups.isEmpty(),
                    emptyMessage = stringResource(R.string.groups_empty_message)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(state.value.groups) { group ->
                            GroupListCard(
                                groupName = group.groupName,
                                totalMember = group.totalMember,
                                totalExpense = group.totalExpense,
                                totalAmount = group.totalAmount,
                                createdAt = group.createdAt,
                                onClick = { onNext(group.id) }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onCreate,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Primary,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = stringResource(R.string.Group_Creation_Button)
            )
        }
    }
}