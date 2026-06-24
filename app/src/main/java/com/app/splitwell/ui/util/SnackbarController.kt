package com.app.splitwell.ui.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SnackbarController {

    private val _events = MutableSharedFlow<SnackbarEvent>()
    val events = _events.asSharedFlow()

    suspend fun show(message: String, actionLabel: String? = null) {
        _events.emit(SnackbarEvent(message = message, actionLabel = actionLabel))
    }
}

data class SnackbarEvent(
    val message: String,
    val actionLabel: String? = null
)