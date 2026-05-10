package com.example.splitbuddy.domain.usecase.user

import com.example.splitbuddy.data.local.model.User
import com.example.splitbuddy.domain.repository.UserRepository

class GetAllUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): List<User> {
        return repository.getAllUser()
    }
}