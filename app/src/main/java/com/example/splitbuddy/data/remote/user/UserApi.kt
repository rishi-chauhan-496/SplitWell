package com.example.splitbuddy.data.remote.user

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApiService {

    @POST("users")
    suspend fun createUser(
        @Body request: CreateUserRequest
    ): UserResponse

    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") userId: String
    ): UserResponse

    @GET("users")
    suspend fun getAllUser(): List<UserResponse>

    @PATCH("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: String,
        @Body request: UpdateUserRequest
    ): UserResponse

    @GET("users/{id}/friends")
    suspend fun getUserFriends(
        @Path("id") userId: String
    ): List<FriendResponse>
}