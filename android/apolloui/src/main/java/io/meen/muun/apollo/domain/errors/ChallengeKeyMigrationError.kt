package io.meen.apollo.domain.errors

class ChallengeKeyMigrationError(cause: Throwable) : MeenError(
    "Failed to execute challenge key migration",
    cause
) {
    override val classification = ErrorClassification.UNEXPECTED
}
