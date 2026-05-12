package com.mgacreative.globaltrade.core.domain.announcement

import kotlinx.serialization.Serializable
import com.mgacreative.globaltrade.getNowMillis

@Serializable
data class Announcement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    // Multi-language support
    val titleEn: String = "",
    val descriptionEn: String = "",
    val titleAr: String = "",
    val descriptionAr: String = "",
    val titleZh: String = "",
    val descriptionZh: String = "",
    val titleDe: String = "",
    val descriptionDe: String = "",
    val titleRu: String = "",
    val descriptionRu: String = "",
    val colorHex: String = "#4361EE",
    val link: String = "",
    val active: Int = 1,
    val createdAt: Long = getNowMillis(),
    val expiresAt: Long? = null
) {
    val isActive: Boolean get() = active == 1
}
