package com.app.splitwell.ui.home_screen.group.group_creation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.app.splitwell.ui.components.AppTextField
import com.app.splitwell.ui.components.EmptyStateView
import com.app.splitwell.ui.components.GradientButton
import com.app.splitwell.ui.components.LoadingView
import com.app.splitwell.ui.home_screen.expense.ExpensePersonCard2
import org.koin.androidx.compose.koinViewModel
import com.app.splitwell.R

@Composable
fun GroupCreationScreen(
    userId: String,
    onGroupCreated: () -> Unit
) {
    val viewModel: GroupCreationViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val toastMsg = stringResource(R.string.toast_group_created)

    LaunchedEffect(Unit) {
        viewModel.load(userId)
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
            onGroupCreated()
            viewModel.resetSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        AppTextField(
            label = stringResource(R.string.title),
            value = uiState.groupName,
            onValueChange = viewModel::onGroupNameChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.Group_Creation_Filed),
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            uiState.isLoading -> LoadingView(modifier = Modifier.weight(1f))

            uiState.friends.isEmpty() -> EmptyStateView(
                message = stringResource(R.string.friends_empty_message),
                modifier = Modifier.weight(1f)
            )

            else -> {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(uiState.friends) { friend ->
                        ExpensePersonCard2(
                            friend = friend,
                            isSelected = uiState.selectedUserIds.contains(friend.id),
                            onCheckedChange = { viewModel.onUserSelected(friend.id) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        GradientButton(
            text = stringResource(R.string.save_),
            onClick = { viewModel.createGroup(userId) }
        )
    }
}