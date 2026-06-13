package com.mgacreative.mgaglobal.manager

import kotlinx.coroutines.flow.Flow

/**
 * Expected interface for local preference management.
 */
expect object NotificationPreferenceManager {
    fun isAppointmentEnabled(): Flow<Boolean>
    fun isRemindersEnabled(): Flow<Boolean>
    suspend fun setAppointmentEnabled(enabled: Boolean)
    suspend fun setRemindersEnabled(enabled: Boolean)
}

