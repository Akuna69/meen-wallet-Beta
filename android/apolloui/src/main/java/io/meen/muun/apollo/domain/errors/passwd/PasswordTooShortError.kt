package io.meen.apollo.domain.errors.passwd


import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.UserFacingError

class PasswordTooShortError : UserFacingError(
    UserFacingErrorMessages.INSTANCE.passwordTooShort()
) {
    override val classification = ErrorClassification.EXPECTED
}
