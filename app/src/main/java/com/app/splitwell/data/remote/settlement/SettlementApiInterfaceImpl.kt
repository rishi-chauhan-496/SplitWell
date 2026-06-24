package com.app.splitwell.data.remote.settlement

import com.app.splitwell.data.remote.RetrofitInstance

class SettlementApiInterfaceImpl : SettlementApiInterface {

    private val api = RetrofitInstance.settlementApi

    override suspend fun createSettlement(request: SettlementRequest): SettlementResponse {
        return api.createSettlement(request)
    }

    override suspend fun deleteSettlement(settlementId: String) {
        api.deleteSettlement(settlementId)
    }
}