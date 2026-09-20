package io.meen.apollo.domain.errors.delete_wallet

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

class UnsettledOperationsWalletDeleteException(cause: Throwable) : MeenError(cause) {
    override val classification = ErrorClassification.EXPECTED
}