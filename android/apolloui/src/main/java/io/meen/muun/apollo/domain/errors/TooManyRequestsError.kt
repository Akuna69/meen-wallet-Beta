package io.meen.apollo.domain.errors


class TooManyRequestsError : MeenError() {
    override val classification = ErrorClassification.UNEXPECTED
}