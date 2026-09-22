package io.meen.apollo.domain.errors.p2p


import android.content.Intent
import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.UserFacingError

class InvalidPictureError : UserFacingError {

    override val classification = ErrorClassification.EXPECTED

    constructor() : super(UserFacingErrorMessages.INSTANCE.invalidPicture())

    constructor(resultIntent: Intent) : super(UserFacingErrorMessages.INSTANCE.invalidPicture()) {
        metadata["intent"] = resultIntent.toString()
        metadata["action"] = resultIntent.action ?: "null"
        metadata["data"] = resultIntent.dataString ?: "null"
        metadata["extrasSize"] = resultIntent.extras?.size() ?: 0

        if (resultIntent.extras != null) {
            var extras = ""
            for (key in resultIntent.extras!!.keySet()) {
                extras += "$key=${resultIntent.extras!![key]} "
            }

            metadata["extras"] = extras
        }
    }
}
