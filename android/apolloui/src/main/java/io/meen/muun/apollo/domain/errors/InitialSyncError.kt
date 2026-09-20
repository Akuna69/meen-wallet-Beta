package io.meen.apollo.domain.errors

class InitialSyncError(cause: Throwable) : MeenError(
    "Error during initial loading. Suggestion: Restart the application and try again", // not user visible
    cause
) {
    override val classification = ErrorClassification.UNEXPECTED
}
