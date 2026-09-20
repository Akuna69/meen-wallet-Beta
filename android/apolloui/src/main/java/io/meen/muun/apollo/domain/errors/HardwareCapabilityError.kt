package io.meen.apollo.domain.errors

open class HardwareCapabilityError(capability: String, cause: Throwable) : MeenError(
    "Error reading hardware capability",
    cause
) {
    override val classification = ErrorClassification.UNEXPECTED

    init {
        metadata["capability"] = capability
    }
}
