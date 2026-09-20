package io.meen.apollo.domain.errors.lnurl

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError

class NoRouteError(message: String, domain: String) : MuunError() {

    override val classification = ErrorClassification.UNEXPECTED

    init {
        metadata["service"] = domain
        metadata["message"] = message
    }
}
