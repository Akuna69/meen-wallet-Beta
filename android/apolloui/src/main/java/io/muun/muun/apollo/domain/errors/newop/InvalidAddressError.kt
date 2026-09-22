package io.meen.apollo.domain.errors.newop


import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.UserFacingError

class InvalidAddressError : UserFacingError(UserFacingErrorMessages.INSTANCE.invalidAddress()) {
    override val classification = ErrorClassification.EXPECTED
}
