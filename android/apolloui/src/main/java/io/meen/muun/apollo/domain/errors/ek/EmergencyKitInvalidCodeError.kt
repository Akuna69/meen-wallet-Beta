package io.meen.apollo.domain.errors.ek

import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.domain.errors.ErrorClassification


class EmergencyKitInvalidCodeError(providedCode: String) : EmergencyKitVerificationError(
    UserFacingErrorMessages.INSTANCE.emergencyKitInvalidVerificationCode()
) {

    override val classification = ErrorClassification.EXPECTED

    init {
        metadata["providedCode"] = providedCode
    }
}
