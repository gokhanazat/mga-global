package com.mgacreative.mgaglobal.core.domain.b2b

/**
 * Service responsible for ranking companies based on their B2B compatibility scores.
 * Optimized for performance and deterministic results.
 */
object B2BMatchRankingService {

    /**
     * Ranks potential partners for a source company and returns the top matches.
     * 
     * @param source The company for which we are finding matches.
     * @param targets A list of potential partner companies to evaluate.
     * @param limit The maximum number of matches to return.
     * @return A list of [B2BScoreResult] objects, sorted by totalScore in descending order.
     */
    fun getTopMatches(
        source: B2BCompany,
        targets: List<B2BCompany>,
        limit: Int = 20
    ): List<B2BScoreResult> {
        if (targets.isEmpty()) return emptyList()

        return targets
            .asSequence() // Use sequence for better performance with large lists (avoids intermediate list copies)
            .map { target -> 
                B2BScoringEngine.calculateScore(source, target) 
            }
            .filter { it.totalScore > 0 } // Performance-safe: discard non-matches early
            .sortedByDescending { it.totalScore }
            .take(limit)
            .toList()
    }
}

