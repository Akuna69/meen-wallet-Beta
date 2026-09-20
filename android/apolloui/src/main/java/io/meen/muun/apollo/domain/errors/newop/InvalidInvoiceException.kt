package io.meen.apollo.domain.errors.newop

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError
import io.meen.common.exception.PotentialBug

class InvalidInvoiceException(invoice: String, cause: Throwable) : MeenError(
    invoice,
    cause
), PotentialBug {
    override val classification = ErrorClassification.EXPECTED
}
