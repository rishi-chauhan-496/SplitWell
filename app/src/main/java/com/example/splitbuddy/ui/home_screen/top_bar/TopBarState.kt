package com.example.splitbuddy.ui.home_screen.top_bar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

data class TopBarState(
    val isVisible: Boolean = true,
    val title: String = "",
    val showBack: Boolean = false,
    val actions: (@Composable RowScope.() -> Unit)? = null
)