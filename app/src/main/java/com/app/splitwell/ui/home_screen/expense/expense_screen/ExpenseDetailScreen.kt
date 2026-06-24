package com.app.splitwell.ui.home_screen.expense.expense_screen

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.splitwell.ui.components.DeleteButtonWithConfirm
import com.app.splitwell.ui.components.EmptyStateView
import com.app.splitwell.ui.components.InitialsAvatar
import com.app.splitwell.ui.components.LoadingView
import com.app.splitwell.ui.theme.Primary
import com.app.splitwell.ui.theme.gradient2
import org.koin.androidx.compose.koinViewModel
import com.app.splitwell.R

@Composable
fun ExpenseDetailScreen(
    expenseId: String,
    onBack: () -> Unit
) {
    val viewModel: ExpenseDetailViewModel = koinViewModel()
    val state = viewModel.state.collectAsState()
    val context = LocalContext.current
    val deletedMsg = stringResource(R.string.expense_detail_delete_success)

    LaunchedEffect(expenseId) { viewModel.load(expenseId) }

    LaunchedEffect(state.value.isDeleted) {
        if (state.value.isDeleted) {
            Toast.makeText(context, deletedMsg, Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    val expense = state.value.expense

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            state.value.isLoading -> LoadingView()

            expense == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.expense_detail_not_found), color = Color.Gray)
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Summary card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(gradient2)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = expense.title,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.amount_rupee_format, "%.2f".format(expense.amount)),
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                InfoChip(
                                    label = stringResource(R.string.expense_detail_paid_by),
                                    value = expense.paidByUserName
                                )
                                InfoChip(
                                    label = stringResource(R.string.expense_detail_split),
                                    value = expense.splitMethod
                                )
                            }
                            if (!expense.description.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = expense.description,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.expense_detail_split_breakdown),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val includedShares = state.value.shares.filter { it.isIncluded }

                    if (includedShares.isEmpty()) {
                        EmptyStateView(
                            message = stringResource(R.string.expense_no_split_data),
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(includedShares) { share ->
                                ShareDetailCard(
                                    name = share.userName,
                                    amount = share.sharedAmount,
                                    percent = if (expense.splitMethod == "PERCENTAGE")
                                        share.sharedPercent else null
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    DeleteButtonWithConfirm(
                        buttonText = stringResource(R.string.expense_detail_delete_button),
                        dialogTitle = stringResource(R.string.expense_detail_delete_title),
                        dialogMessage = stringResource(R.string.expense_detail_delete_message),
                        confirmText = stringResource(R.string.expense_detail_delete_confirm),
                        onConfirm = { viewModel.delete(expenseId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
        Text(value, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
private fun ShareDetailCard(name: String, amount: Double, percent: Double?) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InitialsAvatar(name = name, size = 40.dp, fontSize = 16.sp)

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${"%.2f".format(amount)}",
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    fontSize = 15.sp
                )
                if (percent != null && percent > 0) {
                    Text(
                        text = "${"%.2f".format(percent)}%",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}