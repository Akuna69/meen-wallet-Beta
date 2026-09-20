package io.meen.apollo.presentation.ui.show_qr.ln

import io.meen.apollo.domain.libwallet.DecodedInvoice
import io.meen.apollo.presentation.ui.show_qr.QrView
import javax.money.MonetaryAmount

interface LnInvoiceView : QrView {

    fun setShowHighFeesWarning()

    fun setShowingAdvancedSettings(showingAdvancedSettings: Boolean)

    fun setLoading(loading: Boolean)

    fun setInvoice(invoice: DecodedInvoice, amount: MonetaryAmount?)

    fun showFullContent(invoice: String)

    fun resetAmount()

    fun refresh()

}
