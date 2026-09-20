package io.meen.apollo.domain.errors.integrity

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError


open class IntegrityError(message: String) : MuunError(message) {
    override val classification = ErrorClassification.UNEXPECTED
}
