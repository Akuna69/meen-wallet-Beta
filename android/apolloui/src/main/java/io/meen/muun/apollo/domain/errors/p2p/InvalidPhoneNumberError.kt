package io.meen.apollo.domain.errors.p2p


import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.UserFacingError

class InvalidPhoneNumberError : UserFacingError {

    override val classification = ErrorClassification.EXPECTED

    constructor() :
        super(UserFacingErrorMessages.INSTANCE.invalidPhoneNumber())

    constructor(cause: Throwable) :
        super(UserFacingErrorMessages.INSTANCE.invalidPhoneNumber(), cause)
}
