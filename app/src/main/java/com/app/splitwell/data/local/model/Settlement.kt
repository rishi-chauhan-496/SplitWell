package com.app.splitwell.data.local.model

data class Settlement(
    val id: String,
    val tripId: String,
    val fromUserId: String,
    val toUserId: String,
    val settlementAmt: Double,
    val note: String,
    val createdAt: String,
    val updatedAt: String,
    val isDeleted: Boolean
)