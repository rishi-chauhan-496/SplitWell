package com.app.splitwell.ui.home_screen.expense.expense_creating

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.splitwell.ui.components.AppTextField
import com.app.splitwell.ui.components.GradientButton
import com.app.splitwell.ui.components.PaidByDropdown
import com.app.splitwell.R

@Composable
fun ExpenseScreen1(
    state: ExpenseUiState,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onPaidByChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Everything that can grow lives in here, and scrolls if it has to.
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.Expense_Screen_1_Title),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp
            )
            Text(
                text = stringResource(R.string.Expense_Screen_1_Title_2),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.Expense_Screen_1_Text),
                color = Color.Gray,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                label = stringResource(R.string.Expense_Screen_1_TextFiled),
                value = state.title,
                onValueChange = onTitleChange,
                isError = state.titleError != null,
                errorMessage = state.titleError
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                label = stringResource(R.string.Expense_Screen_1_TextFiled_2),
                value = state.amount,
                onValueChange = onAmountChange,
                isError = state.amountError != null,
                errorMessage = state.amountError
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.Expense_Screen_1_TextFiled_3),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 12.sp
            )
            PaidByDropdown(
                members = state.members,
                selectedUserId = state.paidByUserId,
                onSelect = onPaidByChange,
                isError = state.paidByError != null,
                modifier = Modifier.fillMaxWidth()
            )
            state.paidByError?.let {
                Text(it, color = Color.Red, fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                label = stringResource(R.string.Expense_Screen_1_TextFiled_4),
                value = state.description,
                onValueChange = onDescriptionChange,
                singleLine = false,
                fieldHeight = 100.dp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        GradientButton(
            text = stringResource(R.string.continue_),
            onClick = onNext,
            enabled = state.isFormFilled
        )
    }
}