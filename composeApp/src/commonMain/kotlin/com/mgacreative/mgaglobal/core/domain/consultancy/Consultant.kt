package com.mgacreative.mgaglobal.core.domain.consultancy

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Consultant(
    val id: String = "",
    val name: String = "",
    val title: String = "",
    val expertise: String = "",
    val bio: String = "",
    @SerialName("photo_url")
    val photoUrl: String? = null,
    val email: String = "",
    val phone: String = "",
    val whatsapp: String = "",
    @SerialName("display_order")
    val displayOrder: Int = 0
)
