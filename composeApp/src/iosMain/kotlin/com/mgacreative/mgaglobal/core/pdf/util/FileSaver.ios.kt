package com.mgacreative.mgaglobal.core.pdf.util

actual object FileSaver {
    actual fun savePdf(fileName: String, data: ByteArray) {
        // iOS implementation would use Foundation APIs
        println("Simulating saving PDF to iOS device: $fileName")
    }
}

