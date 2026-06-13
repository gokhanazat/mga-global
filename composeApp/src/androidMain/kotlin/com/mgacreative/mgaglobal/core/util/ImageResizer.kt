package com.mgacreative.mgaglobal.core.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual object ImageResizer {
    actual suspend fun compressImage(
        bytes: ByteArray,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int
    ): ByteArray = withContext(Dispatchers.Default) {
        try {
            // 1. Orijinal resmi yÃ¼kle
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true 
            }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

            // 2. Ã–lÃ§ekleme oranÄ±nÄ± hesapla
            var inSampleSize = 1
            if (options.outHeight > maxHeight || options.outWidth > maxWidth) {
                val halfHeight = options.outHeight / 2
                val halfWidth = options.outWidth / 2
                while (halfHeight / inSampleSize >= maxHeight && halfWidth / inSampleSize >= maxWidth) {
                    inSampleSize *= 2
                }
            }

            // 3. Resmi bellek dostu olarak yÃ¼kle
            val scaleOptions = BitmapFactory.Options().apply {
                inSampleSize = inSampleSize
            }
            val originalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, scaleOptions)
                ?: return@withContext bytes

            // 4. Kesin boyutlandÄ±rma (maxWidth/maxHeight sÄ±nÄ±rÄ±na Ã§ekme)
            val scale = Math.min(
                maxWidth.toFloat() / originalBitmap.width,
                maxHeight.toFloat() / originalBitmap.height
            )
            
            val resizedBitmap = if (scale < 1f) {
                val matrix = Matrix().apply { postScale(scale, scale) }
                Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
            } else {
                originalBitmap
            }

            // 5. WebP formatÄ±nda sÄ±kÄ±ÅŸtÄ±r
            val outputStream = ByteArrayOutputStream()
            val format = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                @Suppress("DEPRECATION")
                Bitmap.CompressFormat.WEBP
            }
            
            resizedBitmap.compress(format, quality, outputStream)
            
            val result = outputStream.toByteArray()
            
            // Bellek temizliÄŸi
            if (resizedBitmap != originalBitmap) resizedBitmap.recycle()
            originalBitmap.recycle()
            
            result
        } catch (e: Exception) {
            e.printStackTrace()
            bytes // Hata durumunda orijinali dÃ¶n
        }
    }
}

