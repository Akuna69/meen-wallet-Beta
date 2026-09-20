package io.meen.apollo.domain.errors.lnurl

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

class InvalidLnUrlError(text: String) : MeenError() {

    override val classification = ErrorClassification.EXPECTED

    init {
        metadata["text"] = text
    }
}
