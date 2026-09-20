package io.meen.apollo.domain.errors.integrity

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError


open class IntegrityError(message: String) : MeenError(message) {
    override val classification = ErrorClassification.UNEXPECTED
}
