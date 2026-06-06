package com.example.splitbuddy.data.remote.settlement

import retrofit2.http.Body
import retrofit2.http.POST

interface SettlementApiService {

    @POST("settlements")
    suspend fun createSettlement(
        @Body request: SettlementRequest
    ): SettlementResponse
}