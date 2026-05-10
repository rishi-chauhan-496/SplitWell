package com.example.splitbuddy.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitbuddy.data.local.query.UserQuery
import com.example.splitbuddy.data.remote.user.UpdateUserRequest
import com.example.splitbuddy.domain.usecase.user.UpdateUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileEditViewModel(
    private val userQuery: UserQuery,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileEditUiState())
    val state: StateFlow<ProfileEditUiState> = _state

    // Load user from local DB

    fun load(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val user = userQuery.getUser(userId)

                if (user == null) {
                    _state.update {
                        it.copy(isLoading = false, error = "User not found")
                    }
                    return@launch
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        firstName = user.firstName,
                        lastName  = user.lastName,
                        email     = user.email,
                        userName  = user.userName,
                        contact   = user.contact
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // Field updates

    fun onFirstNameChange(value: String) {
        _state.update { it.copy(firstName = value) }
    }

    fun onLastNameChange(value: String) {
        _state.update { it.copy(lastName = value) }
    }

    fun onUserNameChange(value: String) {
        _state.update { it.copy(userName = value, userNameError = null) }
    }

    fun onContactChange(value: String) {
        // Only allow digits, +, spaces, dashes
        if (value.isEmpty() || value.matches(Regex("^[+\\d\\s\\-]*$"))) {
            _state.update { it.copy(contact = value, contactError = null) }
        }
    }

    // Save

    fun save(userId: String) {
        val s = _state.value

        // Validate required fields
        val userNameError = when {
            s.userName.isBlank() -> "Username required"
            s.userName.length < 3 -> "Username must be at least 3 characters"
            else -> null
        }
        val contactError = if (s.contact.isBlank()) "Contact required" else null

        if (userNameError != null || contactError != null) {
            _state.update {
                it.copy(userNameError = userNameError, contactError = contactError)
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                val request = UpdateUserRequest(
                    username  = s.userName.trim(),
                    firstName = s.firstName.trim().ifBlank { null },
                    lastName  = s.lastName.trim().ifBlank { null },
                    contact   = s.contact.trim()
                )

                updateUserUseCase(userId, request)
                _state.update { it.copy(isSaving = false, isSaved = true) }

            } catch (e: Exception) {
                _state.update {
                    it.copy(isSaving = false, error = e.message ?: "Failed to save")
                }
            }
        }
    }
}