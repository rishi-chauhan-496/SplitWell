package com.example.splitbuddy.ui.home_screen.top_bar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TopBarViewModel : ViewModel() {

    private val _state = MutableStateFlow(TopBarState())
    val state: StateFlow<TopBarState> = _state

    fun update(state: TopBarState) {
        _state.value = state
    }
}