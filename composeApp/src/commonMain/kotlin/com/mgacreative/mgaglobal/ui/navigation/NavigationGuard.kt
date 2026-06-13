package com.mgacreative.mgaglobal.ui.navigation

import androidx.navigation.NavController
import com.mgacreative.mgaglobal.core.auth.Permission
import com.mgacreative.mgaglobal.core.auth.PermissionManager
import com.mgacreative.mgaglobal.core.presentation.SnackbarManager
import com.mgacreative.mgaglobal.core.error.AppError
import mgaglobal.composeapp.generated.resources.Res
import mgaglobal.composeapp.generated.resources.error_unauthorized

/**
 * A centralized navigation guard to enforce permission checks before navigating.
 * 
 * Requirements:
 * 1) Check permission before navigate.
 * 2) Show "You are not authorized" snackbar if missing.
 * 3) Safe and non-crashing.
 */
fun NavController.safeNavigate(
    screen: Screen,
    routeOverride: String? = null
) {
    val permission = screen.requiredPermission
    
    if (permission == null || PermissionManager.hasPermission(permission)) {
        // Authorized or no permission required
        try {
            this.navigate(routeOverride ?: screen.route)
        } catch (e: Exception) {
            // 3) No crash allowed
            println("NavigationGuard: Failed to navigate to ${screen.route} - ${e.message}")
        }
    } else {
        // 2) Unauthorized
        SnackbarManager.showError(AppError.Unauthorized)
    }
}

/**
 * Extension for simple string routes with explicit permission check.
 */
fun NavController.safeNavigate(
    route: String,
    requiredPermission: Permission
) {
    if (PermissionManager.hasPermission(requiredPermission)) {
        try {
            this.navigate(route)
        } catch (e: Exception) {
            println("NavigationGuard: Failed to navigate to $route - ${e.message}")
        }
    } else {
        SnackbarManager.showError(AppError.Unauthorized)
    }
}


