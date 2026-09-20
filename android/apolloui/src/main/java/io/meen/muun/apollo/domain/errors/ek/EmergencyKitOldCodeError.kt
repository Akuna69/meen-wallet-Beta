package io.meen.apollo.domain.errors.ek

import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.domain.errors.ErrorClassification


class EmergencyKitOldCodeError(firstExpectedDigits: String) : EmergencyKitVerificationError(
    UserFacingErrorMessages.INSTANCE.emergencyKitOldVerificationCode(firstExpectedDigits)
) {
    override val classification = ErrorClassification.EXPECTED
}
