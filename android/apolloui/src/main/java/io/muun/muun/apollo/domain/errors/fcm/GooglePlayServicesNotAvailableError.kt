package io.meen.apollo.domain.errors.fcm


import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.UserFacingError

class GooglePlayServicesNotAvailableError :
    UserFacingError(UserFacingErrorMessages.INSTANCE.googlePlayServicesNotAvailable()) {
    override val classification = ErrorClassification.UNEXPECTED
}
