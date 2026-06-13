package com.mgacreative.mgaglobal.core.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Common formatting utilities for timestamps and strings across the app.
 */
object CommonFormatters {
    
    /**
     * Formats milliseconds into a human-readable date and time string.
     * Output format: YYYY-MM-DD HH:MM
     */
    fun formatTimestamp(millis: Long): String {
        if (millis <= 0L) return "-"
        return try {
            val ext = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.currentSystemDefault())
            val dateStr = ext.date.toString()
            val hourStr = ext.hour.toString().padStart(2, '0')
            val minuteStr = ext.minute.toString().padStart(2, '0')
            "$dateStr $hourStr:$minuteStr"
        } catch (e: Exception) {
            "-"
        }
    }
}

