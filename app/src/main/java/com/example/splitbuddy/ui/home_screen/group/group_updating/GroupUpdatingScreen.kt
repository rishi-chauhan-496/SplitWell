package com.example.splitbuddy.ui.home_screen.group.group_updating

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitbuddy.R
import com.example.splitbuddy.data.local.model.TripManager
import com.example.splitbuddy.ui.components.AppTextField
import com.example.splitbuddy.ui.components.DeleteButtonWithConfirm
import com.example.splitbuddy.ui.components.GradientButton
import com.example.splitbuddy.ui.components.InitialsAvatar
import com.example.splitbuddy.ui.components.LoadingView
import com.example.splitbuddy.ui.theme.Primary
import org.koin.androidx.compose.koinViewModel

@Composable
fun GroupUpdatingScreen(
    groupId: String,
    onBack: () -> Unit
) {
    val viewModel: GroupUpdatingViewModel = koinViewModel()
    val state = viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val msgUpdated = stringResource(R.string.group_toast_updated)
    val msgDeleted = stringResource(R.string.group_toast_deleted)
    val toastMsg = stringResource(R.string.toast_members_removed)

    LaunchedEffect(groupId) {
        viewModel.load(groupId)
    }

    LaunchedEffect(state.value.isUpdated) {
        if (state.value.isUpdated) {
            Toast.makeText(context, msgUpdated, Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    LaunchedEffect(state.value.isDeleted) {
        if (state.value.isDeleted) {
            Toast.makeText(context, msgDeleted, Toast.LENGTH_SHORT).show()
            onBack()
        }
    }
    LaunchedEffect(state.value.isMembersRemoved) {
        if (state.value.isMembersRemoved) {
            Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
        }
    }

    if (state.value.isLoading) {
        LoadingView()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // ── Group name field ──────────────────────────────────────────────────
        AppTextField(
            label = stringResource(R.string.title),
            value = state.value.groupName,
            onValueChange = viewModel::onNameChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Members section ───────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.Group_Updating_Filed),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Remove selected button — only shows when members are selected
            if (state.value.selectedToRemove.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.group_remove_selected, state.value.selectedToRemove.size),
                    color = Color.Red,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (state.value.members.isEmpty()) {
            Text(
                text = stringResource(R.string.group_no_members),
                color = MaterialTheme.colorScheme.surfaceVariant,
                fontSize = 14.sp
            )
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.value.members) { member ->
                MemberRemoveCard(
                    member = member,
                    isSelected = member.userId in state.value.selectedToRemove,
                    onClick = { viewModel.onMemberToggle(member.userId) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        GradientButton(
            text = stringResource(R.string.save_),
            onClick = { viewModel.update(groupId);viewModel.removeMembers(groupId) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DeleteButtonWithConfirm(
            buttonText = stringResource(R.string.delete),
            dialogTitle = stringResource(R.string.group_delete_title),
            dialogMessage = stringResource(R.string.group_delete_message),
            onConfirm = { viewModel.delete(groupId) }
        )
    }
}

// ── Member card with remove selection ────────────────────────────────────────

@Composable
private fun MemberRemoveCard(
    member: TripManager,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) Color.Red.copy(alpha = 0.08f)
                else MaterialTheme.colorScheme.surface
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Red.copy(alpha = 0.5f)
                else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InitialsAvatar(
            name = member.userName.ifBlank { member.userId },
            size = 40.dp,
            backgroundColor = if (isSelected) Color.Red.copy(alpha = 0.12f)
            else Primary.copy(alpha = 0.12f),
            textColor = if (isSelected) Color.Red else Primary
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = member.userName.ifBlank { member.userId },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isSelected) Color.Red
                else MaterialTheme.colorScheme.secondary
            )
            Text(
                text = member.role,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        // Checkmark when selected for removal
        if (isSelected) {
            Text(
                text = "✕",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}