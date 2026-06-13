package com.mgacreative.mgaglobal.core.error

import kotlinx.coroutines.CancellationException

/**
 * Standard Result abstraction ensuring clean architecture boundaries.
 */
sealed interface AppResult<out T> {
    data class Success<out T>(val data: T) : AppResult<T>
    data class Error(val error: AppError) : AppResult<Nothing>

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Error

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }
}

/**
 * A centralized suspend try-catch abstraction for all external, repository, and data source calls.
 * 
 * Catch mapping strategy conceptually covers:
 * - IOException -> Network
 * - SocketTimeoutException -> Timeout
 * - HttpException -> Unauthorized (401), NotFound (404)
 * - IllegalArgumentException -> Validation
 * 
 * Since true network exceptions (java.io.IOException, io.ktor.client.plugins.ResponseException)
 * are not necessarily available in standard Common Multiplatform, this relies on class simpleName
 * and message parsing for idiomatic pure Kotlin compatibility without depending on Ktor/Java APIs.
 */
suspend inline fun <T> safeCall(crossinline action: suspend () -> T): AppResult<T> {
    return try {
        AppResult.Success(action())
    } catch (e: Exception) {
        // We shouldn't silently swallow CancellationException in Coroutines
        if (e is CancellationException) throw e
        e.printStackTrace()
        println("safeCall Error: ${e.message}")
        AppResult.Error(e.mapToAppError())
    }
}

/**
 * Maps any intercepted standard or 3rd-party exceptions into our centralized [AppError] system.
 */
@PublishedApi
internal fun Exception.mapToAppError(): AppError {
    val className = this::class.simpleName ?: ""
    val msg = this.message ?: ""

    return when {
        this is IllegalArgumentException -> AppError.Validation(customMessage = msg.ifBlank { "Invalid input provided." })

        className.contains("IOException") || className.contains("ConnectException") -> AppError.Network
        className.contains("TimeoutException") || className.contains("SocketTimeoutException") -> AppError.Timeout

        // Naive HTTP Exception mapping strategy for custom App Architecture without specific libraries 
        className.contains("HttpException") || className.contains("ResponseException") -> {
            when {
                msg.contains("401") -> AppError.Unauthorized
                msg.contains("404") -> AppError.NotFound
                else -> AppError.Unknown(this)
            }
        }
        
        else -> AppError.Unknown(throwable = this)
    }
}

