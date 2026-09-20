package io.meen.apollo.domain.errors

import io.meen.apollo.data.external.UserFacingErrorMessages

class IncorrectRecoveryCodeError :
    UserFacingError(UserFacingErrorMessages.INSTANCE.incorrectRecoveryCode()) {
    override val classification = ErrorClassification.EXPECTED
}
