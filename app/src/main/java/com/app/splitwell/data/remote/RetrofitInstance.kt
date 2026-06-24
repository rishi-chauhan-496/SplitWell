package com.app.splitwell.data.remote

import android.util.Log
import com.app.splitwell.BuildConfig
import com.app.splitwell.data.remote.expense.ExpenseApiService
import com.app.splitwell.data.remote.group.GroupApiService
import com.app.splitwell.data.remote.invite.InviteApiService
import com.app.splitwell.data.remote.user.UserApiService
import com.app.splitwell.data.remote.settlement.SettlementApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://api.pratikprajapati.cloud:3000/api/"

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("OkHttp", message)
    }.apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
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