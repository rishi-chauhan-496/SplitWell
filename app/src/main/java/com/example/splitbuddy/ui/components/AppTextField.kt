package com.example.splitbuddy.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.splitbuddy.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    fieldHeight: Dp = Dp.Unspecified
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 12.sp
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            readOnly = readOnly,
            isError = isError,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (fieldHeight != Dp.Unspecified)
                        Modifier.height(fieldHeight)
                    else Modifier
                ),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Primary,
                unfocusedIndicatorColor = Primary,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledIndicatorColor = Color.Gray,
                disabledContainerColor = Color.Transparent
            )
        )
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 11.sp
            )
        }
    }
}