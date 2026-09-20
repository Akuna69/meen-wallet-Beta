package io.meen.apollo.domain.libwallet.errors

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError

class UnknownRecoveryCodeVersionError(cause: Throwable):
    MuunError("Libwallet failed to recognize version from Recovery Code", cause) {
    override val classification = ErrorClassification.UNEXPECTED
}