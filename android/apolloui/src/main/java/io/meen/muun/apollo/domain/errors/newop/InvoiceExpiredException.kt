package io.meen.apollo.domain.errors.newop

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError

class InvoiceExpiredException : MuunError {

    override val classification = ErrorClassification.EXPECTED

    constructor(invoice: String) : super(invoice)
    constructor(invoice: String, cause: Throwable) : super(invoice, cause)
}
