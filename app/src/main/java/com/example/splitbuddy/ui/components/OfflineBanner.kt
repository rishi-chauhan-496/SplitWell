package com.example.splitbuddy.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OfflineBanner(isOffline: Boolean) {
    AnimatedVisibility(visible = isOffline) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF3CD))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "⚠ You're offline — showing cached data",
                fontSize = 12.sp,
                color = Color(0xFF856404)
            )
        }
    }
}