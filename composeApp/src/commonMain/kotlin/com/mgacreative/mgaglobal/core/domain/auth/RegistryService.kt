package com.mgacreative.mgaglobal.core.domain.auth

import com.mgacreative.mgaglobal.getNowMillis
import com.mgacreative.mgaglobal.core.network.SupabaseConfig
import com.mgacreative.mgaglobal.core.error.AppResult
import com.mgacreative.mgaglobal.core.error.safeCall
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class RegistryEntry(
    val id: String = "",
    val number: String = "",
    val ownerName: String = "",
    val active: Boolean = true,
    val createdAt: Long = 0L
)

class RegistryService {

    suspend fun isValidRegistryNumber(number: String): AppResult<Boolean> {
        return safeCall {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val results = client.postgrest.from("registry")
                .select {
                    filter {
                        eq("number", number)
                        eq("active", true)
                    }
                }.decodeList<RegistryEntry>()
            results.isNotEmpty()
        }
    }

    suspend fun addRegistryNumber(number: String, ownerName: String): AppResult<Unit> {
        return safeCall {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val entry = RegistryEntry(
                id = "reg_${getNowMillis()}",
                number = number.trim(),
                ownerName = ownerName.trim(),
                active = true,
                createdAt = getNowMillis()
            )
            client.postgrest.from("registry").insert(entry)
        }
    }

    suspend fun setRegistryStatus(number: String, active: Boolean): AppResult<Unit> {
        return safeCall {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            client.postgrest.from("registry").update(
                mapOf("active" to active)
            ) {
                filter {
                    eq("number", number)
                }
            }
        }
    }

    suspend fun deleteRegistryEntry(id: String): AppResult<Unit> {
        return safeCall {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            client.postgrest.from("registry").delete {
                filter {
                    eq("id", id)
                }
            }
        }
    }

    suspend fun getAllRegistryEntries(): AppResult<List<RegistryEntry>> {
        return safeCall {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            client.postgrest.from("registry")
                .select()
                .decodeList<RegistryEntry>()
        }
    }
}
