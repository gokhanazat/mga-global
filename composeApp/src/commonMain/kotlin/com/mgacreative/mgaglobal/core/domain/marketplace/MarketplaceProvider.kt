package com.mgacreative.mgaglobal.core.domain.marketplace

/**
 * Defines the classification of a marketplace provider.
 */
enum class ProviderType {
    LOCAL, GLOBAL, PRIVATE
}

/**
 * Domain model representing a marketplace service provider (e.g., Amazon, Alibaba, local trade portals).
 * Designed to be read-only in the client applications and fully compatible with Firebase.
 * 
 * @property providerId Unique identifier for the provider.
 * @property name Human-readable name of the marketplace.
 * @property type Classification of the marketplace (Local, Global, or Private integration).
 * @property logoUrl URL for the marketplace brand logo.
 * @property baseApiUrl The root endpoint for API communications with this provider.
 * @property active Whether this provider is currently available for integration.
 */
data class MarketplaceProvider(
    val providerId: String = "",
    val name: String = "",
    val type: ProviderType = ProviderType.GLOBAL,
    val logoUrl: String = "",
    val baseApiUrl: String = "",
    val active: Boolean = true
)

