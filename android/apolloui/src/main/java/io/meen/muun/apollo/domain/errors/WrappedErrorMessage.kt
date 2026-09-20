package io.meen.apollo.domain.errors

class WrappedErrorMessage(message: String) : MeenError(message) {
    override val classification = ErrorClassification.UNEXPECTED
}