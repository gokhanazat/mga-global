package com.mgacreative.mgaglobal.core.pdf.util

actual object FileSaver {
    actual fun savePdf(fileName: String, data: ByteArray) {
        // Implementation for Web could use browser APIs to trigger a download
        // For now, leaving it empty to fix build
    }
}

