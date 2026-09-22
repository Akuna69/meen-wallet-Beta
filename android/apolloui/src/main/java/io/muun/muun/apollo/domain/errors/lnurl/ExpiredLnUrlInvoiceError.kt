package io.meen.apollo.domain.errors.lnurl

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

class ExpiredLnUrlInvoiceError(domain: String, invoice: String) : MeenError() {

    override val classification = ErrorClassification.EXPECTED

    init {
        metadata["service"] = domain
        metadata["invoice"] = invoice
    }
}
