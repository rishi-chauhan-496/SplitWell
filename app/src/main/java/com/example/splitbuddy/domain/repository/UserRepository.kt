package com.example.splitbuddy.domain.repository

import com.example.splitbuddy.data.local.model.User
import com.example.splitbuddy.data.remote.user.CreateUserRequest
import com.example.splitbuddy.data.remote.user.UpdateUserRequest

interface UserRepository {

    suspend fun getOrCreateUser(request: CreateUserRequest): String
    suspend fun getAllUser(): List<User>
    suspend fun getUserById(userId: String): Boolean
    suspend fun updateUser(userId: String, request: UpdateUserRequest): Boolean
}