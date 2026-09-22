package io.meen.apollo.domain.errors


import io.meen.apollo.data.external.UserFacingErrorMessages

class DeprecatedClientVersionError : UserFacingError(
    UserFacingErrorMessages.INSTANCE.deprecatedClientVersion()
) {
    override val classification = ErrorClassification.UNEXPECTED
}
