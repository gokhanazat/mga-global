package com.mgacreative.mgaglobal.core.domain.audit

/**
 * Feature flags controlling domain-level logic.
 * Enables keeping heavy operations like Audit Logging optional or dynamically disabled remotely.
 */
object AuditFeatureFlags {
    var IS_AUDIT_LOGGING_ENABLED: Boolean = true
}

