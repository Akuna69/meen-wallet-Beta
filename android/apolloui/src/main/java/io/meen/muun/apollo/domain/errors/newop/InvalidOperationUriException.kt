package io.meen.apollo.domain.errors.newop

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError

class InvalidOperationUriException(message: String) : MuunError(message) {
    override val classification = ErrorClassification.UNEXPECTED
}
