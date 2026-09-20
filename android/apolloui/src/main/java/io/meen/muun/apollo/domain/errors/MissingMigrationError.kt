package io.meen.apollo.domain.errors

class MissingMigrationError(message: String) : MeenError(message) {
    override val classification = ErrorClassification.UNEXPECTED
}
