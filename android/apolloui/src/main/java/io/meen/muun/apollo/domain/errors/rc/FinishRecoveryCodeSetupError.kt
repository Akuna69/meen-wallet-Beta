package io.meen.apollo.domain.errors.rc

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError

class FinishRecoveryCodeSetupError(cause: Throwable) : MuunError(cause) {
    override val classification = ErrorClassification.UNEXPECTED
}