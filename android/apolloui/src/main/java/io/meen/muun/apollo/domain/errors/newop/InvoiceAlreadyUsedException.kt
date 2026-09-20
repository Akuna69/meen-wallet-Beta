package io.meen.apollo.domain.errors.newop

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError

class InvoiceAlreadyUsedException(invoice: String, cause: Throwable) : MuunError(invoice, cause) {
    override val classification = ErrorClassification.EXPECTED
}
