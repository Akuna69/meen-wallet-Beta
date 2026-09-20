package io.meen.apollo.domain.libwallet.errors

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError

class InvalidRecoveryCodeFormatError(cause: Throwable) : MuunError(cause) {
    override val classification = ErrorClassification.EXPECTED
}
