package com.mgacreative.mgaglobal.core.audit

import com.mgacreative.mgaglobal.getNowMillis


/**
 * System configuration parameters for Audit Logging.
 * Can be initialized once on app startup (e.g., MainActivity or App.kt).
 */
object AuditLogEnvironment {
    var defaultAppVersion: String = "1.0.0"
    var defaultDeviceInfo: String = "Multiplatform Client"
    
    // Internal thread-naive flag preventing StackOverflow from deep recursive logs
    @PublishedApi
    internal var isConstructingLog: Boolean = false
}

/**
 * A central, pure Kotlin DSL abstraction guaranteeing that any AuditEvent creation
 * is entirely exception-safe, non-recursive, and automatically enriched.
 *
 * Requirements fulfilled:
 * 1) safeAuditLog { } function structure.
 * 2) Catch all exceptions internally (fail-safe).
 * 3) Prevent recursive logging (isConstructingLog flag).
 * 4) Auto-attaches timestamp, appVersion, and deviceInfo.
 * 5) Promises absolute zero thrown exceptions to the caller.
 */
inline fun safeAuditLog(block: () -> AuditEvent) {
    // 3) Prevent recursive logging drops silent return
    if (AuditLogEnvironment.isConstructingLog) return

    try {
        AuditLogEnvironment.isConstructingLog = true
        
        // Safely invoke the provider lambda
        val rawEvent = block()
        
        // 4) Automatically attach standard missing parameters
        val enrichedEvent = rawEvent.copy(
            timestamp = if (rawEvent.timestamp == 0L) getNowMillis() else rawEvent.timestamp,
            appVersion = rawEvent.appVersion.ifBlank { AuditLogEnvironment.defaultAppVersion },
            deviceInfo = rawEvent.deviceInfo ?: AuditLogEnvironment.defaultDeviceInfo
        )
        
        // Safely push to our queue
        AuditLogger.logEvent(enrichedEvent)
        
    } catch (e: Exception) {
        // 2) Catch all exceptions internally
        // 5) No thrown exception allowed
        println("safeAuditLog: Internal failure - silently dropped to prevent crash. ${e.message}")
    } finally {
        AuditLogEnvironment.isConstructingLog = false
    }
}

