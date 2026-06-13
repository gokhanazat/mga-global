package com.mgacreative.mgaglobal.core.domain.b2b

/**
 * Configuration model for the B2B scoring algorithm weights.
 * Managed by administrators to fine-tune the matching engine remotely.
 * 
 * @property sectorWeight Weight for industry/sector alignment (0.0 to 1.0).
 * @property profileWeight Weight for profile completeness (0.0 to 1.0).
 * @property activityWeight Weight for platform activity and trust (0.0 to 1.0).
 * @property geographyWeight Weight for regional and export alignment (0.0 to 1.0).
 * @property version Versioning for tracking configuration changes over time.
 * @property lastUpdated Timestamp of when this configuration was last modified.
 */
data class AdminScoringConfig(
    val sectorWeight: Double = 0.35,
    val profileWeight: Double = 0.20,
    val activityWeight: Double = 0.25,
    val geographyWeight: Double = 0.20,
    val version: Int = 1,
    val lastUpdated: Long = 0L
) {
    /**
     * Ensures the configuration is mathematically sound.
     * Weights should ideally sum up to 1.0 for a normalized percentage result.
     */
    val isNormalized: Boolean 
        get() = (sectorWeight + profileWeight + activityWeight + geographyWeight) in 0.999..1.001

    /**
     * Fallback mechanism to ensure we always have a valid config if Firebase returns zeroes.
     */
    fun getSafeConfig(): AdminScoringConfig {
        return if (sectorWeight == 0.0 && profileWeight == 0.0) {
            AdminScoringConfig() // Return defaults
        } else {
            this
        }
    }
}

