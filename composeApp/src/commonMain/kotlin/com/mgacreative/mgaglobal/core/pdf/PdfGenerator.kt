package com.mgacreative.mgaglobal.core.pdf

import com.mgacreative.mgaglobal.core.domain.showroom.ShowroomProduct
import com.mgacreative.mgaglobal.core.domain.b2b.B2BCompany

expect object PdfGenerator {
    fun generateShowroomCatalog(
        products: List<ShowroomProduct>,
        company: B2BCompany? = null
    ): ByteArray
    fun generateProductDetail(product: ShowroomProduct): ByteArray
}

