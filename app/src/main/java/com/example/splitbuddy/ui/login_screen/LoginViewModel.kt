package com.example.splitbuddy.ui.login_screen

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.data.remote.user.CreateUserRequest
import com.example.splitbuddy.domain.usecase.user.GetOrCreateUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.core.content.edit

class LoginViewModel(
    private val getOrCreateUserUseCase: GetOrCreateUserUseCase,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state

    fun handleGoogleSignIn(
        googleUid: String,
        email: String?,
        displayName: String?
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val nameParts = displayName?.split(" ") ?: emptyList()
                val firstName = nameParts.firstOrNull()
                val lastName  = if (nameParts.size > 1) nameParts.drop(1).joinToString(" ") else null

                val request = CreateUserRequest(
                    firstName    = firstName,
                    lastName     = lastName,
                    email        = email,
                    socialMediaId = googleUid
                )

                val userId = getOrCreateUserUseCase(request)

                // Save userId to SharedPreferences
                sharedPreferences.edit {
                    putString("userId", userId)
                    putBoolean("isNewLogin", true)
                }

                _state.update { it.copy(isLoading = false, isLoggedIn = true) }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Sign in failed. Please try again."
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}