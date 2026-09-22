package io.meen.apollo.domain.libwallet.errors

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

class UnknownRecoveryCodeVersionError(cause: Throwable):
    MeenError("Libwallet failed to recognize version from Recovery Code", cause) {
    override val classification = ErrorClassification.UNEXPECTED
}