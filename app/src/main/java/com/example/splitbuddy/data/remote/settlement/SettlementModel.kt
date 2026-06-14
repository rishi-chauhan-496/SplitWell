package com.example.splitbuddy.data.remote.settlement

data class SettlementRequest(
    val groupId: String,
    val fromUserId: String,
    val toUserId: String,
    val amount: String,
    val note: String? = null
)

data class SettlementResponse(
    val id: String,
    val groupId: String,
    val fromUserId: String,
    val toUserId: String,
    val amount: String,
    val note: String?,
    val createdAt: String,
    val updatedAt: String
)