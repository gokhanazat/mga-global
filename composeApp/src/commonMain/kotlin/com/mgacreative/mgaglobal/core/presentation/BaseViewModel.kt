package com.mgacreative.mgaglobal.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mgacreative.mgaglobal.core.error.AppError
import com.mgacreative.mgaglobal.core.error.AppResult
import com.mgacreative.mgaglobal.core.error.safeCall
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Standardized Compose-first approach ViewModel that enforces a single,
 * reactive flow of state and error propagation using Clean Architecture principles.
 */
abstract class BaseViewModel<State>(initialState: State) : ViewModel() {

    // 1) The main structured state reflecting what is drawn on the screen.
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    // 2) The side-effect stream dedicated only to error propagation.
    // Unlike states, these are events that the UI should consume and react to (e.g. snackbars).
    private val _errorFlow = MutableSharedFlow<AppError>()
    val errorFlow: SharedFlow<AppError> = _errorFlow.asSharedFlow()

    /**
     * Updates the current UI state.
     */
    protected fun updateState(update: (State) -> State) {
        _uiState.value = update(_uiState.value)
    }

    /**
     * Executes business logic encapsulated safely inside the `safeCall` abstraction.
     * Keeps network/data errors decoupled from UI layers.
     * Automatically emits AppError to the standalone errorFlow without interrupting state.
     */
    protected fun <T> executeSafeCall(
        action: suspend () -> T,
        onSuccess: (T) -> Unit
    ) {
        viewModelScope.launch {
            when (val result = safeCall { action() }) {
                is AppResult.Success -> {
                    onSuccess(result.data)
                }
                is AppResult.Error -> {
                    emitError(result.error)
                }
            }
        }
    }

    /**
     * Exits safely manually triggered or pre-handled [AppError] variants independently.
     */
    protected fun emitError(error: AppError) {
        viewModelScope.launch {
            _errorFlow.emit(error)
        }
    }
}

