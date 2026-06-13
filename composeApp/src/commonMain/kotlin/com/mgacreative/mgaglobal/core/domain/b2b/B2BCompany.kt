package com.mgacreative.mgaglobal.core.domain.b2b

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import com.mgacreative.mgaglobal.core.util.IntToBooleanSerializer

/**
 * Domain model representing a company specifically for B2B matching and scoring.
 */
@Serializable
data class B2BCompany(
    val id: String = "",
    val name: String = "",
    val sector: String = "",
    @SerialName("sub_sectors")
    val subSectors: List<String> = emptyList(),
    val country: String = "",
    @SerialName("is_verified")
    @Serializable(with = IntToBooleanSerializer::class)
    val isVerified: Boolean = false,
    @SerialName("years_in_market")
    val yearsInMarket: Int = 0,
    @SerialName("export_volume")
    val exportVolume: Double = 0.0,
    @SerialName("target_markets")
    val targetMarkets: List<String> = emptyList(),
    val certifications: List<String> = emptyList(),
    @SerialName("has_logo")
    @Serializable(with = IntToBooleanSerializer::class)
    val hasLogo: Boolean = false,
    @SerialName("has_description")
    @Serializable(with = IntToBooleanSerializer::class)
    val hasDescription: Boolean = false,
    @SerialName("has_contact_info")
    @Serializable(with = IntToBooleanSerializer::class)
    val hasContactInfo: Boolean = false,
    @SerialName("platform_activity_score")
    val platformActivityScore: Double = 0.0,
    @SerialName("logo_url")
    val logoUrl: String? = null,
    val phone: String = "",
    val gsm: String = "",
    val email: String = "",
    @SerialName("authorized_person")
    val authorizedPerson: String = "",
    val description: String = ""
)
