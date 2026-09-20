package io.meen.apollo.domain.errors

class ChallengeKeyMigrationError(cause: Throwable) : MuunError(
    "Failed to execute challenge key migration",
    cause
) {
    override val classification = ErrorClassification.UNEXPECTED
}
