package io.meen.apollo.domain.errors.lnurl

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError

class InvalidLnUrlError(text: String) : MuunError() {

    override val classification = ErrorClassification.EXPECTED

    init {
        metadata["text"] = text
    }
}
