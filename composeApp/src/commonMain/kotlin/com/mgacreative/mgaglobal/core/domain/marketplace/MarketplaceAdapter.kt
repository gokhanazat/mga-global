package com.mgacreative.mgaglobal.core.domain.marketplace

/**
 * Strategy interface for interacting with different marketplace platforms.
 * Each implementation handles platform-specific API structures and authentication,
 * returning normalized [MarketplaceProduct] domain models.
 */
interface MarketplaceAdapter {
    /**
     * Fetches a list of products from the marketplace.
     */
    suspend fun fetchProducts(): List<MarketplaceProduct>

    /**
     * Fetches detailed information for a specific product.
     */
    suspend fun fetchProductDetail(productId: String): MarketplaceProduct?
}

