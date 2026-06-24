package com.app.splitwell.domain.repository

import com.app.splitwell.data.local.model.User
import com.app.splitwell.data.remote.user.CreateUserRequest
import com.app.splitwell.data.remote.user.UpdateUserRequest

interface UserRepository {

    suspend fun getOrCreateUser(request: CreateUserRequest): String
    suspend fun getAllUser(): List<User>
    suspend fun getUserById(userId: String): Boolean
    suspend fun updateUser(userId: String, request: UpdateUserRequest)
}