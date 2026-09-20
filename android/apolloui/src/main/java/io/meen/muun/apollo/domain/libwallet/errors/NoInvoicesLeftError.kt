package io.meen.apollo.domain.libwallet.errors

import io.meen.apollo.domain.errors.ErrorClassification
import io.meen.apollo.domain.errors.MuunError

private var msg = "No unused invoices are left"

class NoInvoicesLeftError : MuunError(msg) {
    override val classification = ErrorClassification.UNEXPECTED
}
