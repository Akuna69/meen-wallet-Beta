package io.meen.apollo.domain.libwallet.errors

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

private var msg = "Libwallet failed to parse an invoice"

class InvoiceParsingError(val invoice: String, cause: Throwable) : MeenError(msg, cause) {

    override val classification = ErrorClassification.EXPECTED

    init {
        metadata["invoice"] = invoice
    }
}