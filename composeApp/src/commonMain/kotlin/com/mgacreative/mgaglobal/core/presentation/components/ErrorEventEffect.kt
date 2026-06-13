package com.mgacreative.mgaglobal.core.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.mgacreative.mgaglobal.core.error.AppError
import com.mgacreative.mgaglobal.core.presentation.SnackbarManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * A standard Composable side-effect to securely collect error streams (SharedFlow)
 * published from ViewModels within standard Clean Architecture applications.
 *
 * Implements:
 * 1) Collecting errorFlow securely in Composable via standard Compose paradigms.
 * 2) Forwarding to the centralized Singleton [SnackbarManager].
 * 3) Throttling the error spam with [distinctUntilChanged] and lifecycle scoping
 *    so multiple identical emissions back-to-back are naturally deduplicated.
 * 4) Enforcing Recomposition-safe paradigms via [repeatOnLifecycle].
 *
 * No business logic lives here, isolating UI layers.
 */
@Composable
fun ErrorEventEffect(errorFlow: Flow<AppError>) {
    val lifecycleOwner = LocalLifecycleOwner.current
    
    LaunchedEffect(errorFlow, lifecycleOwner) {
        // Collect securely only when the app is fundamentally in the STARTED state,
        // avoiding dropped events or background crash potentials.
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            errorFlow
                // Prevent extreme rapid spamming of exactly similar errors
                .distinctUntilChanged() 
                .collect { error ->
                    // Forwards strictly to the single-source-of-truth 
                    SnackbarManager.showError(error)
                }
        }
    }
}

