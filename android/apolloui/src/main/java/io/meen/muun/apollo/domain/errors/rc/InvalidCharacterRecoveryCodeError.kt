package io.meen.apollo.domain.errors.rc

import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.UserFacingError

class InvalidCharacterRecoveryCodeError :
    UserFacingError(UserFacingErrorMessages.INSTANCE.invalidCharacterRecoveryCode()) {
    override val classification = ErrorClassification.EXPECTED
}
