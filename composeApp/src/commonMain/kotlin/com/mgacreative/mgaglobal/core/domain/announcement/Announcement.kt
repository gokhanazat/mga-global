package com.mgacreative.mgaglobal.core.domain.announcement

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import com.mgacreative.mgaglobal.getNowMillis

@Serializable
data class Announcement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    @SerialName("title_en")
    val titleEn: String = "",
    @SerialName("description_en")
    val descriptionEn: String = "",
    @SerialName("title_ar")
    val titleAr: String = "",
    @SerialName("description_ar")
    val descriptionAr: String = "",
    @SerialName("title_zh")
    val titleZh: String = "",
    @SerialName("description_zh")
    val descriptionZh: String = "",
    @SerialName("title_de")
    val titleDe: String = "",
    @SerialName("description_de")
    val descriptionDe: String = "",
    @SerialName("title_ru")
    val titleRu: String = "",
    @SerialName("description_ru")
    val descriptionRu: String = "",
    @SerialName("color_hex")
    val colorHex: String = "#4361EE",
    val link: String = "",
    val active: Int = 1,
    @SerialName("created_at")
    val createdAt: Long = getNowMillis(),
    @SerialName("expires_at")
    val expiresAt: Long? = null
) {
    val isActive: Boolean get() = active == 1
}
