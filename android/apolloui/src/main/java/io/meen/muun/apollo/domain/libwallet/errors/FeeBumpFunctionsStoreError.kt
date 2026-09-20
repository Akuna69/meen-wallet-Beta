package io.meen.apollo.domain.libwallet.errors

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

private var msg = "Libwallet failed to store fee bump functions"

class FeeBumpFunctionsStoreError(
    val functions: String, cause: Throwable
) : MeenError(msg, cause) {

    override val classification = ErrorClassification.UNEXPECTED

    init {
        metadata["functions"] = functions
    }
}