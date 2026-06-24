package com.app.splitwell.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.splitwell.ui.theme.Primary

@Composable
fun RunningTotalBar(
    assigned: Double,
    total: Double,
    unit: String,
    modifier: Modifier = Modifier
) {
    val progress = if (total > 0) (assigned / total).toFloat().coerceIn(0f, 1f) else 0f
    val isOver = assigned > total + 0.01
    val remaining = total - assigned
    val barColor = if (isOver) Color.Red else Primary

    Column(modifier = modifier) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = barColor,
            trackColor = Color.LightGray
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$unit${"%.2f".format(assigned)} assigned",
                fontSize = 11.sp,
                color = barColor,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = if (isOver) "$unit${"%.2f".format(-remaining)} over"
                else "$unit${"%.2f".format(remaining)} remaining",
                fontSize = 11.sp,
                color = if (isOver) Color.Red else Color.Gray
            )
        }
    }
}