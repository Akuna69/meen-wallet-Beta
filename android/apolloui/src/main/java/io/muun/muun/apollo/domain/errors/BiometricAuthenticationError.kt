package io.meen.apollo.domain.errors

import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.domain.model.BiometricAuthenticationErrorReason

class BiometricAuthenticationError(
    val reason: BiometricAuthenticationErrorReason,
) : UserFacingError(UserFacingErrorMessages.INSTANCE.biometricsAuthenticationError(reason)) {

    override val classification = ErrorClassification.EXPECTED

    init {
        metadata["biometricAuthenticationErrorReason"] = reason
    }
}
