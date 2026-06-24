package com.app.splitwell.domain.model

data class SettlementSuggestion(
    val fromUserId: String,   // person who pays
    val toUserId: String,     // person who receives
    val amount: Double
)