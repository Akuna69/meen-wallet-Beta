package io.meen.apollo.domain.errors.rc

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

class FinishRecoveryCodeSetupError(cause: Throwable) : MeenError(cause) {
    override val classification = ErrorClassification.UNEXPECTED
}