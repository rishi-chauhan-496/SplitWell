package com.app.splitwell.ui.profile

import androidx.annotation.StringRes

data class ProfileEditUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,

    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val userName: String = "",
    val contact: String = "",
    @StringRes val userNameError: Int? = null,
    @StringRes val contactError: Int? = null,

    val isSaved: Boolean = false,
    @StringRes val error: Int? = null
)