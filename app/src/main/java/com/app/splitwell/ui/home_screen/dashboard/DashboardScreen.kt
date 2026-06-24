package com.app.splitwell.ui.home_screen.dashboard

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.splitwell.ui.components.ScreenStateWrapper
import com.app.splitwell.ui.home_screen.expense.ExpenseListCard
import com.app.splitwell.ui.home_screen.group.GroupListCard
import com.app.splitwell.ui.theme.Primary
import com.app.splitwell.ui.theme.gradient
import org.koin.androidx.compose.koinViewModel
import com.app.splitwell.ui.components.PollWhileVisible
import com.app.splitwell.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userId: String,
    onGroupClick: (String) -> Unit,
    onExpenseClick: (String, String) -> Unit
) {

    val viewModel: DashboardViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load(userId) }
    PollWhileVisible { viewModel.pollRefresh(userId) }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { viewModel.load(userId, isRefresh = true) }
    ) {
        ScreenStateWrapper(isLoading = state.isLoading) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item { GreetingHeader(userName = state.userName) }
                item {
                    SummaryCardsRow(
                        totalSpent = state.totalSpent,
                        youAreOwed = state.youAreOwed,
                        youOwe = state.youOwe
                    )
                }
                item { SectionHeader(title = stringResource(R.string.dashboard_recent_groups)) }
                if (state.recentGroups.isEmpty()) {
                    item { EmptyHint(text = stringResource(R.string.dashboard_empty_groups)) }
                } else {
                    items(state.recentGroups) { group ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            GroupListCard(
                                groupName = group.groupName,
                                totalMember = group.totalMember,
                                totalExpense = group.totalExpense,
                                totalAmount = group.totalAmount,
                                createdAt = group.createdAt,
                                onClick = { onGroupClick(group.id) }
                            )
                        }
                    }
                }
                item { SectionHeader(title = stringResource(R.string.dashboard_recent_expenses)) }
                if (state.recentExpenses.isEmpty()) {
                    item { EmptyHint(text = stringResource(R.string.dashboard_empty_expenses)) }
                } else {
                    items(state.recentExpenses) { expense ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            ExpenseListCard(
                                title = expense.title,
                                by = expense.paidByName,
                                amount = expense.amount,
                                createdAt = expense.createdAt,
                                onClick = { onExpenseClick(expense.id, expense.groupId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Small private composables ──────────────────────────────────────────────────

@Composable
private fun GreetingHeader(userName: String?) {
    val displayName = userName?.takeIf { it.isNotBlank() }
        ?: stringResource(R.string.dashboard_default_name)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(gradient)  // Uses your existing blue→purple gradient
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.dashboard_greeting, displayName),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.dashboard_greeting_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun SummaryCardsRow(
    totalSpent: Double,
    youAreOwed: Double,
    youOwe: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SummaryCard(
            label = stringResource(R.string.dashboard_total_spent),
            amount = totalSpent,
            amountColor = Primary,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            label = stringResource(R.string.dashboard_owed_to_you),
            amount = youAreOwed,
            amountColor = Color(0xFF2E7D32),
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            label = stringResource(R.string.dashboard_you_owe),
            amount = youOwe,
            amountColor = Color(0xFFC62828),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryCard(
    label: String,
    amount: Double,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.dashboard_amount_format, "%.0f".format(amount)),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 4.dp),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun EmptyHint(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.surfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}