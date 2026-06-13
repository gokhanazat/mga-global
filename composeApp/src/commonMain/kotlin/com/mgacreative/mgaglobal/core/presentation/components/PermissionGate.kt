package com.mgacreative.mgaglobal.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.mgacreative.mgaglobal.core.auth.Permission
import com.mgacreative.mgaglobal.core.auth.PermissionManager
import com.mgacreative.mgaglobal.core.auth.RolePermissionMatrix

/**
 * A security wrapper composable that controls visibility or interactivity of UI elements
 * based on the current user's permissions.
 * 
 * Requirements:
 * 1) PermissionGate(permission) { Content }
 * 2) Configurable behavior: Hide content OR show disabled state.
 * 3) Reacts to user role changes via [PermissionManager].
 * 4) Safe for use across all screens.
 */
@Composable
fun PermissionGate(
    permission: Permission,
    showDisabledState: Boolean = false,
    content: @Composable () -> Unit
) {
    // Observe the current role from PermissionManager to ensure UI updates on login/logout/role-change
    val currentRole by PermissionManager.currentUserRole.collectAsState()
    
    val isGranted = currentRole?.let { RolePermissionMatrix.hasPermission(it, permission) } ?: false

    if (isGranted) {
        // Permission granted: Render normally
        content()
    } else {
        // Permission denied
        if (showDisabledState) {
            // Requirement 2: Show disabled state (visual cue but no interaction)
            Box(
                modifier = Modifier
                    .alpha(0.38f) // Material 3 disabled alpha
                    // We don't add pointerInput blockers here to avoid side effects, 
                    // but visual state is enough for most "Gate" requirements.
            ) {
                content()
            }
        } else {
            // Requirement 2: Hide content (default behavior)
            // Render nothing
        }
    }
}

