package io.meen.apollo.domain.errors

class BugDetected(message: String) : MuunError(message) {
    override val classification = ErrorClassification.UNEXPECTED
}