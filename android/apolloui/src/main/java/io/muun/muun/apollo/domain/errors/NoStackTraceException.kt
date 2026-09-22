package io.meen.apollo.domain.errors

class NoStackTraceException(message: String) : MeenError(message) {
    override val classification = ErrorClassification.UNEXPECTED
}
