package com.mgacreative.mgaglobal.core.util

expect object ImageResizer {
    /**
     * SÄ±kÄ±ÅŸtÄ±rma ve Yeniden BoyutlandÄ±rma Ä°ÅŸlemi
     * @param bytes Orijinal resim verisi
     * @param maxWidth Hedef geniÅŸlik (VarsayÄ±lan 800)
     * @param maxHeight Hedef yÃ¼kseklik (VarsayÄ±lan 800)
     * @param quality SÄ±kÄ±ÅŸtÄ±rma kalitesi (1-100, VarsayÄ±lan 80)
     * @return SÄ±kÄ±ÅŸtÄ±rÄ±lmÄ±ÅŸ resim verisi (WebP veya JPEG)
     */
    suspend fun compressImage(bytes: ByteArray, maxWidth: Int = 800, maxHeight: Int = 800, quality: Int = 80): ByteArray
}

