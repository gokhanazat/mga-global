package com.mgacreative.mgaglobal.core.network

object ApiConfig {
    const val BASE_URL = ""
    
    val LOGIN_URL = "$BASE_URL/auth/login"
    val REGISTER_URL = "$BASE_URL/auth/register"
    val PRODUCTS_URL = "$BASE_URL/products"
    val UPLOAD_URL = "$BASE_URL/upload"
    val EDUCATIONS_URL = "$BASE_URL/educations"
    val CONSULTANTS_URL = "$BASE_URL/consultants"
    val ANNOUNCEMENTS_URL = "$BASE_URL/announcements"
    val SECTORS_URL = "$BASE_URL/sectors"
    val REGISTRY_URL = "$BASE_URL/registry"
    val AUDIT_LOGS_URL = "$BASE_URL/audit_logs"
    val SETTINGS_URL = "$BASE_URL/settings"
    
    fun getImageUrl(filename: String) = "$BASE_URL/image/$filename"
}

