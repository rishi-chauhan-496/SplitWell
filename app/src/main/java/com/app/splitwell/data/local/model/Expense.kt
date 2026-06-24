package com.app.splitwell.data.local.model

data class Expense(
    val id: String,
    val title: String,
    val description: String?,
    val amount: Double,
    val splitMethod: String,
    val paidByUser: String,
    val paidByUserName: String = "",
    val tripId: String,
    val currencyCode: String,
    val createdAt: String,
    val updatedAt: String,
    val isDeleted: Boolean
)