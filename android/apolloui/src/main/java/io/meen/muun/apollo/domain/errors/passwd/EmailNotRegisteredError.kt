package io.meen.apollo.domain.errors.passwd


import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.UserFacingError

class EmailNotRegisteredError : UserFacingError(
    UserFacingErrorMessages.INSTANCE.emailNotRegistered()
) {
    override val classification = ErrorClassification.EXPECTED
}
