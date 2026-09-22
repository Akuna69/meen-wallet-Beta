package io.meen.apollo.domain.errors

class InvalidChallengeSignatureError : MeenError() {
    override val classification = ErrorClassification.EXPECTED // Wrong password/RC
}
