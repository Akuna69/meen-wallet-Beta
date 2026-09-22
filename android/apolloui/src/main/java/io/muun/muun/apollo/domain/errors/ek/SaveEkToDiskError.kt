package io.meen.apollo.domain.errors.ek

import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.UserFacingError

class SaveEkToDiskError(cause: Throwable) : UserFacingError(
    UserFacingErrorMessages.INSTANCE.saveEkToDisk(),
    cause
) {
    override val classification = ErrorClassification.UNEXPECTED
}