package io.meen.apollo.presentation.ui.show_qr.bitcoin

import io.meen.apollo.domain.model.AddressType
import io.meen.apollo.domain.model.UserActivatedFeatureStatus
import io.meen.apollo.presentation.ui.show_qr.QrView
import javax.money.MonetaryAmount

interface BitcoinAddressView : QrView {

    fun setShowingAdvancedSettings(showingAdvancedSettings: Boolean)

    fun setContent(content: String, addressType: AddressType, amount: MonetaryAmount?)

    fun setTaprootState(blocksToTaproot: Int, status: UserActivatedFeatureStatus)

    fun showFullAddress(address: String, addressType: AddressType)
}
