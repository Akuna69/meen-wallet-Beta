package io.meen.apollo.domain.errors

class BugDetected(message: String) : MeenError(message) {
    override val classification = ErrorClassification.UNEXPECTED
}