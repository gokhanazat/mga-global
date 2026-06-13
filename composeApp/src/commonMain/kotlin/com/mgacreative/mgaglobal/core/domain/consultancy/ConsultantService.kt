package com.mgacreative.mgaglobal.core.domain.consultancy

import com.mgacreative.mgaglobal.core.network.SupabaseConfig
import com.mgacreative.mgaglobal.core.error.AppResult
import com.mgacreative.mgaglobal.core.error.AppError
import io.github.jan.supabase.postgrest.postgrest

class ConsultantService {

    suspend fun getConsultants(): AppResult<List<Consultant>> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val list = client.postgrest.from("consultants")
                .select()
                .decodeList<Consultant>()
            AppResult.Success(list.sortedBy { it.displayOrder })
        } catch (e: Exception) {
            AppResult.Error(AppError.Unknown(e))
        }
    }

    suspend fun addConsultant(consultant: Consultant): AppResult<Unit> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            client.postgrest.from("consultants").upsert(consultant)
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(AppError.Unknown(e))
        }
    }

    suspend fun updateConsultant(consultant: Consultant): AppResult<Unit> {
        return addConsultant(consultant)
    }

    suspend fun deleteConsultant(id: String): AppResult<Unit> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            client.postgrest.from("consultants").delete {
                filter {
                    eq("id", id)
                }
            }
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(AppError.Unknown(e))
        }
    }
}
