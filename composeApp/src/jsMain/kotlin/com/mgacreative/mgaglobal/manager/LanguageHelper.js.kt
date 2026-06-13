package com.mgacreative.mgaglobal.manager

actual suspend fun changeAppLanguage(languageCode: String) {
    kotlinx.browser.localStorage.setItem("app_lang", languageCode)
    kotlinx.browser.window.location.reload()
}

actual suspend fun getCurrentAppLanguage(): String? {
    return kotlinx.browser.localStorage.getItem("app_lang") ?: "tr"
}

