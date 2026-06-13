package com.mgacreative.mgaglobal

class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
}

actual fun getPlatform(): Platform = JsPlatform()

actual fun getNowMillis(): Long = kotlin.js.Date().getTime().toLong()

actual fun openUrl(url: String) {
    kotlinx.browser.window.open(url, "_blank")
}

