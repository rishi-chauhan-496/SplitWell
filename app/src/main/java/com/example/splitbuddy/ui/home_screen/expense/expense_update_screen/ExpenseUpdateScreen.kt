package com.example.splitbuddy.ui.home_screen.expense.expense_update_screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitbuddy.ui.components.*
import com.example.splitbuddy.ui.home_screen.expense.ExpensePersonCard
import com.example.splitbuddy.ui.home_screen.expense.expense_creating.SplitMethod
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExpenseUpdateScreen(
    expenseId: String,
    groupId: String,
    onBack: () -> Unit
) {
    val viewModel: ExpenseUpdateViewModel = koinViewModel()
    val state = viewModel.state.collectAsState()
    val context = LocalContext.current
    val updatedMsg = "Expense Updated"

    LaunchedEffect(expenseId) { viewModel.load(expenseId, groupId) }

    LaunchedEffect(state.value.isUpdated) {
        if (state.value.isUpdated) {
            Toast.makeText(context, updatedMsg, Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    val total = state.value.amount.toDoubleOrNull() ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                AppTextField(
                    label = "Title",
                    value = state.value.title,
                    onValueChange = viewModel::onTitleChange,
                    isError = state.value.titleError != null,
                    errorMessage = state.value.titleError
                )
            }

            item {
                AppTextField(
                    label = "Amount",
                    value = state.value.amount,
                    onValueChange = viewModel::onAmountChange,
                    isError = state.value.amountError != null,
                    errorMessage = state.value.amountError
                )
            }

            item {
                AppTextField(
                    label = "Description",
                    value = state.value.description,
                    onValueChange = viewModel::onDescriptionChange,
                    singleLine = false,
                    fieldHeight = 80.dp
                )
            }

            item {
                Text("Paid by", color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp)
                PaidByDropdown(
                    members = state.value.members,
                    selectedUserId = state.value.paidByUserId,
                    onSelect = viewModel::onPaidByChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Splitting among ${state.value.includedCount} of ${state.value.members.size} people",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                val selectedTab = when (state.value.splitMethod) {
                    SplitMethod.EQUAL   -> SplitTab.EQUAL
                    SplitMethod.PERCENT -> SplitTab.PERCENT
                    SplitMethod.AMOUNT  -> SplitTab.AMOUNT
                }

                SplitTabRow(
                    selected = selectedTab,
                    onSelect = { tab ->
                        viewModel.onSplitMethodChange(
                            when (tab) {
                                SplitTab.EQUAL   -> SplitMethod.EQUAL
                                SplitTab.PERCENT -> SplitMethod.PERCENT
                                SplitTab.AMOUNT  -> SplitMethod.AMOUNT
                            }
                        )
                    }
                )
            }

            item {
                when (state.value.splitMethod) {
                    SplitMethod.AMOUNT -> {
                        val assigned = state.value.members.mapIndexed { i, m ->
                            if (state.value.isIncluded(m.userId))
                                state.value.splitAmounts.getOrElse(i) { "0.00" }.toDoubleOrNull() ?: 0.0
                            else 0.0
                        }.sum()
                        RunningTotalBar(assigned = assigned, total = total, unit = "₹")
                    }
                    SplitMethod.PERCENT -> {
                        val pct = state.value.members.mapIndexed { i, m ->
                            if (state.value.isIncluded(m.userId))
                                state.value.percentages.getOrElse(i) { "0.00" }.toDoubleOrNull() ?: 0.0
                            else 0.0
                        }.sum()
                        RunningTotalBar(assigned = pct, total = 100.0, unit = "%")
                    }
                    SplitMethod.EQUAL -> {}
                }
            }

            if (state.value.members.isEmpty()) {
                item {
                    EmptyStateView(message = "No members found")
                }
            } else {
                itemsIndexed(state.value.members) { index, member ->
                    val included = state.value.isIncluded(member.userId)
                    when (state.value.splitMethod) {
                        SplitMethod.EQUAL -> ExpensePersonCard(
                            name = member.userName,
                            amount = state.value.splitAmounts.getOrElse(index) { "0.00" },
                            isEditable = false,
                            isIncluded = included,
                            suffix = "₹",
                            onToggleInclude = { viewModel.onUserToggle(member.userId) },
                            onAmountChange = {}
                        )
                        SplitMethod.AMOUNT -> ExpensePersonCard(
                            name = member.userName,
                            amount = state.value.splitAmounts.getOrElse(index) { "" },
                            isEditable = included,
                            isIncluded = included,
                            suffix = "₹",
                            onToggleInclude = { viewModel.onUserToggle(member.userId) },
                            onAmountChange = { viewModel.onSplitAmountChange(index, it) }
                        )
                        SplitMethod.PERCENT -> ExpensePersonCard(
                            name = member.userName,
                            amount = state.value.percentages.getOrElse(index) { "" },
                            isEditable = included,
                            isIncluded = included,
                            suffix = "%",
                            onToggleInclude = { viewModel.onUserToggle(member.userId) },
                            onAmountChange = { viewModel.onPercentChange(index, it) }
                        )
                    }
                }
            }

            state.value.splitError?.let { err ->
                item {
                    Text(
                        text = err,
                        color = Color.Red,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.value.error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            GradientButton(
                text = "Save",
                onClick = { viewModel.update(expenseId, groupId) },
                isLoading = state.value.isSaving
            )
        }
    }
}