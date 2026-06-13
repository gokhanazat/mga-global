package com.mgacreative.mgaglobal.core.presentation

import androidx.compose.material3.SnackbarDuration
import com.mgacreative.mgaglobal.core.error.AppError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.jetbrains.compose.resources.StringResource

import mgaglobal.composeapp.generated.resources.*

/**
 * A custom implementation of SnackbarVisuals to carry color styling and action hints.
 */
class AppSnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration,
    val isError: Boolean
) : androidx.compose.material3.SnackbarVisuals

/**
 * Sealed class representing internal Snackbar events decoupled from the UI.
 */
sealed interface SnackbarEvent {
    data class Message(
        val messageRes: StringResource,
        val duration: SnackbarDuration = SnackbarDuration.Short,
        val actionLabelRes: StringResource? = null,
        val action: (() -> Unit)? = null
    ) : SnackbarEvent

    data class Error(
        val error: AppError,
        val actionLabelRes: StringResource? = null,
        val action: (() -> Unit)? = null
    ) : SnackbarEvent
}

/**
 * A centralized singleton manager for showing Snackbars from anywhere in the app 
 * without injecting Context or UI dependencies.
 */
object SnackbarManager {
    // Note: Use extraBufferCapacity to safely tryEmit from synchronous ViewModel operations
    private val _events = MutableSharedFlow<SnackbarEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    /**
     * Publishes an Error event configured by the AppError type logic including optional quick actions.
     */
    fun showError(error: AppError, overrideActionLabelRes: StringResource? = null, action: (() -> Unit)? = null) {
        val actionLabelRes = overrideActionLabelRes ?: when (error) {
            is AppError.Unauthorized -> Res.string.action_login
            is AppError.Network -> Res.string.action_retry
            else -> null
        }
        _events.tryEmit(SnackbarEvent.Error(error, actionLabelRes, action))
    }

    /**
     * Publishes standard standard string resource messages.
     */
    fun showMessage(
        messageRes: StringResource, 
        duration: SnackbarDuration = SnackbarDuration.Short,
        actionLabelRes: StringResource? = null,
        action: (() -> Unit)? = null
    ) {
        _events.tryEmit(SnackbarEvent.Message(messageRes, duration, actionLabelRes, action))
    }

    /**
     * Determines proper standard SnackbarDuration mapped from error type logic.
     */
    fun getDurationForError(error: AppError): SnackbarDuration {
        return when (error) {
            is AppError.Network -> SnackbarDuration.Short
            is AppError.Validation -> SnackbarDuration.Short
            is AppError.Unauthorized -> SnackbarDuration.Long
            is AppError.Unknown -> SnackbarDuration.Long
            else -> SnackbarDuration.Short
        }
    }
}


