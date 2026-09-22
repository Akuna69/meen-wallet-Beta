package io.meen.apollo.domain.errors

import io.meen.common.exception.HttpException


class ApiError(cause: HttpException) : MeenError(cause) {

    override val classification = ErrorClassification.UNEXPECTED

    init {
        metadata["errorCode"] = cause.errorCode.name()
        metadata["requestId"] = cause.requestId ?: 0
        metadata["developerMessage"] = cause.developerMessage ?: "null"
    }

}