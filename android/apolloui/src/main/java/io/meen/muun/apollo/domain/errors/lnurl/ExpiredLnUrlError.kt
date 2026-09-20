package io.meen.apollo.domain.errors.lnurl

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError

class ExpiredLnUrlError(message: String, lnUrl: String) : MuunError() {

    override val classification = ErrorClassification.EXPECTED

    init {
        metadata["message"] = message
        metadata["LNURL"] = lnUrl
    }
}
