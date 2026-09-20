package io.meen.apollo.data.os.secure_storage

import android.security.KeyStoreException
import io.meen.apollo.data.os.OS
import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError
import io.meen.apollo.domain.utils.getTypedClause

class MuunKeyStoreException(cause: Throwable) : MuunError(cause) {

    override val classification = ErrorClassification.UNEXPECTED

    init {
        val maybeKeystoreException = cause.getTypedClause<KeyStoreException>()

        metadata["hasKeystoreExceptionCause"] = maybeKeystoreException.isPresent

        if (maybeKeystoreException.isPresent && OS.supportsKeystoreExceptionPublicMethods()) {
            val keyStoreException = maybeKeystoreException.get()
            metadata["numericErrorCode"] = keyStoreException.numericErrorCode
            metadata["retryPolicy"] = mapRetryPolicy(keyStoreException.retryPolicy)
            metadata["isSystemError"] = keyStoreException.isSystemError
            metadata["isTransientFailure"] = keyStoreException.isTransientFailure
            metadata["requiresUserAuthentication"] = keyStoreException.requiresUserAuthentication()
            metadata["toString"] = keyStoreException.toString()
        }
    }

    private fun mapRetryPolicy(retryPolicy: Int): String {
        return when (retryPolicy) {
            1 -> "RETRY_NEVER"
            2 -> "RETRY_WITH_EXPONENTIAL_BACKOFF"
            3 -> "RETRY_WHEN_CONNECTIVITY_AVAILABLE"
            4 -> "RETRY_AFTER_NEXT_REBOOT"
            else -> "UNKNOWN: $retryPolicy"
        }
    }
}