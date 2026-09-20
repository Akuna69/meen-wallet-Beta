package io.meen.apollo.domain.errors.newop


import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.UserFacingError

class AmountTooSmallError(amountInSats: Long) : UserFacingError(
    UserFacingErrorMessages.INSTANCE.amountTooSmall()
) {

    override val classification = ErrorClassification.EXPECTED

    init {
        metadata["amountInSats"] = amountInSats
    }

}
