package com.example.splitbuddy.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.splitbuddy.ui.theme.Primary

enum class SplitTab { EQUAL, PERCENT, AMOUNT }

@Composable
fun SplitTabRow(
    selected: SplitTab,
    onSelect: (SplitTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
    ) {
        SplitTabButton(
            label = "Equal",
            isSelected = selected == SplitTab.EQUAL,
            shape = RoundedCornerShape(10.dp, 0.dp, 0.dp, 10.dp),
            modifier = Modifier.weight(1f),
            onClick = { onSelect(SplitTab.EQUAL) }
        )
        SplitTabButton(
            label = "Percent",
            isSelected = selected == SplitTab.PERCENT,
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.weight(1f),
            onClick = { onSelect(SplitTab.PERCENT) }
        )
        SplitTabButton(
            label = "Amount",
            isSelected = selected == SplitTab.AMOUNT,
            shape = RoundedCornerShape(0.dp, 10.dp, 10.dp, 0.dp),
            modifier = Modifier.weight(1f),
            onClick = { onSelect(SplitTab.AMOUNT) }
        )
    }
}

@Composable
private fun SplitTabButton(
    label: String,
    isSelected: Boolean,
    shape: androidx.compose.ui.graphics.Shape,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = shape,
        border = BorderStroke(1.dp, if (isSelected) Primary else Color.Gray),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Primary.copy(alpha = 0.12f)
            else MaterialTheme.colorScheme.background
        ),
        modifier = modifier.fillMaxHeight(),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Primary else Color.Gray,
            fontSize = 12.sp
        )
    }
}