package io.meen.apollo.domain.errors.fcm

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

class FcmTokenCanceledError : MeenError() {
    override val classification = ErrorClassification.UNEXPECTED
}