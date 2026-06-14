package com.example.splitbuddy.ui.home_screen.group.group_add_member_screen

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitbuddy.R
import com.example.splitbuddy.data.local.model.User
import com.example.splitbuddy.ui.components.EmptyStateView
import com.example.splitbuddy.ui.components.GradientButton
import com.example.splitbuddy.ui.components.InitialsAvatar
import com.example.splitbuddy.ui.components.LoadingView
import com.example.splitbuddy.ui.theme.Primary
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddMemberScreen(
    groupId: String,
    onBack: () -> Unit
) {
    val viewModel: AddMemberViewModel = koinViewModel()
    val state = viewModel.state.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val toastMsg = stringResource(R.string.toast_members_added)

    LaunchedEffect(groupId) { viewModel.load(groupId) }
    LaunchedEffect(state.value.isSaved) {
        if (state.value.isSaved) {
            Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        val count = state.value.selectedUserIds.size
        Text(
            text = when (count) {
                0    -> stringResource(R.string.add_member_hint)
                1    -> stringResource(R.string.add_member_selected_single)
                else -> stringResource(R.string.add_member_selected_plural, count)
            },
            color = if (count == 0) Color.Gray else Primary,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            state.value.isLoading -> LoadingView(modifier = Modifier.weight(1f))

            state.value.error != null -> {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.add_member_error, state.value.error ?: ""))
                }
            }

            state.value.users.isEmpty() -> {
                EmptyStateView(
                    message = stringResource(R.string.add_member_no_users),
                    modifier = Modifier.weight(1f)
                )
            }

            else -> {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(state.value.users) { user ->
                        UserSelectCard(
                            user = user,
                            isSelected = user.id in state.value.selectedUserIds,
                            isExisting = user.id in state.value.existingMemberIds,
                            onClick = { viewModel.onUserToggle(user.id) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        GradientButton(
            text = stringResource(R.string.add_member_button),
            onClick = { viewModel.addMembers(groupId) },
            enabled = state.value.selectedUserIds.isNotEmpty(),
            isLoading = state.value.isSaving
        )
    }
}

@Composable
private fun UserSelectCard(
    user: User,
    isSelected: Boolean,
    isExisting: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        isExisting -> Color.LightGray
        isSelected -> Primary
        else       -> Color.Transparent
    }
    val bgColor = when {
        isSelected -> Primary.copy(alpha = 0.08f)
        else       -> MaterialTheme.colorScheme.background
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = !isExisting) { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InitialsAvatar(
            name = user.firstName,
            backgroundColor = if (isExisting) Color.LightGray else Primary.copy(alpha = 0.15f),
            textColor = if (isExisting) Color.Gray else Primary
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${user.firstName} ${user.lastName}",
                fontWeight = FontWeight.SemiBold,
                color = if (isExisting) Color.Gray else MaterialTheme.colorScheme.secondary
            )
            Text(
                text = if (isExisting) stringResource(R.string.add_member_already_in_group)
                else user.email,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}