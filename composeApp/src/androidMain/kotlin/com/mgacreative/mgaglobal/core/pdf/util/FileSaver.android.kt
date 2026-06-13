package com.mgacreative.mgaglobal.core.pdf.util

import android.os.Environment
import java.io.File
import java.io.FileOutputStream

actual object FileSaver {
    actual fun savePdf(fileName: String, data: ByteArray) {
        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            val fos = FileOutputStream(file)
            fos.write(data)
            fos.close()
            println("Saved PDF to: ${file.absolutePath}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

