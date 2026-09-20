package io.meen.apollo.domain.errors.notifications

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError
import io.meen.common.api.messages.MessageSpec
import io.meen.common.model.SessionStatus

class MessagePermissionsError(
    sessionUuid: String,
    messageId: Long,
    currentStatus: SessionStatus?,
    spec: MessageSpec,
) : MeenError("Received a message without the right permissions") {

    override val classification = ErrorClassification.UNEXPECTED

    init {
        metadata["sessionUuid"] = sessionUuid
        metadata["sessionStatus"] = currentStatus?.name ?: "null"
        metadata["messageId"] = messageId
        metadata["messageType"] = spec.messageType
        metadata["allowedSessionStatus"] = spec.permission.name
    }
}