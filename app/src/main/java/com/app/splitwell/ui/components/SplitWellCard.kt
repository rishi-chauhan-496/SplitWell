package com.app.splitwell.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.splitwell.ui.theme.Primary

@Composable
fun SplitWellCard(
    modifier: Modifier = Modifier,
    dimmed: Boolean = false,        // true = grayed out (used in settlement paid cards)
    elevation: Dp = 6.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(
                elevation    = elevation,
                shape        = RoundedCornerShape(16.dp),
                ambientColor = if (isDark) Color.White.copy(0.05f)
                else Primary.copy(if (dimmed) 0.03f else 0.1f),
                spotColor    = if (isDark) Color.White.copy(0.05f)
                else Primary.copy(if (dimmed) 0.05f else 0.15f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = if (dimmed) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                else MaterialTheme.colorScheme.surface,
        onClick = onClick ?: {}
        ) {
        content()
    }
}