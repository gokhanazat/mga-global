package com.mgacreative.mgaglobal.core.pdf.util

expect object FileSaver {
    fun savePdf(fileName: String, data: ByteArray)
}

