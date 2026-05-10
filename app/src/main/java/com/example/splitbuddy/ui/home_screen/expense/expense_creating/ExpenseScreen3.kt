package com.example.splitbuddy.ui.home_screen.expense.expense_creating

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitbuddy.R
import com.example.splitbuddy.ui.components.GradientButton
import com.example.splitbuddy.ui.home_screen.expense.ExpensePersonCard
import com.example.splitbuddy.ui.theme.gradient2

@Composable
fun ExpenseScreen3(
    state: ExpenseUiState,
    onSave: () -> Unit
) {
    val includedMembers = state.members.filter { state.isIncluded(it.userId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(gradient2),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = state.title,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "₹${state.amount}",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Paid by: ${state.paidByUserName}",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Split among ${includedMembers.size} people",
                    color = Color.White.copy(alpha = 0.75f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(includedMembers) { _, member ->
                val originalIndex = state.members.indexOfFirst { it.userId == member.userId }
                ExpensePersonCard(
                    name = member.userName,
                    amount = state.splitAmounts.getOrElse(originalIndex) { "0.00" },
                    isEditable = false,
                    isIncluded = true,
                    suffix = "₹",
                    onToggleInclude = {},
                    onAmountChange = {}
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        GradientButton(
            text = stringResource(R.string.save_),
            onClick = onSave
        )
    }
}