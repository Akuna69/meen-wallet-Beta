package io.meen.apollo.domain.libwallet.errors

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError

class LibwalletEmergencyKitError(cause: Throwable):
    MuunError("Emergency kit generation failed", cause) {
    override val classification = ErrorClassification.UNEXPECTED
}
