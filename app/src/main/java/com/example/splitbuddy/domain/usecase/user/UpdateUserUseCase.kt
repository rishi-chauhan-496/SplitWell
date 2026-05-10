package com.example.splitbuddy.domain.usecase.user

import com.example.splitbuddy.data.remote.user.UpdateUserRequest
import com.example.splitbuddy.domain.repository.UserRepository

class UpdateUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String, request: UpdateUserRequest): Boolean {
        return repository.updateUser(userId, request)
    }
}