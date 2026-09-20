package io.meen.apollo.domain.errors.lnurl

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError
import io.meen.apollo.domain.model.lnurl.LnUrlEvent

class UnknownLnUrlError(event: LnUrlEvent) : MeenError() {

    override val classification = ErrorClassification.UNEXPECTED

    init {
        metadata["code"] = event.code
        metadata["message"] = event.message
        metadata["metadata"] = event.metadata
    }
}
