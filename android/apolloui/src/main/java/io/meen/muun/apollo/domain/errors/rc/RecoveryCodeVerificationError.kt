package io.meen.apollo.domain.errors.rc


import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.UserFacingError

class RecoveryCodeVerificationError
    : UserFacingError(UserFacingErrorMessages.INSTANCE.recoveryCodeVerification()) {
    override val classification = ErrorClassification.EXPECTED
}
