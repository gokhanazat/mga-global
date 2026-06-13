package com.mgacreative.mgaglobal

import kotlinx.browser.window

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

// @JsFun notasyonu Kotlin/Wasm iÃ§in en gÃ¼venli JS interop yÃ¶ntemidir
@JsFun("() => Number(Date.now())")
external fun dateNow(): Double

actual fun getNowMillis(): Long = dateNow().toLong()

actual fun openUrl(url: String) {
    window.open(url, "_blank")
}

@JsFun("(content, fileName, mimeType) => { const blob = new Blob([content], {type: mimeType}); const url = URL.createObjectURL(blob); const a = document.createElement('a'); a.href = url; a.download = fileName; document.body.appendChild(a); a.click(); document.body.removeChild(a); URL.revokeObjectURL(url); }")
external fun downloadFileJs(content: String, fileName: String, mimeType: String)

actual fun saveFile(fileName: String, content: String, mimeType: String) {
    downloadFileJs(content, fileName, mimeType)
}

