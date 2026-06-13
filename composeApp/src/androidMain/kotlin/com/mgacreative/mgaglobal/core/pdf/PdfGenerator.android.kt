package com.mgacreative.mgaglobal.core.pdf

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.util.Base64
import com.mgacreative.mgaglobal.core.domain.showroom.ShowroomProduct
import com.mgacreative.mgaglobal.core.domain.b2b.B2BCompany
import java.io.ByteArrayOutputStream
import java.net.URL

actual object PdfGenerator {
    actual fun generateShowroomCatalog(products: List<ShowroomProduct>, company: B2BCompany?): ByteArray {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 width, height

        val navy = Color.rgb(27, 38, 59)
        val titlePaint = Paint().apply {
            color = navy
            textSize = 24f
            isFakeBoldText = true
        }
        
        val bodyBoldPaint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
            isFakeBoldText = true
        }

        val bodyPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 14f
        }

        val footerPaint = Paint().apply {
            color = Color.GRAY
            textSize = 10f
        }

        // 1. Cover Page
        val coverPage = document.startPage(pageInfo)
        val coverCanvas = coverPage.canvas
        
        // Background strip
        val rectPaint = Paint().apply { color = navy }
        coverCanvas.drawRect(0f, 0f, 595f, 300f, rectPaint)
        
        titlePaint.color = Color.WHITE
        titlePaint.textSize = 32f
        coverCanvas.drawText("ÃœRÃœN KATALOÄU", 50f, 150f, titlePaint)
        
        bodyPaint.color = Color.WHITE
        bodyPaint.textSize = 18f
        coverCanvas.drawText(company?.name ?: "MGA GLOBAL Platform", 50f, 200f, bodyPaint)
        
        // Logo if available
        company?.logoUrl?.let { logoData ->
            val bitmap = if (logoData.startsWith("data:image/")) {
                try {
                    val base64String = logoData.substringAfter("base64,")
                    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                } catch (e: Exception) { null }
            } else if (logoData.startsWith("http")) {
                try {
                    val url = URL(logoData)
                    BitmapFactory.decodeStream(url.openConnection().getInputStream())
                } catch (e: Exception) { null }
            } else null

            bitmap?.let {
                val scaled = android.graphics.Bitmap.createScaledBitmap(it, 150, 150, true)
                coverCanvas.drawBitmap(scaled, 380f, 80f, null)
            }
        }
        
        // Cover Footer Info
        bodyPaint.color = Color.DKGRAY
        bodyPaint.textSize = 14f
        var coverY = 400f
        company?.let {
            coverCanvas.drawText("ÅÄ°RKET BÄ°LGÄ°LERÄ°", 50f, coverY, bodyBoldPaint)
            coverY += 30f
            coverCanvas.drawText("Telefon: ${it.phone.ifEmpty { it.gsm }}", 50f, coverY, bodyPaint)
            coverY += 20f
            coverCanvas.drawText("Email: ${it.email}", 50f, coverY, bodyPaint)
            coverY += 20f
            coverCanvas.drawText("Ãœlke: ${it.country}", 50f, coverY, bodyPaint)
        }
        
        document.finishPage(coverPage)

        // 2. Product Pages
        products.forEachIndexed { index, product ->
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            
            // Header Bar
            canvas.drawRect(0f, 0f, 595f, 60f, rectPaint)
            titlePaint.color = Color.WHITE
            titlePaint.textSize = 18f
            canvas.drawText("${company?.name ?: "KATALOG"} | ${index + 1} / ${products.size}", 40f, 38f, titlePaint)
            
            var currentY = 100f
            
            // Product Name
            titlePaint.color = navy
            titlePaint.textSize = 24f
            canvas.drawText(product.name, 40f, currentY, titlePaint)
            currentY += 30f
            
            // Price & Category
            canvas.drawText("Kategori: ${product.category}", 40f, currentY, bodyPaint)
            currentY += 20f
            canvas.drawText("Fiyat: $${product.price}", 40f, currentY, bodyBoldPaint)
            currentY += 40f
            
            // Description
            canvas.drawText("AÃ§Ä±klama:", 40f, currentY, bodyBoldPaint)
            currentY += 25f
            val descLines = wrapText(product.description, bodyPaint, 515f)
            descLines.take(5).forEach { line -> // Limit description lines to fit
                canvas.drawText(line, 40f, currentY, bodyPaint)
                currentY += 20f
            }

            // Image
            val imgData = product.imageUrl
            if (!imgData.isNullOrBlank()) {
                val bitmap = if (imgData.startsWith("data:image/")) {
                    try {
                        val base64String = imgData.substringAfter("base64,")
                        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    } catch (e: Exception) { null }
                } else if (imgData.startsWith("http")) {
                    try {
                        val url = URL(imgData)
                        BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    } catch (e: Exception) { null }
                } else null

                bitmap?.let {
                    val maxW = 515f
                    val maxH = 400f
                    var w = it.width.toFloat()
                    var h = it.height.toFloat()
                    val ratio = (maxW / w).coerceAtMost(maxH / h)
                    w *= ratio
                    h *= ratio
                    
                    val scaledBitmap = android.graphics.Bitmap.createScaledBitmap(it, w.toInt(), h.toInt(), true)
                    canvas.drawBitmap(scaledBitmap, 40f, currentY + 20f, null)
                }
            }

            // Footer
            canvas.drawRect(0f, 800f, 595f, 842f, rectPaint)
            footerPaint.color = Color.WHITE
            canvas.drawText("Bu katalog MGA GLOBAL Platform Ã¼zerinden oluÅŸturulmuÅŸtur.", 40f, 825f, footerPaint)
            
            document.finishPage(page)
        }

        if (products.isEmpty()) {
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            canvas.drawText("SeÃ§ili Ã¼rÃ¼n bulunamadÄ±.", 40f, 100f, bodyPaint)
            document.finishPage(page)
        }

        val out = ByteArrayOutputStream()
        document.writeTo(out)
        document.close()
        return out.toByteArray()
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()
        
        for (word in words) {
            if (paint.measureText(currentLine.toString() + word) < maxWidth) {
                currentLine.append("$word ")
            } else {
                lines.add(currentLine.toString())
                currentLine = StringBuilder("$word ")
            }
        }
        lines.add(currentLine.toString())
        return lines
    }

    actual fun generateProductDetail(product: ShowroomProduct): ByteArray {
        return generateShowroomCatalog(listOf(product))
    }
}


