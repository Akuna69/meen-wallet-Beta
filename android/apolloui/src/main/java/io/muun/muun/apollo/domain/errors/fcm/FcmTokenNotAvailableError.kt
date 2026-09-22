package io.meen.apollo.domain.errors.fcm


import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.UserFacingError

class FcmTokenNotAvailableError : UserFacingError(
    UserFacingErrorMessages.INSTANCE.fcmTokenNotAvailable()
) {
    override val classification = ErrorClassification.UNEXPECTED
}
