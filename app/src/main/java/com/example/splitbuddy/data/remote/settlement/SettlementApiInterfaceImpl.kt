package com.example.splitbuddy.data.remote.settlement

import com.example.splitbuddy.data.remote.RetrofitInstance

class SettlementApiInterfaceImpl : SettlementApiInterface {

    private val api = RetrofitInstance.settlementApi

    override suspend fun createSettlement(request: SettlementRequest): SettlementResponse {
        return api.createSettlement(request)
    }
}