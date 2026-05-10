package com.example.splitbuddy.data.remote.user

interface UserApiInterface {

    suspend fun createUser(request: CreateUserRequest): UserResponse
    suspend fun getAllUser(): List<UserResponse>
    suspend fun getUserById(userId: String): UserResponse
    suspend fun updateUser(userId: String, request: UpdateUserRequest): UserResponse
}