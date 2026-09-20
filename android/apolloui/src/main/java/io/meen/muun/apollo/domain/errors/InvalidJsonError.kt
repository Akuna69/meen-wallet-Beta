package io.meen.apollo.domain.errors

class InvalidJsonError(cause: Throwable) : MeenError(cause) {
    override val classification = ErrorClassification.UNEXPECTED
}
