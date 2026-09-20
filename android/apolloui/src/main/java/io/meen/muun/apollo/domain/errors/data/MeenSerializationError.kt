package io.meen.apollo.domain.errors.data

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError
import io.meen.common.exception.PotentialBug
import okhttp3.Request

class MeenSerializationError(
    supportId: String,
    originalRequest: Request,
    cause: Throwable,
) : MeenError(cause), PotentialBug {

    override val classification = ErrorClassification.UNEXPECTED

    init {
        metadata["supportId"] = supportId
        metadata["request"] = originalRequest.url().uri().toString()
    }
}