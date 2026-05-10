package com.example.splitbuddy.data.remote.expense

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ExpenseApiService {

    @POST("expenses")
    suspend fun createExpense(
        @Body request: ExpenseRequest
    ): ExpenseResponse

    @GET("expenses")
    suspend fun getAllExpense(): List<ExpenseResponse>

    @GET("expenses/{id}")
    suspend fun getExpenseById(
        @Path("id") id: String
    ): ExpenseResponse

    @PUT("expenses/{id}")
    suspend fun updateExpense(
        @Path("id") id: String,
        @Body request: ExpenseRequest
    ): ExpenseResponse

    @DELETE("expenses/{id}")
    suspend fun deleteExpense(
        @Path("id") id: String
    ): ExpenseResponse
}
