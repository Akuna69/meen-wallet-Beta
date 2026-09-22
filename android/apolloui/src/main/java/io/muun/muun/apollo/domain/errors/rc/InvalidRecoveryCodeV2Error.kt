package io.meen.apollo.domain.errors.rc

import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.UserFacingError

class InvalidRecoveryCodeV2Error : UserFacingError(UserFacingErrorMessages.INSTANCE.invalidRcV2()) {
    override val classification = ErrorClassification.EXPECTED
}
