package io.meen.apollo.domain.libwallet.errors

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

private var msg = "Libwallet failed to derive an address"

class AddressDerivationError(val version: Int, val path: String, cause: Throwable) :
    MeenError(msg, cause) {

    override val classification = ErrorClassification.UNEXPECTED

    init {
        metadata["version"] = version
        metadata["path"] = path
    }
}
