package io.meen.apollo.domain.errors.notifications

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError


class UnknownNotificationTypeError(type: String) :
    MuunError("Unknown notification type") {

    override val classification = ErrorClassification.UNEXPECTED

    init {
        metadata["type"] = type
    }

}
