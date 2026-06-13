package com.mgacreative.mgaglobal.core.domain.marketplace

/**
 * Adapter implementation for Amazon.
 * Note: Specified as Read-Only in current requirements.
 */
class AmazonAdapter : MarketplaceAdapter {
    override suspend fun fetchProducts(): List<MarketplaceProduct> {
        // Calling Amazon Selling Partner API (SP-API)
        return emptyList()
    }

    override suspend fun fetchProductDetail(productId: String): MarketplaceProduct? {
        return null
    }
}

