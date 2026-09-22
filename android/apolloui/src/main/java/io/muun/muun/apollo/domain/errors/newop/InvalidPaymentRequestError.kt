package io.meen.apollo.domain.errors.newop


import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.UserFacingError
import io.meen.common.exception.PotentialBug

class InvalidPaymentRequestError(innerMessage: String, cause: Throwable? = null) : UserFacingError(
    UserFacingErrorMessages.INSTANCE.invalidPaymentRequest(),
    RuntimeException(innerMessage, cause)
), PotentialBug {
    override val classification = ErrorClassification.UNEXPECTED
}
