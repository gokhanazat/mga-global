package com.mgacreative.mgaglobal.core.domain.b2b

/**
 * A builder for constructing language-neutral explanations for B2B scores.
 * Instead of human-readable strings, it stores keys and associated values
 * to be localized at the UI layer.
 */
class B2BScoreExplanationBuilder {
    private val factors = mutableListOf<String>()

    /**
     * Adds a scoring factor to the explanation list.
     * 
     * @param key The language-neutral key representing the factor.
     * @param points The specific point value added or subtracted.
     */
    fun addFactor(key: String, points: Double) {
        // Format: "key|points" - strictly deterministic and parseable
        factors.add("$key|${points.toInt()}")
    }

    /**
     * Build the final list of explanation entries.
     */
    fun build(): List<String> = factors.toList()

    companion object {
        // Key constants to ensure consistency across the engine and UI
        const val SECTOR_MATCH_FULL = "b2b_score_sector_match_full"
        const val SECTOR_MATCH_PARTIAL = "b2b_score_sector_match_partial"
        const val SECTOR_MATCH_NONE = "b2b_score_sector_match_none"
        
        const val PROFILE_LOGO = "b2b_score_profile_logo"
        const val PROFILE_DESCRIPTION = "b2b_score_profile_desc"
        const val PROFILE_CONTACT = "b2b_score_profile_contact"
        const val PROFILE_CERTIFICATES = "b2b_score_profile_certs"
        
        const val TRUST_VERIFIED = "b2b_score_trust_verified"
        const val TRUST_ESTABLISHED = "b2b_score_trust_established"
        const val TRUST_ACTIVITY = "b2b_score_trust_activity"
        
        const val GEO_MARKET_MATCH = "b2b_score_geo_market_match"
        const val GEO_VOLUME_HIGH = "b2b_score_geo_volume_high"
        const val GEO_VOLUME_MEDIUM = "b2b_score_geo_volume_med"
        const val GEO_NO_ALIGNMENT = "b2b_score_geo_none"
    }
}

