package com.mgacreative.mgaglobal.core.domain.b2b

import kotlin.math.min

/**
 * Deterministic engine for calculating B2B compatibility scores between companies.
 * This engine uses pre-defined weights and logic without AI or randomness.
 */
object B2BScoringEngine {

    /**
     * Calculates the matching score between a source company and a target company.
     * 
     * @param source The company seeking a match.
     * @param target The potential partner company.
     * @return A [B2BScoreResult] containing the breakdown.
     */
    fun calculateScore(source: B2BCompany, target: B2BCompany): B2BScoreResult {
        val builder = B2BScoreExplanationBuilder()

        // 1. Sector Match Score (35% weight)
        val sectorScore = calculateSectorScore(source, target, builder)
        
        // 2. Profile Completeness Score (20% weight)
        val profileScore = calculateProfileScore(target, builder)
        
        // 3. Activity & Trust Score (25% weight)
        val activityScore = calculateTrustScore(target, builder)
        
        // 4. Geography / Export Score (20% weight)
        val geographyScore = calculateGeographyScore(source, target, builder)

        // Weighted Average
        val totalScore = (sectorScore * 0.35) + 
                         (profileScore * 0.20) + 
                         (activityScore * 0.25) + 
                         (geographyScore * 0.20)

        return B2BScoreResult(
            companyId = target.id,
            totalScore = totalScore * 100,
            sectorScore = sectorScore * 100,
            profileScore = profileScore * 100,
            activityScore = activityScore * 100,
            geographyScore = geographyScore * 100,
            explanation = builder.build()
        )
    }

    private fun calculateSectorScore(source: B2BCompany, target: B2BCompany, builder: B2BScoreExplanationBuilder): Double {
        return when {
            source.sector == target.sector -> {
                val commonSubs = source.subSectors.intersect(target.subSectors.toSet())
                if (commonSubs.isNotEmpty()) {
                    builder.addFactor(B2BScoreExplanationBuilder.SECTOR_MATCH_FULL, 35.0)
                    1.0
                } else {
                    builder.addFactor(B2BScoreExplanationBuilder.SECTOR_MATCH_FULL, 30.0)
                    0.85
                }
            }
            source.subSectors.any { it in target.subSectors } -> {
                builder.addFactor(B2BScoreExplanationBuilder.SECTOR_MATCH_PARTIAL, 15.0)
                0.60
            }
            else -> {
                builder.addFactor(B2BScoreExplanationBuilder.SECTOR_MATCH_NONE, 5.0)
                0.20
            }
        }
    }

    private fun calculateProfileScore(company: B2BCompany, builder: B2BScoreExplanationBuilder): Double {
        var score = 0.0
        if (company.hasLogo) {
            score += 0.2
            builder.addFactor(B2BScoreExplanationBuilder.PROFILE_LOGO, 4.0)
        }
        if (company.hasDescription) {
            score += 0.3
            builder.addFactor(B2BScoreExplanationBuilder.PROFILE_DESCRIPTION, 6.0)
        }
        if (company.hasContactInfo) {
            score += 0.2
            builder.addFactor(B2BScoreExplanationBuilder.PROFILE_CONTACT, 4.0)
        }
        
        val certCount = company.certifications.size
        if (certCount > 0) {
            val certPoints = min(0.3, certCount * 0.1)
            score += certPoints
            builder.addFactor(B2BScoreExplanationBuilder.PROFILE_CERTIFICATES, certPoints * 20.0)
        }
        
        return score
    }

    private fun calculateTrustScore(company: B2BCompany, builder: B2BScoreExplanationBuilder): Double {
        var score = 0.0
        
        if (company.isVerified) {
            score += 0.5
            builder.addFactor(B2BScoreExplanationBuilder.TRUST_VERIFIED, 12.5)
        }

        if (company.yearsInMarket >= 5) {
            score += 0.3
            builder.addFactor(B2BScoreExplanationBuilder.TRUST_ESTABLISHED, 7.5)
        } else if (company.yearsInMarket > 0) {
            score += (company.yearsInMarket * 0.05)
        }

        if (company.platformActivityScore > 0.5) {
            score += (company.platformActivityScore * 0.2)
            builder.addFactor(B2BScoreExplanationBuilder.TRUST_ACTIVITY, company.platformActivityScore * 5.0)
        }
        
        return min(1.0, score)
    }

    private fun calculateGeographyScore(source: B2BCompany, target: B2BCompany, builder: B2BScoreExplanationBuilder): Double {
        var score = 0.0
        
        if (target.targetMarkets.contains(source.country)) {
            score += 0.6
            builder.addFactor(B2BScoreExplanationBuilder.GEO_MARKET_MATCH, 12.0)
        }

        if (target.exportVolume > 1_000_000) {
            score += 0.4
            builder.addFactor(B2BScoreExplanationBuilder.GEO_VOLUME_HIGH, 8.0)
        } else if (target.exportVolume > 100_000) {
            score += 0.2
            builder.addFactor(B2BScoreExplanationBuilder.GEO_VOLUME_MEDIUM, 4.0)
        }

        if (score == 0.0) {
            builder.addFactor(B2BScoreExplanationBuilder.GEO_NO_ALIGNMENT, 2.0)
            score = 0.1
        }

        return min(1.0, score)
    }
}

