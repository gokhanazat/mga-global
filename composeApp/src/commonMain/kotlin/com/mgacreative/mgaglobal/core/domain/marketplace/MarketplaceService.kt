package com.mgacreative.mgaglobal.core.domain.marketplace

import com.mgacreative.mgaglobal.getNowMillis

import com.mgacreative.mgaglobal.core.audit.ActionType
import com.mgacreative.mgaglobal.core.audit.AuditEvent
import com.mgacreative.mgaglobal.core.audit.AuditLogger
import com.mgacreative.mgaglobal.core.error.AppResult
import com.mgacreative.mgaglobal.core.error.safeCall
import com.mgacreative.mgaglobal.core.util.SecureConfigProvider
import com.mgacreative.mgaglobal.core.util.ConfigKeys

/**
 * Domain-level service that orchestrates marketplace operations.
 * It manages adapter selection, error handling, and basic in-memory caching.
 */
class MarketplaceService(
    private val configProvider: SecureConfigProvider
) {
    private val adapters: Map<String, MarketplaceAdapter> = mapOf(
        "TRENDYOL" to TrendyolAdapter(),
        "AMAZON" to AmazonAdapter()
    )
    private val fallbackAdapter: MarketplaceAdapter = MockAdapter()

    // Simple in-memory cache: providerId -> List of Products
    private val cache = mutableMapOf<String, List<MarketplaceProduct>>()

    /**
     * Safely fetches products for a specific provider.
     */
    suspend fun getProducts(providerId: String, forceRefresh: Boolean = false): AppResult<List<MarketplaceProduct>> {
        return safeCall {
            // Security Check
            val apiKey = configProvider.getMarketplaceKey(providerId)
            
            if (configProvider.isProduction() && apiKey.isNullOrBlank() && providerId.uppercase() != "MOCK") {
                throw IllegalStateException("Secure connection failed: API Key missing for $providerId")
            }

            if (!forceRefresh) {
                val cached = cache[providerId]
                if (cached != null) return@safeCall cached
            }

            val adapter = adapters[providerId.uppercase()] ?: fallbackAdapter
            val products = adapter.fetchProducts()

            // Log the fetch event
            AuditLogger.logEvent(
                AuditEvent(
                    actionType = ActionType.MARKETPLACE_PRODUCTS_FETCHED,
                    targetModule = "Marketplace",
                    targetId = providerId,
                    description = "Fetched ${products.size} products from $providerId",
                    timestamp = getNowMillis()
                )
            )

            cache[providerId] = products
            products
        }
    }

    /**
     * Safely fetches product details.
     */
    suspend fun getProductDetail(providerId: String, productId: String): AppResult<MarketplaceProduct?> {
        return safeCall {
            val adapter = adapters[providerId.uppercase()] ?: fallbackAdapter
            adapter.fetchProductDetail(productId)
        }
    }

    /**
     * Logs the activation or deactivation of a marketplace provider.
     */
    fun logProviderStatusChange(providerId: String, isActive: Boolean, adminId: String) {
        AuditLogger.logEvent(
            AuditEvent(
                userId = adminId,
                actionType = ActionType.MARKETPLACE_PROVIDER_STATUS_CHANGED,
                targetModule = "Marketplace",
                targetId = providerId,
                description = "Provider $providerId set to active=$isActive",
                timestamp = getNowMillis()
            )
        )
    }

    /**
     * Clears the current cache.
     */
    fun clearCache() {
        cache.clear()
    }
}

