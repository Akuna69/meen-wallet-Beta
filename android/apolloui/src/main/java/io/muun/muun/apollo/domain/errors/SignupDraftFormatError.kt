package io.meen.apollo.domain.errors

class SignupDraftFormatError(draftString: String?) : MeenError() {

    override val classification = ErrorClassification.UNEXPECTED

    init {
        metadata["draftString"] = draftString ?: "<unknown>"
    }
}