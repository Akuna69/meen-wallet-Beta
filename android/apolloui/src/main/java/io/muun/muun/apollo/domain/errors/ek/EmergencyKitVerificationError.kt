package io.meen.apollo.domain.errors.ek

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.UserFacingError


open class EmergencyKitVerificationError(message: String) : UserFacingError(message) {
    override val classification = ErrorClassification.EXPECTED
}
