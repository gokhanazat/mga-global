package com.mgacreative.mgaglobal.core.domain.announcement

import com.mgacreative.mgaglobal.core.network.SupabaseConfig
import com.mgacreative.mgaglobal.core.error.AppResult
import com.mgacreative.mgaglobal.core.error.safeCall
import com.mgacreative.mgaglobal.getNowMillis
import io.github.jan.supabase.postgrest.postgrest

class AnnouncementService {

    suspend fun getAllAnnouncements(): AppResult<List<Announcement>> {
        return safeCall {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val list = client.postgrest.from("announcements")
                .select()
                .decodeList<Announcement>()
            list.sortedByDescending { it.createdAt }
        }
    }

    suspend fun getActiveAnnouncements(): AppResult<List<Announcement>> {
        val allResult = getAllAnnouncements()
        return if (allResult.isSuccess) {
            AppResult.Success(allResult.getOrNull()?.filter { it.isActive } ?: emptyList())
        } else {
            allResult
        }
    }

    suspend fun saveAnnouncement(announcement: Announcement): AppResult<Unit> {
        return safeCall {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val finalAnnouncement = if (announcement.id.isBlank()) {
                announcement.copy(
                    id = "ann_${getNowMillis()}",
                    createdAt = getNowMillis()
                )
            } else {
                announcement
            }
            client.postgrest.from("announcements").upsert(finalAnnouncement)
        }
    }

    suspend fun deleteAnnouncement(id: String): AppResult<Unit> {
        return safeCall {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            client.postgrest.from("announcements").delete {
                filter {
                    eq("id", id)
                }
            }
        }
    }

    suspend fun toggleAnnouncementStatus(id: String, active: Boolean): AppResult<Unit> {
        return safeCall {
            val client = SupabaseConfig.client ?: throw Exception("Supabase is not initialized")
            val activeValue = if (active) 1 else 0
            client.postgrest.from("announcements").update(
                mapOf("active" to activeValue)
            ) {
                filter {
                    eq("id", id)
                }
            }
        }
    }
}
