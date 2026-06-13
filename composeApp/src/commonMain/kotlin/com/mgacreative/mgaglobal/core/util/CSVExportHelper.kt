package com.mgacreative.mgaglobal.core.util

import com.mgacreative.mgaglobal.core.domain.b2b.B2BCompany

object CSVExportHelper {
    fun companiesToCsv(companies: List<B2BCompany>): String {
        // Turkish Excel usually expects semicolon (;) as separator
        val header = "ID;Åirket AdÄ±;SektÃ¶r;Ãœlke;Email;Telefon;GSM;Yetkili KiÅŸi;DoÄŸrulanmÄ±ÅŸ;Market YÄ±lÄ±;Export Hacmi;Puan;AÃ§Ä±klama"
        
        val rows = companies.map { c ->
            buildString {
                append(c.id).append(";")
                append(c.name.clean()).append(";")
                append(c.sector.clean()).append(";")
                append(c.country.clean()).append(";")
                append(c.email.clean()).append(";")
                append(c.phone.clean()).append(";")
                append(c.gsm.clean()).append(";")
                append(c.authorizedPerson.clean()).append(";")
                append(if (c.isVerified) "Evet" else "HayÄ±r").append(";")
                append(c.yearsInMarket).append(";")
                append(c.exportVolume).append(";")
                append(c.platformActivityScore).append(";")
                append(c.description.clean().take(100)) // Limit description size
            }
        }
        
        // UTF-8 BOM for Excel to recognize Turkish characters
        val bom = "\uFEFF"
        return bom + (listOf(header) + rows).joinToString("\n")
    }

    private fun String.clean(): String {
        return this.replace(";", ",")
            .replace("\n", " ")
            .replace("\r", " ")
            .trim()
    }
}

