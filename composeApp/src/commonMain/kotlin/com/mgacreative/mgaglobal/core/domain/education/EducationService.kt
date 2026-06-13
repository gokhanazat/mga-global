package com.mgacreative.mgaglobal.core.domain.education

import com.mgacreative.mgaglobal.core.network.SupabaseConfig
import com.mgacreative.mgaglobal.getNowMillis
import com.mgacreative.mgaglobal.ui.education.Education
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class UserCertificate(
    val id: Int = 0,
    val email: String,
    @SerialName("cert_code")
    val certCode: String,
    @SerialName("edu_id")
    val eduId: String,
    @SerialName("created_at")
    val createdAt: Long
)

class EducationService {

    suspend fun saveEducation(education: Education): Result<Unit> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val docId = education.id.ifEmpty { "edu_${getNowMillis()}" }
            val newEducation = education.copy(
                id = docId,
                createdAt = if (education.createdAt == 0L) getNowMillis() else education.createdAt
            )
            
            client.postgrest.from("educations").upsert(newEducation)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllEducations(): Result<List<Education>> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val list = client.postgrest.from("educations")
                .select()
                .decodeList<Education>()
            Result.success(list.sortedByDescending { it.createdAt })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEducationById(id: String): Result<Education> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val edu = client.postgrest.from("educations")
                .select {
                    filter {
                        eq("id", id)
                    }
                }.decodeSingle<Education>()
            Result.success(edu)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserCertificates(email: String): Result<List<UserCertificate>> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val list = client.postgrest.from("certificates")
                .select {
                    filter {
                        eq("email", email)
                    }
                }.decodeList<UserCertificate>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteEducation(id: String): Result<Unit> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            client.postgrest.from("educations").delete {
                filter {
                    eq("id", id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
