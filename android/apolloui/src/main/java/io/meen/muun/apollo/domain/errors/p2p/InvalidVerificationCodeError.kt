package io.meen.apollo.domain.errors.p2p


import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.UserFacingError

class InvalidVerificationCodeError : UserFacingError(
    UserFacingErrorMessages.INSTANCE.invalidVerificationCode()
) {
    override val classification = ErrorClassification.EXPECTED
}
