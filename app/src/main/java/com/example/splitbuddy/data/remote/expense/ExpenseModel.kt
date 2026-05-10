package com.example.splitbuddy.data.remote.expense

data class ExpenseResponse(
    val id: String,
    val title: String,
    val description: String?,
    val amount: String,
    val splitMethod: String,
    val paidByUser: String,
    val groupId: String,
    val currencyCode: String,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val shares: List<Share>,
)

data class Share(
    val id: String,
    val expenseId: String,
    val userId: String,
    val shareAmount: String,
    val sharePercent: String?,
    val isIncluded: Boolean,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String,
)

data class ExpenseRequest(
    val title: String,
    val description: String?,
    val amount: String,
    val splitMethod: String,
    val paidByUser: String,
    val groupId: String,
    val currencyCode: String = "INR",
    val shares: List<ShareRequest>
)

data class ShareRequest(
    val userId: String,
    val shareAmount: String,
    val isIncluded: Boolean,
    val sharePercent: String? = null
)