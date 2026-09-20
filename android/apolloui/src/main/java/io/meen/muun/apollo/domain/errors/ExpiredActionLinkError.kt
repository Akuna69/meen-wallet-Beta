package io.meen.apollo.domain.errors

class ExpiredActionLinkError : MeenError() {
    override val classification = ErrorClassification.EXPECTED
}
