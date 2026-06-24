package com.app.splitwell.data.local.model

data class User(
    val id: String,
    val userName: String,
    val firstName: String,
    val lastName: String,
    val contact: String,
    val email: String,
    val socialMediaId: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val isDeleted: Boolean
)