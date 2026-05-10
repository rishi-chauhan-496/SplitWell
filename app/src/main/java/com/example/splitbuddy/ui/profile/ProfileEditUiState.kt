package com.example.splitbuddy.ui.profile

data class ProfileEditUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,

    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val userName: String = "",
    val contact: String = "",
    val userNameError: String? = null,
    val contactError: String? = null,

    val isSaved: Boolean = false,
    val error: String? = null
)