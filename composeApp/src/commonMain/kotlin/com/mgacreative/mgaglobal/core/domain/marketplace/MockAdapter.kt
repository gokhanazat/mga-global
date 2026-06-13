package com.mgacreative.mgaglobal.core.domain.marketplace

/**
 * Fallback Mock Adapter for testing and initial UI development.
 * Provides deterministic mock data without network calls.
 */
class MockAdapter : MarketplaceAdapter {
    override suspend fun fetchProducts(): List<MarketplaceProduct> {
        return listOf(
            MarketplaceProduct(
                productId = "MOCK_001",
                providerId = "MOCK_PROVIDER",
                title = "Industrial Power Drill",
                price = 149.99,
                currency = "USD",
                imageUrl = "https://example.com/drill.jpg"
            ),
            MarketplaceProduct(
                productId = "MOCK_002",
                providerId = "MOCK_PROVIDER",
                title = "Safety Helmet",
                price = 25.00,
                currency = "EUR",
                imageUrl = "https://example.com/helmet.jpg"
            )
        )
    }

    override suspend fun fetchProductDetail(productId: String): MarketplaceProduct? {
        return fetchProducts().find { it.productId == productId }
    }
}

