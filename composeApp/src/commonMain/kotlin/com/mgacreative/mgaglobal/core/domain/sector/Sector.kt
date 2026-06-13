package com.mgacreative.mgaglobal.core.domain.sector

import com.mgacreative.mgaglobal.core.util.IntToBooleanSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Sector(
    val id: String = "",
    val name: String = "",
    @SerialName("group_no")
    val groupNo: String = "",
    @SerialName("is_active")
    @Serializable(with = IntToBooleanSerializer::class)
    val isActive: Boolean = true
)
