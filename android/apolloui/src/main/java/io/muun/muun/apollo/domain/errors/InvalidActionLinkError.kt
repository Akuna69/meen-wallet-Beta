package io.meen.apollo.domain.errors

class InvalidActionLinkError : MeenError {

    override val classification = ErrorClassification.EXPECTED

    constructor() : super()

    constructor(uri: String, expected: String) : super("Invalid Deeplink clicked") {
        metadata["uri"] = uri
        metadata["expected"] = expected
    }
}
