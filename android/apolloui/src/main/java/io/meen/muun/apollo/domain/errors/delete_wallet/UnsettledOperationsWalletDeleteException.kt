package io.meen.apollo.domain.errors.delete_wallet

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError

class UnsettledOperationsWalletDeleteException(cause: Throwable) : MuunError(cause) {
    override val classification = ErrorClassification.EXPECTED
}