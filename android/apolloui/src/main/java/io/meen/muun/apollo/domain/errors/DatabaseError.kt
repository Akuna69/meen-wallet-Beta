package io.meen.apollo.domain.errors


class DatabaseError(message: String, cause: Throwable) : MuunError(message, cause) {
    override val classification = ErrorClassification.UNEXPECTED
}
