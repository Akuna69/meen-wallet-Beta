package io.meen.apollo.domain.errors.rc

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.UserFacingError

class CredentialsDontMatchError : UserFacingError() {
    override val classification = ErrorClassification.EXPECTED
}