package io.meen.apollo.presentation.ui.show_qr.unified

import io.meen.apollo.domain.libwallet.DecodedBitcoinUri
import io.meen.apollo.domain.model.AddressType
import io.meen.apollo.domain.model.UserActivatedFeatureStatus
import io.meen.apollo.presentation.ui.show_qr.QrView
import javax.money.MonetaryAmount

interface ShowUnifiedQrView : QrView {

    fun setShowHighFeesWarning()

    fun setLoading(loading: Boolean)

    fun setBitcoinUri(
        bitcoinUri: DecodedBitcoinUri,
        addressType: AddressType,
        amount: MonetaryAmount?,
    )

    fun resetAmount()

    fun showFullContent(bitcoinUri: String, address: String, invoice: String)

    fun setTaprootState(status: UserActivatedFeatureStatus)

    fun refresh()

}
