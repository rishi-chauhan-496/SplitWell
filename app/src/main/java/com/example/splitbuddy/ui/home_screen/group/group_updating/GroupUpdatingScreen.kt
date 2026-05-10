package com.example.splitbuddy.ui.home_screen.group.group_updating

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitbuddy.R
import com.example.splitbuddy.ui.components.AppTextField
import com.example.splitbuddy.ui.components.DeleteButtonWithConfirm
import com.example.splitbuddy.ui.components.GradientButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun GroupUpdatingScreen(
    groupId: String,
    onBack: () -> Unit
) {
    val viewModel: GroupUpdatingViewModel = koinViewModel()
    val state = viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(groupId) {
        viewModel.load(groupId)
    }

    LaunchedEffect(state.value.isUpdated, state.value.isDeleted) {
        if (state.value.isUpdated) {
            Toast.makeText(context, "Group Updated", Toast.LENGTH_SHORT).show()
            onBack()
        }
        if (state.value.isDeleted) {
            Toast.makeText(context, "Group Deleted", Toast.LENGTH_SHORT).show()
            onBack()
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
            value = state.value.groupName,
            onValueChange = viewModel::onNameChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.Group_Updating_Filed),
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        GradientButton(
            text = stringResource(R.string.save_),
            onClick = { viewModel.update(groupId, state.value.groupName) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DeleteButtonWithConfirm(
            buttonText = stringResource(R.string.delete),
            dialogTitle = "Delete Group",
            dialogMessage = "Are you sure you want to delete this group?",
            onConfirm = { viewModel.delete(groupId) }
        )
    }
}