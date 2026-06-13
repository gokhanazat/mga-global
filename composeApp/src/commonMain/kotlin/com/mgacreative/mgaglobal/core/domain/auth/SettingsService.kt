package com.mgacreative.mgaglobal.core.domain.auth

import com.mgacreative.mgaglobal.core.network.SupabaseConfig
import com.mgacreative.mgaglobal.core.error.AppResult
import com.mgacreative.mgaglobal.core.error.safeCall
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class AppConfig(
    @SerialName("contact_email")
    val contactEmail: String = "destek@mgaglobal.com",
    val faqs: List<FAQItem> = emptyList()
)

class SettingsService {

    suspend fun getContactEmail(): AppResult<String> {
        return safeCall {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val config = client.postgrest.from("settings")
                .select {
                    filter {
                        eq("id", 1)
                    }
                }.decodeSingleOrNull<AppConfig>()
            config?.contactEmail ?: "destek@mgaglobal.com"
        }
    }

    suspend fun updateContactEmail(email: String): AppResult<Unit> {
        return safeCall {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            client.postgrest.from("settings").update(
                mapOf("contact_email" to email)
            ) {
                filter {
                    eq("id", 1)
                }
            }
        }
    }

    suspend fun getHelpCenterFAQs(): AppResult<List<FAQItem>> {
        return safeCall {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val config = client.postgrest.from("settings")
                .select {
                    filter {
                        eq("id", 1)
                    }
                }.decodeSingleOrNull<AppConfig>()
            config?.faqs ?: emptyList()
        }
    }

    suspend fun updateHelpCenterFAQs(faqs: List<FAQItem>): AppResult<Unit> {
        return safeCall {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            client.postgrest.from("settings").update(
                mapOf("faqs" to faqs)
            ) {
                filter {
                    eq("id", 1)
                }
            }
        }
    }
}
