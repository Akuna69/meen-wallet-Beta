package io.meen.apollo.domain.errors

class ExpiredActionLinkError : MuunError() {
    override val classification = ErrorClassification.EXPECTED
}
