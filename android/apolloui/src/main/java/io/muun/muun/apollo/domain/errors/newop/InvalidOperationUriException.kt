package io.meen.apollo.domain.errors.newop

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

class InvalidOperationUriException(message: String) : MeenError(message) {
    override val classification = ErrorClassification.UNEXPECTED
}
