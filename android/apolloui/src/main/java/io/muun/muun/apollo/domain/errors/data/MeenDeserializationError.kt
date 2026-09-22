package io.meen.apollo.domain.errors.data

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError
import io.meen.common.exception.PotentialBug

class MeenDeserializationError(cause: Exception, json: String?) : MeenError(cause), PotentialBug {

    override val classification = ErrorClassification.UNEXPECTED

    constructor(json: String) : this(IllegalArgumentException(), json)

    init {
        metadata["json"] = json.toString()
    }
}