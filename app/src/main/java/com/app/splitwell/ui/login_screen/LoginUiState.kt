package com.app.splitwell.ui.login_screen

import androidx.annotation.StringRes

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    @StringRes val error: Int? = null
)