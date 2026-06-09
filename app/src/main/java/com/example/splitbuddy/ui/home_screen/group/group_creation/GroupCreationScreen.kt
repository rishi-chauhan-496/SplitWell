package com.example.splitbuddy.ui.home_screen.group.group_creation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitbuddy.R
import com.example.splitbuddy.ui.components.AppTextField
import com.example.splitbuddy.ui.components.GradientButton
import com.example.splitbuddy.ui.components.LoadingView
import com.example.splitbuddy.ui.home_screen.expense.ExpensePersonCard2
import org.koin.androidx.compose.koinViewModel

@Composable
fun GroupCreationScreen(
    userId: String,
    onGroupCreated: () -> Unit
) {
    val viewModel: GroupCreationViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current


    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            Toast.makeText(context, "Group created", Toast.LENGTH_SHORT).show()
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

        if (uiState.isLoading) {
            LoadingView(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(uiState.users) { user ->
                    ExpensePersonCard2(
                        user = user,
                        isSelected = uiState.selectedUserIds.contains(user.id),
                        onCheckedChange = { viewModel.onUserSelected(user.id) }
                    )
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