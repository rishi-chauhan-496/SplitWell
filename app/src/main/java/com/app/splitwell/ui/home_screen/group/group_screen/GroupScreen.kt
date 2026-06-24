package com.app.splitwell.ui.home_screen.group.group_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.splitwell.ui.components.EmptyStateView
import com.app.splitwell.ui.home_screen.expense.ExpenseListCard
import com.app.splitwell.ui.theme.gradient2
import org.koin.androidx.compose.koinViewModel
import com.app.splitwell.ui.components.PollWhileVisible
import com.app.splitwell.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(
    groupId: String,
    ownerID: String,
    onAddExpense: () -> Unit,
    onAddMember: () -> Unit,
    onSettlement: () -> Unit,
    onExpenseClick: (String) -> Unit,
    onUnavailable: () -> Unit
) {
    val viewModel: GroupDetailViewModel = koinViewModel()
    val state = viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(groupId) { viewModel.load(groupId, ownerID) }

    PollWhileVisible { viewModel.pollRefresh(groupId, ownerID) }

    LaunchedEffect(state.value.isUnavailable) {
        if (state.value.isUnavailable) onUnavailable()
    }

    // Open Android share sheet when inviteUrl is ready
    LaunchedEffect(state.value.inviteUrl) {
        state.value.inviteUrl?.let { url ->
            val sendIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                putExtra(
                    android.content.Intent.EXTRA_TEXT,
                    "Join my group on SplitWell!\n$url"
                )
                type = "text/plain"
            }
            val shareIntent = android.content.Intent.createChooser(
                sendIntent, "Share Invite Link"
            )
            context.startActivity(shareIntent)
            viewModel.onInviteShared()       // clear URL after sharing
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(gradient2)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = state.value.groupName.ifBlank { stringResource(R.string.group_fallback_name) },
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            stringResource(R.string.group_member_count, state.value.memberCount),
                            color = Color.White.copy(0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            stringResource(
                                R.string.group_expense_count,
                                state.value.expenses.size
                            ), color = Color.White.copy(0.8f), fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(
                            R.string.group_total_amount,
                            "%.2f".format(state.value.totalAmount)
                        ),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    GroupActionButton(
                        stringResource(R.string.group_action_add_member),
                        R.drawable.group,
                        onAddMember,
                        Modifier.weight(1f)
                    )
                    GroupActionButton(
                        stringResource(R.string.group_action_settlement),
                        R.drawable.settlement,
                        onSettlement,
                        Modifier.weight(1f)
                    )
                    GroupActionButton(
                        stringResource(R.string.group_action_add_expense),
                        R.drawable.expense,
                        onAddExpense,
                        Modifier.weight(1f)
                    )
                }
            }
            // Invite icon button — top right corner of the card
            IconButton(
                onClick = { viewModel.createInvite(groupId, ownerID) },
                enabled = !state.value.isGeneratingInvite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                if (state.value.isGeneratingInvite) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        painter = painterResource(
                            R.drawable.link  // use your share icon
                        ),
                        contentDescription = "Invite",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        PullToRefreshBox(
            isRefreshing = state.value.isRefreshing,
            onRefresh = { viewModel.refresh(groupId, ownerID) },
            modifier = Modifier.weight(1f)
        ) {
            if (state.value.expenses.isEmpty()) {
                EmptyStateView(
                    message = stringResource(R.string.group_empty_expenses),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.value.expenses) { expense ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            ExpenseListCard(
                                title = expense.title,
                                by = expense.paidByUserName,
                                amount = expense.amount,
                                createdAt = expense.createdAt,
                                onClick = { onExpenseClick(expense.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupActionButton(
    label: String,
    icon: Int,
    onClick: () -> Unit,
    modifier: Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White.copy(alpha = 0.15f),
            contentColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
        contentPadding = PaddingValues(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = label,
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )
            Text(
                label, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Medium,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}