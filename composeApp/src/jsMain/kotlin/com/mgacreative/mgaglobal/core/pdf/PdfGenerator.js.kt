package com.mgacreative.mgaglobal.core.pdf

import com.mgacreative.mgaglobal.core.domain.showroom.ShowroomProduct
import com.mgacreative.mgaglobal.core.domain.b2b.B2BCompany

actual object PdfGenerator {
    actual fun generateShowroomCatalog(
        products: List<ShowroomProduct>,
        company: B2BCompany?
    ): ByteArray {
        return ByteArray(0)
    }

    actual fun generateProductDetail(product: ShowroomProduct): ByteArray {
        return ByteArray(0)
    }
}

