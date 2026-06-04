package com.example.splitbuddy.ui.home_screen.expense.expense_creating

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitbuddy.R
import com.example.splitbuddy.ui.components.GradientButton
import com.example.splitbuddy.ui.components.RunningTotalBar
import com.example.splitbuddy.ui.components.SplitTab
import com.example.splitbuddy.ui.components.SplitTabRow
import com.example.splitbuddy.ui.home_screen.expense.ExpensePersonCard

@Composable
fun ExpenseScreen2(
    state: ExpenseUiState,
    onSplitMethodChange: (SplitMethod) -> Unit,
    onSplitAmountChange: (Int, String) -> Unit,
    onPercentChange: (Int, String) -> Unit,
    onUserToggle: (String) -> Unit,
    onNext: () -> Unit
) {
    val total = state.amount.toDoubleOrNull() ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.Expense_Screen_2_Title),
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${stringResource(R.string.Expense_Screen_2_Title_2)} ₹${state.amount} ?",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.expense_splitting_among, state.includedCount, state.members.size),
            color = Color.Gray,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Map SplitMethod enum to SplitTab enum
        val selectedTab = when (state.splitMethod) {
            SplitMethod.EQUAL   -> SplitTab.EQUAL
            SplitMethod.PERCENT -> SplitTab.PERCENT
            SplitMethod.AMOUNT  -> SplitTab.AMOUNT
        }

        SplitTabRow(
            selected = selectedTab,
            onSelect = { tab ->
                onSplitMethodChange(
                    when (tab) {
                        SplitTab.EQUAL   -> SplitMethod.EQUAL
                        SplitTab.PERCENT -> SplitMethod.PERCENT
                        SplitTab.AMOUNT  -> SplitMethod.AMOUNT
                    }
                )
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        when (state.splitMethod) {
            SplitMethod.AMOUNT -> {
                val assigned = state.members.mapIndexed { i, m ->
                    if (state.isIncluded(m.userId))
                        state.splitAmounts.getOrElse(i) { "0.00" }.toDoubleOrNull() ?: 0.0
                    else 0.0
                }.sum()
                RunningTotalBar(assigned = assigned, total = total, unit = "₹")
            }
            SplitMethod.PERCENT -> {
                val pct = state.members.mapIndexed { i, m ->
                    if (state.isIncluded(m.userId))
                        state.percentages.getOrElse(i) { "0.00" }.toDoubleOrNull() ?: 0.0
                    else 0.0
                }.sum()
                RunningTotalBar(assigned = pct, total = 100.0, unit = "%")
            }
            SplitMethod.EQUAL -> {}
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            when (state.splitMethod) {
                SplitMethod.EQUAL -> itemsIndexed(state.members) { index, member ->
                    ExpensePersonCard(
                        name = member.userName,
                        amount = state.splitAmounts.getOrElse(index) { "0.00" },
                        isEditable = false,
                        isIncluded = state.isIncluded(member.userId),
                        suffix = "₹",
                        onToggleInclude = { onUserToggle(member.userId) },
                        onAmountChange = {}
                    )
                }
                SplitMethod.AMOUNT -> itemsIndexed(state.members) { index, member ->
                    val included = state.isIncluded(member.userId)
                    ExpensePersonCard(
                        name = member.userName,
                        amount = state.splitAmounts.getOrElse(index) { "" },
                        isEditable = included,
                        isIncluded = included,
                        suffix = "₹",
                        onToggleInclude = { onUserToggle(member.userId) },
                        onAmountChange = { onSplitAmountChange(index, it) }
                    )
                }
                SplitMethod.PERCENT -> itemsIndexed(state.members) { index, member ->
                    val included = state.isIncluded(member.userId)
                    ExpensePersonCard(
                        name = member.userName,
                        amount = state.percentages.getOrElse(index) { "" },
                        isEditable = included,
                        isIncluded = included,
                        suffix = "%",
                        onToggleInclude = { onUserToggle(member.userId) },
                        onAmountChange = { onPercentChange(index, it) }
                    )
                }
            }
        }

        state.splitError?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        GradientButton(
            text = stringResource(R.string.next_),
            onClick = onNext
        )
    }
}