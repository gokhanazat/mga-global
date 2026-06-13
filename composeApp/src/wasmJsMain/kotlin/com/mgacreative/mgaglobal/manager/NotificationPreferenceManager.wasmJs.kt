package com.mgacreative.mgaglobal.manager

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

actual object NotificationPreferenceManager {
    actual fun isAppointmentEnabled(): Flow<Boolean> = flowOf(true)
    actual fun isRemindersEnabled(): Flow<Boolean> = flowOf(true)
    actual suspend fun setAppointmentEnabled(enabled: Boolean) {}
    actual suspend fun setRemindersEnabled(enabled: Boolean) {}
}

