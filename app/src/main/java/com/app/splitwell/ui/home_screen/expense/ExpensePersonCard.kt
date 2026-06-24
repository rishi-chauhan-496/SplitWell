package com.app.splitwell.ui.home_screen.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpensePersonCard(
    name: String,
    amount: String,
    isEditable: Boolean,
    isIncluded: Boolean = true,
    suffix: String = "₹",
    onToggleInclude: () -> Unit = {},
    onAmountChange: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val alphaModifier = if (isIncluded) 1f else 0.4f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 4.dp)
            .shadow(
                elevation = if (isIncluded) 12.dp else 2.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = if (isDark) Color.White else Color.Black,
                spotColor = if (isDark) Color.White else Color.Black
            ),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = if (isIncluded) 8.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ── Checkbox ──────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isIncluded) Color(0xFF6A1BFF) else Color.Transparent
                    )
                    .border(
                        width = 2.dp,
                        color = if (isIncluded) Color(0xFF6A1BFF) else Color.Gray,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { onToggleInclude() },
                contentAlignment = Alignment.Center
            ) {
                if (isIncluded) {
                    Text("✓", color = Color.White, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // ── Avatar ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Color(0xFF6A1BFF).copy(alpha = if (isIncluded) 0.15f else 0.05f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    color = Color(0xFF6A1BFF).copy(alpha = alphaModifier),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = alphaModifier)
            )

            // ── Amount field ──────────────────────────────────────────────────
            TextField(
                value = if (isIncluded) amount else "0.00",
                onValueChange = onAmountChange,
                readOnly = !isEditable || !isIncluded,
                singleLine = true,
                suffix = { Text(suffix, color = Color.Gray.copy(alpha = alphaModifier)) },
                modifier = Modifier.width(120.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = if (isEditable && isIncluded) Color(0xFF6A1BFF)
                    else Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledTextColor = Color.Gray
                )
            )
        }
    }
}