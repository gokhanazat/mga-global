package com.mgacreative.mgaglobal.core.util

actual object ImageResizer {
    actual suspend fun compressImage(bytes: ByteArray, maxWidth: Int, maxHeight: Int, quality: Int): ByteArray {
        return bytes
    }
}

