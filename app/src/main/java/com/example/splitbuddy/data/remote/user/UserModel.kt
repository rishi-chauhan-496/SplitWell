package com.example.splitbuddy.data.remote.user

// User response body
data class UserResponse(
    val id: String,
    val username: String,
    val isActive: Boolean,
    val isDeleted: Boolean,
    val firstName: String,
    val lastName: String,
    val contact: String,
    val email: String,
    val socialMediaId: String,
    val createdAt: String,
    val updatedAt: String,
)

// User creation body
data class CreateUserRequest(
    val username: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val contact: String? = null,
    val email: String? = null,
    val socialMediaId: String
)

// User update body
data class UpdateUserRequest(
    val username: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val contact: String? = null,
    val email: String? = null,
    val socialMediaId: String? = null,
    val isActive: Boolean? = null
)

// Friend API response — GET /users/:id/friends
data class FriendResponse(
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val contact: String,
    val isActive: Boolean
)