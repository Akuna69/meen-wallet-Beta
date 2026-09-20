package io.meen.apollo.domain.errors.newop

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError
import io.meen.common.exception.PotentialBug

class InvalidInvoiceException(invoice: String, cause: Throwable) : MuunError(
    invoice,
    cause
), PotentialBug {
    override val classification = ErrorClassification.EXPECTED
}
