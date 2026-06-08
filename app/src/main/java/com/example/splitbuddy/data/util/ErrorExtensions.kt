package com.example.splitbuddy.data.util

import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

// ── Convert raw exception → typed AppError ────────────────────────────────────
fun Exception.toAppError(): AppError = when (this) {
    is UnknownHostException   -> AppError.NetworkError
    is ConnectException       -> AppError.NetworkError
    is SocketTimeoutException -> AppError.NetworkError
    is HttpException          -> when (code()) {
        401          -> AppError.AuthError
        in 400..499  -> AppError.ClientError(code())
        in 500..599  -> AppError.ServerError(code())
        else         -> AppError.UnknownError(message())
    }
    else -> AppError.UnknownError(message)
}

// ── Convert AppError → user-friendly message ──────────────────────────────────
fun AppError.toMessage(): String = when (this) {
    is AppError.NetworkError  ->
        "No internet connection. Showing cached data."
    is AppError.AuthError     ->
        "Session expired. Please login again."
    is AppError.ServerError   ->
        "Server error (${code}). Please try again later."
    is AppError.ClientError   -> when (code) {
        400  -> "Invalid request. Please try again."
        404  -> "Not found."
        409  -> "Already exists."
        else -> "Request failed (${code})."
    }
    is AppError.UnknownError  ->
        "Something went wrong. Please try again."
}

// ── Write error message — shown in Snackbar ───────────────────────────────────
// Different from read errors — more actionable
fun AppError.toWriteMessage(): String = when (this) {
    is AppError.NetworkError ->
        "No internet connection. Please connect and try again."
    is AppError.AuthError    ->
        "Session expired. Please login again."
    is AppError.ServerError  ->
        "Server error. Please try again later."
    is AppError.ClientError  -> when (code) {
        400  -> "Invalid data. Please check your input."
        404  -> "Not found. Please refresh and try again."
        else -> "Request failed. Please try again."
    }
    is AppError.UnknownError ->
        "Something went wrong. Please try again."
}