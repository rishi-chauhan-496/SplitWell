package com.example.splitbuddy.domain.usecase.settlement

import com.example.splitbuddy.data.local.model.Settlement
import com.example.splitbuddy.data.local.query.SettlementQuery
import com.example.splitbuddy.data.remote.settlement.SettlementApiInterface
import com.example.splitbuddy.data.remote.settlement.SettlementRequest

class CreateSettlementUseCase(
    private val settlementApiInterface: SettlementApiInterface,
    private val settlementQuery: SettlementQuery
) {
    suspend operator fun invoke(request: SettlementRequest) {
        // Step 1 — call API
        val response = settlementApiInterface.createSettlement(request)

        // Step 2 — save to local DB so history works offline
        settlementQuery.insertSettlement(
            Settlement(
                id            = response.id,
                tripId        = response.groupId,
                fromUserId    = response.fromUserId,
                toUserId      = response.toUserId,
                settlementAmt = response.amount.toDoubleOrNull() ?: 0.0,
                note          = response.note ?: "",
                createdAt     = response.createdAt,
                updatedAt     = response.updatedAt,
                isDeleted     = false
            )
        )
    }
}