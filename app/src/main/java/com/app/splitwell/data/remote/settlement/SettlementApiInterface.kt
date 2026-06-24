package com.app.splitwell.data.remote.settlement

interface SettlementApiInterface {
    suspend fun createSettlement(request: SettlementRequest): SettlementResponse
    suspend fun deleteSettlement(settlementId: String)
}