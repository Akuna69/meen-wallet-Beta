package io.meen.apollo.domain.errors


import io.meen.apollo.data.os.secure_storage.SecureStorageProvider
import io.meen.common.exception.PotentialBug

class WeirdIncorrectAttemptsBugError(
    remainingAttempts: Int,
    maxAttempts: Int,
    debugSnapshot: SecureStorageProvider.DebugSnapshot,
) : SecureStorageError(
    debugSnapshot
), PotentialBug {

    init {
        metadata["message"] =
            "IncorrectAttempts in secure storage, was found to be higher or equal than max attempts"
        metadata["remainingAttempts"] = remainingAttempts
        metadata["maxAttempts"] = maxAttempts
        metadata["incorrectAttempts"] = maxAttempts - remainingAttempts

    }
}
