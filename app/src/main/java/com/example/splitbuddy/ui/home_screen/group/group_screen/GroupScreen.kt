package com.example.splitbuddy.ui.home_screen.group.group_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitbuddy.ui.components.EmptyStateView
import com.example.splitbuddy.ui.components.OfflineBanner
import com.example.splitbuddy.ui.home_screen.expense.ExpenseListCard
import com.example.splitbuddy.ui.theme.gradient2
import org.koin.androidx.compose.koinViewModel

@Composable
fun GroupScreen(
    groupId: String,
    onAddExpense: () -> Unit,
    onAddMember: () -> Unit,
    onSettlement: () -> Unit,
    onExpenseClick: (String) -> Unit
) {
    val viewModel: GroupDetailViewModel = koinViewModel()
    val state = viewModel.uiState.collectAsState()

    LaunchedEffect(groupId) { viewModel.load(groupId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        OfflineBanner(isOffline = state.value.isOffline)
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
                        text = state.value.groupName.ifBlank { "Group" },
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("${state.value.memberCount} members", color = Color.White.copy(0.8f), fontSize = 14.sp)
                        Text("${state.value.expenses.size} expenses", color = Color.White.copy(0.8f), fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Total: ₹${"%.2f".format(state.value.totalAmount)}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    GroupActionButton("Add Member",  com.example.splitbuddy.R.drawable.group,      onAddMember,  Modifier.weight(1f))
                    GroupActionButton("Settlement",  com.example.splitbuddy.R.drawable.settlement, onSettlement, Modifier.weight(1f))
                    GroupActionButton("Add Expense", com.example.splitbuddy.R.drawable.expense,    onAddExpense, Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (state.value.expenses.isEmpty()) {
            EmptyStateView(message = "No expenses yet", modifier = Modifier.weight(1f))
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.value.expenses) { expense ->
                    ExpenseListCard(
                        title     = expense.title,
                        by        = expense.paidByUserName,
                        amount    = expense.amount,
                        createdAt = expense.createdAt,
                        onClick   = { onExpenseClick(expense.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupActionButton(label: String, icon: Int, onClick: () -> Unit, modifier: Modifier) {
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
            horizontalAlignment = Alignment.CenterHorizontally ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = label,
                modifier = Modifier.size(18.dp),
                tint = Color.White
            )
            Text(label, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Medium,
                maxLines = 1,
                softWrap = false)
        }
    }
}