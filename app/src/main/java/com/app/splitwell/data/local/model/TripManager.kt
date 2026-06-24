package com.app.splitwell.data.local.model

data class TripManager(
    val id: String,
    val tripId: String,
    val userId: String,
    val userName: String = "",
    val role: String,
    val isActive: Boolean,
    val joinedAt: String,
    val leftAt: String?,
    val createdAt: String,
    val updatedAt: String,
    val isDeleted: Boolean
)