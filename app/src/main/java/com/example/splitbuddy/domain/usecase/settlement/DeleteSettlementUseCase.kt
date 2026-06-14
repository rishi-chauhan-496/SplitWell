package com.example.splitbuddy.domain.usecase.settlement

import com.example.splitbuddy.data.local.query.SettlementQuery
import com.example.splitbuddy.data.remote.settlement.SettlementApiInterface

class DeleteSettlementUseCase(
    private val settlementApiInterface: SettlementApiInterface,
    private val settlementQuery: SettlementQuery
) {
    suspend operator fun invoke(settlementId: String) {
        // Step 1 — delete from server
        settlementApiInterface.deleteSettlement(settlementId)

        // Step 2 — delete from local DB
        settlementQuery.deleteSettlement(settlementId)
    }
}