package com.mgacreative.mgaglobal.core.util

actual object ImageResizer {
    /**
     * Web (Wasm) tarafÄ±nda sÄ±kÄ±ÅŸtÄ±rma iÅŸlemi ÅŸimdilik ham veriyi dÃ¶ner.
     */
    actual suspend fun compressImage(bytes: ByteArray, maxWidth: Int, maxHeight: Int, quality: Int): ByteArray {
        return bytes
    }
}

