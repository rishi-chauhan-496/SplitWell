package com.app.splitwell.data.local.model

data class ExpenseShare(
    val id: String,
    val expenseId: String,
    val userId: String,
    val userName: String = "",
    val sharedAmount: Double,
    val sharedPercent: Double,
    val isIncluded: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val isDeleted: Boolean
)