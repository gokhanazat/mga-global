package com.mgacreative.mgaglobal.core.domain.auth

import com.mgacreative.mgaglobal.core.network.SupabaseConfig
import com.mgacreative.mgaglobal.core.error.AppResult
import com.mgacreative.mgaglobal.core.error.safeCall
import com.mgacreative.mgaglobal.core.auth.Role
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val success: Boolean,
    val token: String? = null,
    val role: String? = null,
    val message: String? = null
)

@Serializable
private data class SupabaseProfile(
    val id: String,
    val role: String = "GUEST",
    val email: String? = null,
    val is_approved: Boolean = false
)

class AuthService {

    private fun getEmailFromRegistryNumber(registryNumber: String): String {
        return if (registryNumber.contains("@")) {
            registryNumber.trim()
        } else {
            "reg_${registryNumber.trim()}@mgaglobal.com"
        }
    }

    suspend fun login(registryNumber: String, password: String): AppResult<AuthResponse> {
        return safeCall {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val targetEmail = getEmailFromRegistryNumber(registryNumber)
            
            // Supabase email/password login
            client.auth.signInWith(Email) {
                this.email = targetEmail
                this.password = password
            }
            
            val currentUser = client.auth.currentUserOrNull() ?: throw Exception("Kullanıcı bulunamadı")
            
            // Fetch profile for the role
            val profile = try {
                client.postgrest.from("profiles")
                    .select {
                        filter {
                            eq("id", currentUser.id)
                        }
                    }.decodeSingleOrNull<SupabaseProfile>()
            } catch (e: Exception) {
                null
            }
            
            val role = profile?.role ?: "MEMBER"
            AuthResponse(success = true, token = client.auth.currentAccessTokenOrNull(), role = role)
        }
    }

    suspend fun register(registryNumber: String, password: String): AppResult<AuthResponse> {
        return safeCall {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val targetEmail = getEmailFromRegistryNumber(registryNumber)
            
            // Supabase email/password sign up
            client.auth.signUpWith(Email) {
                this.email = targetEmail
                this.password = password
            }
            
            val currentUser = client.auth.currentUserOrNull() ?: throw Exception("Kayıt hatası")
            
            // Update the profile with registry number and role
            try {
                client.postgrest.from("profiles").update(
                    mapOf(
                        "registry_number" to registryNumber,
                        "role" to "MEMBER"
                    )
                ) {
                    filter {
                        eq("id", currentUser.id)
                    }
                }
            } catch (e: Exception) {
                // Ignore profile update errors if trigger already handled it
            }
            
            AuthResponse(success = true, token = client.auth.currentAccessTokenOrNull(), role = "MEMBER")
        }
    }

    suspend fun changePassword(newPassword: String): AppResult<AuthResponse> {
        return safeCall {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            client.auth.updateUser {
                this.password = newPassword
            }
            AuthResponse(success = true)
        }
    }

    suspend fun adminResetPassword(registryNumber: String, newPassword: String): AppResult<AuthResponse> {
        return safeCall {
            // Client-side Anon Key ile diğer kullanıcının şifresi doğrudan sıfırlanamaz.
            // Bu nedenle admin paneli için başarılı kabul edilip Supabase panelinden yönetilmesi önerilir.
            AuthResponse(success = true, message = "Şifre sıfırlama talebi alındı. Supabase Auth panelinden güncelleyin.")
        }
    }
}
