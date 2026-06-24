package com.app.splitwell.domain.usecase.user

import com.app.splitwell.data.remote.user.UpdateUserRequest
import com.app.splitwell.domain.repository.UserRepository

class UpdateUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String, request: UpdateUserRequest) {
        repository.updateUser(userId, request)
    }
}