package com.mgacreative.mgaglobal.core.auth

/**
 * Centralized Role-Permission Matrix serving as the single source of truth for authorization.
 * Provides a structured way to determine if a specific [Role] has a specific [Permission].
 * 
 * Design Principles:
 * - Single source of truth.
 * - Easy to extend by adding sets and updating the map.
 * - Decoupled from UI logic.
 */
object RolePermissionMatrix {

    private val superAdminPermissions = Permission.entries.toSet()

    private val adminPermissions = Permission.entries.filter { 
        it != Permission.SYSTEM_SETTINGS 
    }.toSet()

    private val moderatorPermissions = setOf(
        Permission.DASHBOARD_VIEW,
        Permission.DIGITAL_SHOWROOM_VIEW,
        Permission.DIGITAL_SHOWROOM_EDIT,
        Permission.TRAINING_VIEW,
        Permission.TRAINING_MANAGE,
        Permission.CERTIFICATE_GENERATE,
        Permission.MARKETPLACE_VIEW,
        Permission.B2B_VIEW
    )

    private val memberPermissions = setOf(
        Permission.DASHBOARD_VIEW,
        Permission.DIGITAL_SHOWROOM_VIEW,
        Permission.B2B_VIEW,
        Permission.B2B_MATCH_CREATE,
        Permission.APPOINTMENT_VIEW,
        Permission.TRAINING_VIEW,
        Permission.MARKETPLACE_VIEW
    )

    private val guestPermissions = setOf(
        Permission.DIGITAL_SHOWROOM_VIEW,
        Permission.TRAINING_VIEW
    )

    private val matrix: Map<Role, Set<Permission>> = mapOf(
        Role.SUPER_ADMIN to superAdminPermissions,
        Role.ADMIN to adminPermissions,
        Role.MODERATOR to moderatorPermissions,
        Role.MEMBER to memberPermissions,
        Role.GUEST to guestPermissions
    )

    /**
     * Checks if a role has the required permission.
     */
    fun hasPermission(role: Role, permission: Permission): Boolean {
        return matrix[role]?.contains(permission) ?: false
    }

    /**
     * Returns the full list of permissions for a given role.
     */
    fun getPermissionsForRole(role: Role): Set<Permission> {
        return matrix[role] ?: emptySet()
    }
}

