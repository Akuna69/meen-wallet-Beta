package io.meen.apollo.domain.errors.data

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError
import io.meen.common.exception.PotentialBug

class MuunDeserializationError(cause: Exception, json: String?) : MuunError(cause), PotentialBug {

    override val classification = ErrorClassification.UNEXPECTED

    constructor(json: String) : this(IllegalArgumentException(), json)

    init {
        metadata["json"] = json.toString()
    }
}