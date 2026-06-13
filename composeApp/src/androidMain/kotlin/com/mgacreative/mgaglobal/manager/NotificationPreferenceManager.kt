package com.mgacreative.mgaglobal.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.notificationDataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_prefs")

actual object NotificationPreferenceManager {
    private val KEY_APPOINTMENT_ENABLED = booleanPreferencesKey("appointment_notifications_enabled")
    private val KEY_REMINDERS_ENABLED = booleanPreferencesKey("reminder_notifications_enabled")

    private var context: Context? = null

    fun init(ctx: Context) {
        context = ctx.applicationContext
    }

    private fun getContextOrThrow(): Context = context ?: throw IllegalStateException("NotificationPreferenceManager not initialized. Call init(context) first.")

    actual fun isAppointmentEnabled(): Flow<Boolean> = getContextOrThrow().notificationDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[KEY_APPOINTMENT_ENABLED] ?: true }

    actual fun isRemindersEnabled(): Flow<Boolean> = getContextOrThrow().notificationDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[KEY_REMINDERS_ENABLED] ?: true }

    actual suspend fun setAppointmentEnabled(enabled: Boolean) {
        getContextOrThrow().notificationDataStore.edit { it[KEY_APPOINTMENT_ENABLED] = enabled }
    }

    actual suspend fun setRemindersEnabled(enabled: Boolean) {
        getContextOrThrow().notificationDataStore.edit { it[KEY_REMINDERS_ENABLED] = enabled }
    }

    /**
     * Synchronous check for non-UI logic (Background workers/Domain)
     */
    suspend fun isAppointmentEnabledSync(): Boolean = isAppointmentEnabled().first()
    suspend fun isRemindersEnabledSync(): Boolean = isRemindersEnabled().first()
}

