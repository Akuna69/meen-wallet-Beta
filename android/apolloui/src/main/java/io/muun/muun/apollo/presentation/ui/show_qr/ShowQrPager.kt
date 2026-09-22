package io.meen.apollo.presentation.ui.show_qr

import io.meen.apollo.domain.selector.UserPreferencesSelector
import io.meen.apollo.presentation.ui.show_qr.bitcoin.BitcoinAddressQrFragment
import io.meen.apollo.presentation.ui.show_qr.ln.LnInvoiceQrFragment
import io.meen.common.model.ReceiveFormatPreference

class ShowQrPager(userPreferencesSel: UserPreferencesSelector) {

    private val inOrder = when (userPreferencesSel.get().receivePreference) {
        ReceiveFormatPreference.ONCHAIN -> arrayOf(ShowQrPage.BITCOIN, ShowQrPage.LN)
        ReceiveFormatPreference.LIGHTNING -> arrayOf(ShowQrPage.LN, ShowQrPage.BITCOIN)
        ReceiveFormatPreference.UNIFIED -> throw IllegalStateException() // should not happen
    }

    fun at(position: Int): ShowQrPage =
        inOrder[position]

    fun classAt(position: Int) =
        when (at(position)) {
            ShowQrPage.BITCOIN -> BitcoinAddressQrFragment::class
            ShowQrPage.LN -> LnInvoiceQrFragment::class
        }
}