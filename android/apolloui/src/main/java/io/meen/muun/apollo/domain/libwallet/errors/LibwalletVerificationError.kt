package io.meen.apollo.domain.libwallet.errors

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError


class LibwalletVerificationError(cause: Throwable):
    MeenError("Libwallet rejected the transaction during verification", cause) {
    override val classification = ErrorClassification.UNEXPECTED
}
