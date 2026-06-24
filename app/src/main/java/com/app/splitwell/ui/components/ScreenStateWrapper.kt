package com.app.splitwell.ui.components

import androidx.compose.runtime.Composable

@Composable
fun ScreenStateWrapper(
    isLoading: Boolean,
    error: String? = null,
    isEmpty: Boolean = false,
    emptyMessage: String = "",
    content: @Composable () -> Unit
) {
    when {
        isLoading     -> LoadingView()
        error != null -> EmptyStateView(message = error)
        isEmpty       -> EmptyStateView(message = emptyMessage)
        else          -> content()
    }
}