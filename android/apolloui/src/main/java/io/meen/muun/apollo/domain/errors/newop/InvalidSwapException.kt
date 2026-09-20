package io.meen.apollo.domain.errors.newop

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError
import io.meen.common.exception.PotentialBug

class InvalidSwapException(swapUuid: String) : MuunError(
    "Validation failed for swap UUID $swapUuid"
), PotentialBug {
    override val classification = ErrorClassification.UNEXPECTED
}
