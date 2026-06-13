package com.mgacreative.mgaglobal.manager

expect suspend fun changeAppLanguage(languageCode: String)
expect suspend fun getCurrentAppLanguage(): String?
expect fun syncPlatformLocale(languageCode: String)

