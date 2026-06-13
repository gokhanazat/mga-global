package com.mgacreative.mgaglobal

import android.content.Intent
import android.net.Uri
import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun getNowMillis(): Long = System.currentTimeMillis()

actual fun openUrl(url: String) {
    val context = com.mgacreative.mgaglobal.manager.LanguagePreferenceManager.appContext ?: return
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}

actual fun saveFile(fileName: String, content: String, mimeType: String) {
    val context = com.mgacreative.mgaglobal.manager.LanguagePreferenceManager.appContext ?: return
    try {
        val resolver = context.contentResolver
        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(android.provider.MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS)
            }
        }

        val collection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            // Android 10 Ã¶ncesi iÃ§in basitleÅŸtirilmiÅŸ bir yol, ancak gerÃ§ekte API 29 Ã¶ncesinde 
            // MediaStore.Downloads yok. DosyayÄ± harici depolamaya yazmak iÃ§in permission gerekir.
            // Neyse ki bu uygulama modern Android sÃ¼rÃ¼mleri hedeflenerek yapÄ±ldÄ±. 
            // 29 altÄ± iÃ§in cache veya files dir kullanabiliriz.
            android.provider.MediaStore.Files.getContentUri("external")
        }

        val uri = resolver.insert(collection, contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri)?.use { 
                it.write(content.toByteArray())
                it.flush()
            }
        }
    } catch (e: Exception) {
        println("Android Save File Error: ${e.message}")
    }
}

