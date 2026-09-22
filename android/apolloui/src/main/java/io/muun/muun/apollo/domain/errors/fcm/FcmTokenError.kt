package io.meen.apollo.domain.errors.fcm

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

class FcmTokenError(cause: Throwable) : MeenError(cause) {
    override val classification = ErrorClassification.UNEXPECTED
}
