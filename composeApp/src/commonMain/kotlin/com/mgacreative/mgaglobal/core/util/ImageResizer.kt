package com.mgacreative.mgaglobal.core.util

expect object ImageResizer {
    /**
     * Sıkıştırma ve Yeniden Boyutlandırma İşlemi
     * @param bytes Orijinal resim verisi
     * @param maxWidth Hedef genişlik (Varsayılan 800)
     * @param maxHeight Hedef yükseklik (Varsayılan 800)
     * @param quality Sıkıştırma kalitesi (1-100, Varsayılan 80)
     * @return Sıkıştırılmış resim verisi (WebP veya JPEG)
     */
    suspend fun compressImage(bytes: ByteArray, maxWidth: Int = 800, maxHeight: Int = 800, quality: Int = 80): ByteArray
}

