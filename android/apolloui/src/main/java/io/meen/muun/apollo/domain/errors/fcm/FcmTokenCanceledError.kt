package io.meen.apollo.domain.errors.fcm

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError

class FcmTokenCanceledError : MuunError() {
    override val classification = ErrorClassification.UNEXPECTED
}