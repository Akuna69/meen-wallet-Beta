package io.meen.apollo.domain.libwallet.errors

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

private var msg = "Libwallet failed to decrypt a payload"

class PayloadDecryptError(cause: Throwable) : MeenError(msg, cause) {
    override val classification = ErrorClassification.UNEXPECTED
}
