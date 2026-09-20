package io.meen.apollo.domain.errors


class TooManyRequestsError : MuunError() {
    override val classification = ErrorClassification.UNEXPECTED
}