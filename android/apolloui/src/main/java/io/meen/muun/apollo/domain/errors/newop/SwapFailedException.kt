package io.meen.apollo.domain.errors.newop

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

class SwapFailedException(cause: Throwable) : MeenError(cause) {
    override val classification = ErrorClassification.UNEXPECTED
}
