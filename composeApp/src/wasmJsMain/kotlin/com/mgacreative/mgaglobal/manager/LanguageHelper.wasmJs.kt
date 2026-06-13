package com.mgacreative.mgaglobal.manager

import kotlinx.browser.window
import kotlinx.browser.localStorage
import com.mgacreative.mgaglobal.overrideNavigatorLanguage

actual suspend fun changeAppLanguage(languageCode: String) {
    localStorage.setItem("app_language", languageCode)
    // WASM/JS tarafÄ±nda Compose Resources'Ä±n yeni dili yÃ¼klemesi iÃ§in sayfayÄ± yenilemek en gÃ¼venli yoldur.
    window.location.reload()
}

actual suspend fun getCurrentAppLanguage(): String? {
    return localStorage.getItem("app_language") ?: "tr"
}

actual fun syncPlatformLocale(languageCode: String) {
    com.mgacreative.mgaglobal.overrideNavigatorLanguage(languageCode)
}

