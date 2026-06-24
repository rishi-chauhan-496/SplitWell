package com.app.splitwell.domain.usecase.user

import com.app.splitwell.data.remote.user.CreateUserRequest
import com.app.splitwell.domain.repository.UserRepository

class GetOrCreateUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(request: CreateUserRequest): String {
        return repository.getOrCreateUser(request)
    }
}