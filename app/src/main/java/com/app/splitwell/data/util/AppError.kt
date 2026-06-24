package com.app.splitwell.data.util

sealed class AppError {

    // No internet / timeout / host unreachable
    object NetworkError : AppError()

    // 401 — session expired
    object AuthError : AppError()

    // 4xx — bad request, not found etc
    data class ClientError(val code: Int) : AppError()

    // 5xx — server side problem
    data class ServerError(val code: Int) : AppError()

    // Anything unexpected
    data class UnknownError(val message: String?) : AppError()
}