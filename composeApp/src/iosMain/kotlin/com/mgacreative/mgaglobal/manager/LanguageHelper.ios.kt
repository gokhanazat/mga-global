package com.mgacreative.mgaglobal.manager

import platform.Foundation.NSUserDefaults

actual suspend fun changeAppLanguage(languageCode: String) {
    NSUserDefaults.standardUserDefaults.setObject(languageCode, forKey = "selected_language")
}

actual suspend fun getCurrentAppLanguage(): String? {
    return NSUserDefaults.standardUserDefaults.stringForKey("selected_language")
}

