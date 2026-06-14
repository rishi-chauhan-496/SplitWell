package com.example.splitbuddy.data.remote.user

import com.example.splitbuddy.data.remote.RetrofitInstance

class UserApiInterfaceImpl: UserApiInterface {

    private val api = RetrofitInstance.userApi

    override suspend fun createUser(request: CreateUserRequest): UserResponse {
        val result =  api.createUser(request)
        return result
    }

    override suspend fun getAllUser(): List<UserResponse> {
        val result = api.getAllUser()
        return result
    }

    override suspend fun getUserById(userId: String): UserResponse {
        return api.getUserById(userId)
    }

    override suspend fun updateUser(userId: String, request: UpdateUserRequest): UserResponse {
        return api.updateUser(userId, request)
    }

    override suspend fun getUserFriends(userId: String): List<FriendResponse> {
        return api.getUserFriends(userId)
    }
}