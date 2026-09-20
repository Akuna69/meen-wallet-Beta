package io.meen.apollo.data.apis

import com.google.android.gms.auth.UserRecoverableAuthException
import io.meen.common.utils.ExceptionUtils

class DriveError(cause: Throwable) : RuntimeException(cause) {

    fun isMissingPermissions() =
        ExceptionUtils.getTypedCause(this, UserRecoverableAuthException::class.java).isPresent

}

