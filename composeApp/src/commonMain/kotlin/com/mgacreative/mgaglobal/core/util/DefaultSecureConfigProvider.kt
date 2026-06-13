package com.mgacreative.mgaglobal.core.util

/**
 * A fail-safe implementation of [SecureConfigProvider] for development
 * or as a fallback when remote configuration is unavailable.
 */
class DefaultSecureConfigProvider(
    private val isProd: Boolean = false
) : SecureConfigProvider {

    override fun getMarketplaceKey(providerId: String): String? {
        // In a real scenario, this might check local encrypted storage 
        // or return null to trigger a fallback in the service.
        return null 
    }

    override fun getConfigValue(key: String): String? {
        return when (key) {
            ConfigKeys.ENVIRONMENT -> if (isProd) "production" else "development"
            else -> null
        }
    }

    override fun isProduction(): Boolean = isProd
}

