package io.meen.apollo.domain.libwallet.errors

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

class LibwalletEmergencyKitError(cause: Throwable):
    MeenError("Emergency kit generation failed", cause) {
    override val classification = ErrorClassification.UNEXPECTED
}
