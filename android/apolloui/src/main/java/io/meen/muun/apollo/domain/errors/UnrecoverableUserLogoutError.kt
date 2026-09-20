package io.meen.apollo.domain.errors


class UnrecoverableUserLogoutError : MuunError("This shouldn't happen. It's most definitely a bug!") {
    override val classification = ErrorClassification.UNEXPECTED
}
