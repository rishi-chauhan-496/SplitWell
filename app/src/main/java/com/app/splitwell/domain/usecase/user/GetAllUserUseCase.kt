package com.app.splitwell.domain.usecase.user

import com.app.splitwell.data.local.model.User
import com.app.splitwell.domain.repository.UserRepository

class GetAllUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): List<User> {
        return repository.getAllUser()
    }
}