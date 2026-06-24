package com.app.splitwell.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.splitwell.R
import com.app.splitwell.data.remote.user.UpdateUserRequest
import com.app.splitwell.data.util.AppError
import com.app.splitwell.data.util.toAppError
import com.app.splitwell.data.util.toWriteMessage
import com.app.splitwell.domain.usecase.user.GetUserProfileUseCase
import com.app.splitwell.domain.usecase.user.UpdateUserUseCase
import com.app.splitwell.ui.util.SnackbarController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileEditViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileEditUiState())
    val state: StateFlow<ProfileEditUiState> = _state

    fun load(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val user = getUserProfileUseCase(userId)

                if (user == null) {
                    _state.update {
                        it.copy(isLoading = false, error = R.string.error_user_not_found)
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
                val errorRes = if (e.toAppError() is AppError.NetworkError) {
                    R.string.error_check_network
                } else {
                    R.string.error_generic_try_again
                }
                _state.update { it.copy(isLoading = false, error = errorRes) }
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

        val userNameError = when {
            s.userName.isBlank() -> R.string.error_username_required
            s.userName.length < 3 -> R.string.error_username_too_short
            else -> null
        }
        val contactError = if (s.contact.isBlank()) R.string.error_contact_required else null

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
                _state.update { it.copy(isSaving = false) }
                SnackbarController.show(e.toAppError().toWriteMessage())
            }
        }
    }
}