package io.meen.apollo.domain.errors

class SignupDraftFormatError(draftString: String?) : MuunError() {

    override val classification = ErrorClassification.UNEXPECTED

    init {
        metadata["draftString"] = draftString ?: "<unknown>"
    }
}