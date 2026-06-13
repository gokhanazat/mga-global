package com.mgacreative.mgaglobal.core.domain.sector

import com.mgacreative.mgaglobal.core.network.SupabaseConfig
import com.mgacreative.mgaglobal.core.error.AppResult
import com.mgacreative.mgaglobal.core.error.safeCall
import com.mgacreative.mgaglobal.getNowMillis
import io.github.jan.supabase.postgrest.postgrest

class SectorService {

    suspend fun getSectors(): AppResult<List<Sector>> {
        return safeCall {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val list = client.postgrest.from("sectors")
                .select()
                .decodeList<Sector>()
            list
        }
    }

    suspend fun saveSector(sector: Sector): AppResult<Unit> = safeCall {
        val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
        val finalId = if (sector.id.isBlank()) "sec_${getNowMillis()}" else sector.id
        
        val updatedSector = sector.copy(
            id = finalId,
            name = sector.name.trim(),
            groupNo = sector.groupNo.trim(),
            isActive = true
        )
        
        client.postgrest.from("sectors").upsert(updatedSector)
    }

    suspend fun deleteSector(id: String): AppResult<Unit> = safeCall {
        val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
        client.postgrest.from("sectors").delete {
            filter {
                eq("id", id)
            }
        }
    }
}
