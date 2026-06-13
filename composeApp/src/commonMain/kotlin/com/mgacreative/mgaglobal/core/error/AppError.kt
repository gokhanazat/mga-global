package com.mgacreative.mgaglobal.core.error

import mgaglobal.composeapp.generated.resources.Res
import mgaglobal.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

/**
 * A centralized sealed class representing all possible application-level errors.
 * Compatible with Clean Architecture and pure Kotlin without UI logic attached.
 */
sealed class AppError : Exception() {

    abstract val errorCode: Int
    abstract val userMessage: StringResource

    data object Network : AppError() {
        override val errorCode: Int = 1001
        override val userMessage: StringResource = Res.string.error_network
    }

    data object Timeout : AppError() {
        override val errorCode: Int = 1002
        override val userMessage: StringResource = Res.string.error_timeout
    }

    data object Unauthorized : AppError() {
        override val errorCode: Int = 1003
        override val userMessage: StringResource = Res.string.error_unauthorized
    }

    data object NotFound : AppError() {
        override val errorCode: Int = 1004
        override val userMessage: StringResource = Res.string.error_not_found
    }

    data class Validation(val customMessage: String) : AppError() {
        override val errorCode: Int = 1005
        override val userMessage: StringResource = Res.string.error_validation
        override val message: String
            get() = "Validation Error: $customMessage"
    }

    data class Unknown(val throwable: Throwable? = null) : AppError() {
        override val errorCode: Int = 1000
        override val userMessage: StringResource = Res.string.error_unknown
        override val cause: Throwable? = throwable
        override val message: String?
            get() = throwable?.message ?: "Unknown application error occurred."
    }
}


