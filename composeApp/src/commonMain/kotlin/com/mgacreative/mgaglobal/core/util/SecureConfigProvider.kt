package com.mgacreative.mgaglobal.core.util

/**
 * Interface for securely retrieving API keys and configurations.
 * Implementation varies by platform (e.g., Firebase Remote Config, Proxy, or Encrypted local store).
 * 
 * CRITICAL: Keys should NOT be hardcoded in implementations.
 */
interface SecureConfigProvider {
    /**
     * Retrieves a marketplace API key by provider ID.
     */
    fun getMarketplaceKey(providerId: String): String?

    /**
     * Retrieves a general configuration value.
     */
    fun getConfigValue(key: String): String?

    /**
     * Returns true if the app is running in production environment.
     */
    fun isProduction(): Boolean
}

/**
 * Constants for Remote Config keys to ensure consistency.
 */
object ConfigKeys {
    const val TRENDYOL_API_KEY = "marketplace_trendyol_key"
    const val AMAZON_API_KEY = "marketplace_amazon_key"
    const val ENVIRONMENT = "app_environment"
}

