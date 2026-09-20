package io.meen.apollo.domain.errors.notifications

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError
import io.meen.common.api.messages.MessageOrigin
import io.meen.common.api.messages.MessageSpec

class MessageOriginError(sessionId: String, msgId: Long, origin: MessageOrigin, spec: MessageSpec) :
    MeenError("Received a message from an unexpected origin") {

    override val classification = ErrorClassification.UNEXPECTED

    init {
        metadata["sessionUuid"] = sessionId
        metadata["messageId"] = msgId
        metadata["messageType"] = spec.messageType
        metadata["origin"] = origin.name
        metadata["allowedOrigin"] = spec.allowedOrigin.name
    }

}
