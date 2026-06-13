package com.mgacreative.mgaglobal.core.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton manager to bridge the current user context with the [RolePermissionMatrix].
 */
object PermissionManager {

    private val _currentUserRole = MutableStateFlow<Role?>(null)
    /**
     * Observable state of the current user's role.
     */
    val currentUserRole: StateFlow<Role?> = _currentUserRole.asStateFlow()

    /**
     * Updates the current session's role.
     */
    fun updateRole(role: Role?) {
        _currentUserRole.value = role
    }

    /**
     * Checks if the current user has a specific permission.
     */
    fun hasPermission(permission: Permission): Boolean {
        val role = _currentUserRole.value ?: return false
        return try {
            RolePermissionMatrix.hasPermission(role, permission)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Checks if the current user has at least one of the specified permissions.
     */
    fun hasAny(permissions: List<Permission>): Boolean {
        if (permissions.isEmpty()) return false
        val role = _currentUserRole.value ?: return false
        return try {
            permissions.any { RolePermissionMatrix.hasPermission(role, it) }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Checks if the current user holds all specified permissions.
     */
    fun hasAll(permissions: List<Permission>): Boolean {
        if (permissions.isEmpty()) return true
        val role = _currentUserRole.value ?: return false
        return try {
            permissions.all { RolePermissionMatrix.hasPermission(role, it) }
        } catch (e: Exception) {
            false
        }
    }
}

