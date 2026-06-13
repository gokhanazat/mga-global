package com.mgacreative.mgaglobal.core.domain.b2b

/**
 * Domain model representing the detailed B2B scoring result for a company.
 * Designed to be read-only (immutable) and compatible with Firebase Firestore.
 * 
 * @property companyId Unique identifier for the company being scored.
 * @property totalScore Overall calculated score, typically a weighted average.
 * @property sectorScore Score based on industry and sector alignment.
 * @property profileScore Score based on profile completeness and quality.
 * @property activityScore Score based on recent platform activity and engagement.
 * @property geographyScore Score based on location and trade route alignment.
 * @property explanation Detailed list of points explaining the score components.
 */
data class B2BScoreResult(
    val companyId: String = "",
    val totalScore: Double = 0.0,
    val sectorScore: Double = 0.0,
    val profileScore: Double = 0.0,
    val activityScore: Double = 0.0,
    val geographyScore: Double = 0.0,
    val explanation: List<String> = emptyList()
)

