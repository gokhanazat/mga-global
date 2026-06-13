package com.mgacreative.mgaglobal.core.audit

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Defines all standard actions an entity can perform within the system.
 * 
 * Used for tracking user activities in AuditLog to reconstruct user lifecycles and behaviors.
 */
@Serializable
enum class ActionType {
    LOGIN,
    LOGOUT,
    CREATE,
    UPDATE,
    DELETE,
    APPROVAL,
    REJECTION,
    VIEW,
    DOWNLOAD,
    CERTIFICATE_GENERATED,
    CERTIFICATE_REVOKED,
    CANCELLED,
    APPOINTMENT_NOTIFICATION,
    B2B_MATCH_CALCULATED,
    B2B_MATCH_APPROVED,
    MARKETPLACE_PROVIDER_STATUS_CHANGED,
    MARKETPLACE_PRODUCTS_FETCHED
}

/**
 * Application-wide data model representing an atomic audit log entry.
 * Designed to be safely serializable for Firestore Database/Supabase integration.
 */
@Serializable
data class AuditEvent(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("user_role")
    val userRole: String = "",
    @SerialName("action_type")
    val actionType: ActionType = ActionType.VIEW,
    @SerialName("target_module")
    val targetModule: String = "",
    @SerialName("target_id")
    val targetId: String? = null,
    val description: String = "",
    val timestamp: Long = 0L,
    @SerialName("device_info")
    val deviceInfo: String? = null,
    @SerialName("app_version")
    val appVersion: String = ""
)
