package io.meen.apollo.domain.libwallet.errors

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MeenError

private var msg = "No unused invoices are left"

class NoInvoicesLeftError : MeenError(msg) {
    override val classification = ErrorClassification.UNEXPECTED
}
