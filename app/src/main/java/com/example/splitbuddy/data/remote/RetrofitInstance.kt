package com.example.splitbuddy.data.remote

import com.example.splitbuddy.data.remote.expense.ExpenseApiService
import com.example.splitbuddy.data.remote.group.GroupApiService
import com.example.splitbuddy.data.remote.invite.InviteApiService
import com.example.splitbuddy.data.remote.user.UserApiService
import com.example.splitbuddy.data.remote.settlement.SettlementApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://10.158.176.251:3000/api/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val groupApi: GroupApiService by lazy {
        retrofit.create(GroupApiService::class.java)
    }

    val userApi: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }

    val expenseApi: ExpenseApiService by lazy {
        retrofit.create(ExpenseApiService::class.java)
    }

    val settlementApi: SettlementApiService by lazy {
        retrofit.create(SettlementApiService::class.java)
    }

    val inviteApi: InviteApiService by lazy {
        retrofit.create(InviteApiService::class.java)
    }
}