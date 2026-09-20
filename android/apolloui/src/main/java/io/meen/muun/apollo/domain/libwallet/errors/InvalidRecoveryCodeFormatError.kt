package io.meen.apollo.domain.libwallet.errors

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

class InvalidRecoveryCodeFormatError(cause: Throwable) : MeenError(cause) {
    override val classification = ErrorClassification.EXPECTED
}
