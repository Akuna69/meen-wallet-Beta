package io.meen.apollo.domain.errors


import io.meen.apollo.data.external.UserFacingErrorMessages

class ExpiredSessionError : UserFacingError(
    UserFacingErrorMessages.INSTANCE.expiredSession()
) {
    override val classification = ErrorClassification.UNEXPECTED
}
