package com.example.splitbuddy.data.remote.settlement

interface SettlementApiInterface {
    suspend fun createSettlement(request: SettlementRequest): SettlementResponse
}