package com.app.splitwell.data.repository

import com.app.splitwell.data.local.model.User
import com.app.splitwell.data.local.query.UserQuery
import com.app.splitwell.data.remote.user.CreateUserRequest
import com.app.splitwell.data.remote.user.UpdateUserRequest
import com.app.splitwell.data.remote.user.UserApiInterface
import com.app.splitwell.domain.repository.UserRepository
import kotlin.String

class UserRepositoryImpl(
    val userApiInterface: UserApiInterface,
    val userQuery: UserQuery
) : UserRepository {

    override suspend fun getOrCreateUser(request: CreateUserRequest): String {
        val data = userApiInterface.createUser(request)
        userQuery.insertUser(data)
        return data.id
    }

    override suspend fun getAllUser(): List<User> {
        try {
            // Try API — sync to local DB if online
            val users = userApiInterface.getAllUser()
            users.forEach { user -> userQuery.insertUser(user) }
        } catch (_: Exception) {
            // Offline — fall through to local data
        }

        // Always return from local DB
        return userQuery.getALLUser()
    }

    override suspend fun getUserById(userId: String): Boolean {
        return try {
            val user = userApiInterface.getUserById(userId)
            userQuery.insertUser(user)
            true
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun updateUser(userId: String, request: UpdateUserRequest) {
        val response = userApiInterface.updateUser(userId, request)
        userQuery.insertUser(response)
    }

    suspend fun syncUsers(userIds: List<String>) {
        userIds.forEach { userId ->
            try {
                val user = userApiInterface.getUserById(userId)
                userQuery.insertUser(user)
            } catch (_: Exception) { }
        }
    }

}