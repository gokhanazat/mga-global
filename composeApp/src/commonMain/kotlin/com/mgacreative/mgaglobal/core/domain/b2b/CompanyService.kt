package com.mgacreative.mgaglobal.core.domain.b2b

import com.mgacreative.mgaglobal.core.network.SupabaseConfig
import com.mgacreative.mgaglobal.core.auth.SessionManager
import com.mgacreative.mgaglobal.getNowMillis
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.Serializable

class CompanyService {

    suspend fun saveCompany(company: B2BCompany): Result<Unit> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val userId = SessionManager.getUserId()
            if (userId == "guest") return Result.failure(Exception("Oturum acilmamis"))

            var finalLogoUrl = company.logoUrl

            if (company.logoUrl != null && company.logoUrl!!.startsWith("data:image/")) {
                val base64Header = company.logoUrl!!.substringBefore(",", "")
                val extension = if (base64Header.contains("image/png")) "png" 
                               else if (base64Header.contains("image/jpeg")) "jpg"
                               else "webp"

                val base64Data = company.logoUrl!!.substringAfter("base64,")
                @OptIn(ExperimentalEncodingApi::class)
                val imageBytes = Base64.Default.decode(base64Data)

                val filename = "logo_${userId}_${getNowMillis()}.$extension"
                val bucket = client.storage.from("products")
                bucket.upload(filename, imageBytes)
                
                finalLogoUrl = bucket.publicUrl(filename)
            }

            // Agresif temizlik: Sadece ASCII karakterleri tut
            val cleanedLogoUrl = finalLogoUrl?.filter { it.code in 33..126 && it != '"' && it != '\'' }

            val finalCompany = company.copy(
                id = userId,
                logoUrl = cleanedLogoUrl,
                hasLogo = !cleanedLogoUrl.isNullOrBlank(),
                hasDescription = company.description.isNotBlank(),
                hasContactInfo = company.email.isNotBlank() || company.phone.isNotBlank() || company.gsm.isNotBlank()
            )

            client.postgrest.from("companies").upsert(finalCompany)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOwnCompany(): Result<B2BCompany?> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val userId = SessionManager.getUserId()
            if (userId == "guest") return Result.failure(Exception("Oturum acilmamis"))

            val company = client.postgrest.from("companies")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }.decodeSingleOrNull<B2BCompany>()
            
            if (company != null && company.id.isEmpty() && company.name.isEmpty()) Result.success(null)
            else Result.success(company)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCompanyById(id: String): Result<B2BCompany?> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val company = client.postgrest.from("companies")
                .select {
                    filter {
                        eq("id", id)
                    }
                }.decodeSingleOrNull<B2BCompany>()
            Result.success(company)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllCompanies(): Result<List<B2BCompany>> {
        return try {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val list = client.postgrest.from("companies")
                .select()
                .decodeList<B2BCompany>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
