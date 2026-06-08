package com.example.splitbuddy.data.util

sealed class Resource<T> {

    data class Success<T>(
        val data: T
    ) : Resource<T>()

    data class Error<T>(
        val error: AppError,
        val data: T? = null    // stale local data — shown while offline
    ) : Resource<T>()

    class Loading<T> : Resource<T>()
}