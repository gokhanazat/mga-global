package com.mgacreative.mgaglobal.core.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
// Apple Native API'larÄ± (Sadece iOS hedefinde derlenir)
// import platform.UIKit.* 
// import platform.Foundation.*
// import platform.CoreGraphics.*

actual object ImageResizer {
    actual suspend fun compressImage(
        bytes: ByteArray,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int
    ): ByteArray = withContext(Dispatchers.Default) {
        // iOS tarafÄ±na geÃ§tiÄŸinizde buraya CoreGraphics/UIKit tabanlÄ± 
        // resim iÅŸleme kodu eklenecektir. Demo aÅŸamasÄ±nda orijinali dÃ¶nÃ¼yoruz.
        bytes 
    }
}

