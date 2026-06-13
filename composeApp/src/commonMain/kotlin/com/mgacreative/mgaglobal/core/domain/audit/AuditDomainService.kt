package com.mgacreative.mgaglobal.core.domain.audit

import com.mgacreative.mgaglobal.core.audit.ActionType
import com.mgacreative.mgaglobal.core.audit.AuditEvent
import com.mgacreative.mgaglobal.core.audit.safeAuditLog
import com.mgacreative.mgaglobal.core.auth.Role

/**
 * Domain boundary for triggering application-level Audit Logs.
 * 
 * Strict compliance with requirements:
 * 1) Only specific domain actions are exposed.
 * 2) Entirely abstracted from the UI layer.
 * 3) Feature flag integrated.
 * 4) Triggers underlying `safeAuditLog` autonomously.
 */
object AuditDomainService {

    /**
     * Auth actions (Login / Logout)
     */
    fun logLoginAction(userId: String, userRole: String, isLogin: Boolean) {
        if (!AuditFeatureFlags.IS_AUDIT_LOGGING_ENABLED) return
        
        safeAuditLog {
            AuditEvent(
                userId = userId,
                userRole = userRole,
                actionType = if (isLogin) ActionType.LOGIN else ActionType.LOGOUT,
                targetModule = "Authentication",
                description = "User ${if (isLogin) "logged in" else "logged out"}."
            )
        }
    }

    /**
     * Administrative logic approvals (e.g. B2B, Marketplace integrations, etc.)
     */
    fun logAdminApproval(adminId: String, adminRole: String, targetId: String, module: String, isApproved: Boolean) {
        if (!AuditFeatureFlags.IS_AUDIT_LOGGING_ENABLED) return
        
        safeAuditLog {
            AuditEvent(
                userId = adminId,
                userRole = adminRole,
                actionType = if (isApproved) ActionType.APPROVAL else ActionType.REJECTION,
                targetModule = module,
                targetId = targetId,
                description = "Admin ${if (isApproved) "approved" else "rejected"} the entity."
            )
        }
    }

    /**
     * Content Management (Create / Update / Delete)
     * General usage for Businesses, Products, Educations, Appointments etc.
     */
    fun logContentAction(userId: String, userRole: String, targetId: String, module: String, action: ActionType) {
        if (!AuditFeatureFlags.IS_AUDIT_LOGGING_ENABLED) return
        
        // Ensure only CUD actions
        if (action != ActionType.CREATE && action != ActionType.UPDATE && action != ActionType.DELETE) return

        safeAuditLog {
            AuditEvent(
                userId = userId,
                userRole = userRole,
                actionType = action,
                targetModule = module,
                targetId = targetId,
                description = "User performed $action on $module entity."
            )
        }
    }

    /**
     * Training Completion metric.
     */
    fun logTrainingCompletion(userId: String, userRole: String, educationId: String, educationTitle: String) {
        if (!AuditFeatureFlags.IS_AUDIT_LOGGING_ENABLED) return
        
        safeAuditLog {
            AuditEvent(
                userId = userId,
                userRole = userRole,
                actionType = ActionType.UPDATE, // Effectively updating progress
                targetModule = "Education",
                targetId = educationId,
                description = "User successfully completed training: $educationTitle."
            )
        }
    }

    /**
     * Certificate Generation logs.
     */
    fun logCertificateGeneration(userId: String, userRole: String, certificateId: String) {
        if (!AuditFeatureFlags.IS_AUDIT_LOGGING_ENABLED) return
        
        safeAuditLog {
            AuditEvent(
                userId = userId,
                userRole = userRole,
                actionType = ActionType.CERTIFICATE_GENERATED,
                targetModule = "Education",
                targetId = certificateId,
                description = "System successfully generated and assigned PDF certificate."
            )
        }
    }

    /**
     * Role and Permission management logs.
     */
    fun logRoleAssignment(adminId: String, adminRole: String, targetUserId: String, oldRole: Role, newRole: Role) {
        if (!AuditFeatureFlags.IS_AUDIT_LOGGING_ENABLED) return

        safeAuditLog {
            AuditEvent(
                userId = adminId,
                userRole = adminRole,
                actionType = ActionType.UPDATE,
                targetModule = "UserManagement",
                targetId = targetUserId,
                description = "Admin changed role of user $targetUserId from $oldRole to $newRole."
            )
        }
    }
    /**
     * Certificate Revocation logs.
     */
    fun logCertificateRevocation(adminId: String, adminRole: String, certificateId: String, reason: String) {
        if (!AuditFeatureFlags.IS_AUDIT_LOGGING_ENABLED) return
        
        safeAuditLog {
            AuditEvent(
                userId = adminId,
                userRole = adminRole,
                actionType = ActionType.CERTIFICATE_REVOKED,
                targetModule = "Education",
                targetId = certificateId,
                description = "Admin revoked certificate $certificateId. Reason: $reason"
            )
        }
    }
    /**
     * Appointment Management logs.
     */
    fun logAppointmentAction(userId: String, userRole: String, appointmentId: String, action: ActionType, description: String) {
        if (!AuditFeatureFlags.IS_AUDIT_LOGGING_ENABLED) return

        safeAuditLog {
            AuditEvent(
                userId = userId,
                userRole = userRole,
                actionType = action,
                targetModule = "Appointment",
                targetId = appointmentId,
                description = description
            )
        }
    }
}

