package com.example.splitbuddy.domain.usecase.user

import com.example.splitbuddy.domain.repository.UserRepository

class GetUserByIdUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): Boolean {
        return repository.getUserById(userId)
    }
}