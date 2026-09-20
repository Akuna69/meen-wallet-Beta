package io.meen.apollo.domain.errors.newop

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

class InvoiceMissingAmountException(invoice: String, cause: Throwable) : MeenError(invoice, cause) {
    override val classification = ErrorClassification.EXPECTED
}
