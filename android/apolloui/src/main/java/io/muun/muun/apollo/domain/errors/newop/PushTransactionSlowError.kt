package io.meen.apollo.domain.errors.newop

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.UserFacingError

class PushTransactionSlowError(cause: Throwable) : UserFacingError(cause) {
    override val classification = ErrorClassification.UNEXPECTED
}