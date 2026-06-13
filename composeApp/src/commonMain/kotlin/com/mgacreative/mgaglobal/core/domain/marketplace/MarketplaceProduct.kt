package com.mgacreative.mgaglobal.core.domain.marketplace

import kotlinx.datetime.Instant

/**
 * Domain model representing a product from any marketplace provider.
 * This model normalizes data structures across different platforms (Amazon, Alibaba, etc.)
 * to ensure the UI remains provider-agnostic.
 * 
 * @property productId Unique identifier for the product within the provider's system.
 * @property providerId Reference to the [MarketplaceProvider] this product belongs to.
 * @property title The display title or name of the product.
 * @property description Detailed product information (optional).
 * @property price Numeric value of the product price.
 * @property currency Three-letter ISO currency code (e.g., "USD", "EUR", "TRY").
 * @property imageUrl Direct link to the primary product image.
 * @property productUrl Direct link to the product page on the provider's marketplace.
 * @property lastUpdated Timestamp of the last synchronization or data update.
 */
data class MarketplaceProduct(
    val productId: String = "",
    val providerId: String = "",
    val title: String = "",
    val description: String? = null,
    val price: Double = 0.0,
    val currency: String = "USD",
    val imageUrl: String = "",
    val productUrl: String = "",
    val lastUpdated: Long = 0L
) {
    /**
     * Helper to get a formatted price string for the UI.
     */
    val formattedPrice: String get() = "$price $currency"
}

