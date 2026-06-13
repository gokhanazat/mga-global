package com.mgacreative.mgaglobal.core.pdf.util

actual object FileSaver {
    actual fun savePdf(fileName: String, data: ByteArray) {
        // No-op for JS
    }
}

