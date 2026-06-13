package com.mgacreative.mgaglobal.core.audit

import com.mgacreative.mgaglobal.core.network.SupabaseConfig
import com.mgacreative.mgaglobal.getNowMillis
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

object AuditLogger {
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val logQueue = Channel<AuditEvent>(Channel.UNLIMITED)

    init {
        startQueueProcessor()
    }

    fun logEvent(event: AuditEvent) {
        logQueue.trySend(event)
    }

    suspend fun getLogs(): Result<List<AuditEvent>> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val list = client.postgrest.from("audit_logs")
                .select()
                .decodeList<AuditEvent>()
            Result.success(list.sortedByDescending { it.timestamp })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun startQueueProcessor() {
        scope.launch {
            for (event in logQueue) {
                try {
                    val client = SupabaseConfig.client ?: continue
                    val docId = event.id.ifEmpty { "audit_${getNowMillis()}" }
                    val finalEvent = event.copy(id = docId)
                    client.postgrest.from("audit_logs").insert(finalEvent)
                } catch (e: Exception) {
                    println("AuditLogger: Silently dropped event ${event.actionType} due to ${e.message}")
                }
            }
        }
    }
}
