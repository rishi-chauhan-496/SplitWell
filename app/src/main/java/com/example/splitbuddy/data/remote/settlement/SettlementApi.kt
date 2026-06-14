package com.example.splitbuddy.data.remote.settlement

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface SettlementApiService {

    @POST("settlements")
    suspend fun createSettlement(
        @Body request: SettlementRequest
    ): SettlementResponse

    @DELETE("settlements/{id}")
    suspend fun deleteSettlement(
        @Path("id") settlementId: String
    )
}