package io.meen.apollo.presentation.ui.show_qr.unified

import android.annotation.SuppressLint
import android.os.Bundle
import icepick.State
import io.meen.apollo.data.external.Globals
import io.meen.apollo.domain.action.address.GenerateBip21UriAction
import io.meen.apollo.domain.analytics.AnalyticsEvent.ADDRESS_ORIGIN
import io.meen.apollo.domain.libwallet.DecodedBitcoinUri
import io.meen.apollo.domain.libwallet.Invoice
import io.meen.apollo.domain.model.AddressGroup
import io.meen.apollo.domain.model.AddressType
import io.meen.apollo.domain.model.BitcoinAmount
import io.meen.apollo.domain.model.MeenFeature
import io.meen.apollo.domain.selector.FeatureSelector
import io.meen.apollo.domain.selector.UserActivatedFeatureStatusSelector
import io.meen.apollo.domain.selector.UserPreferencesSelector
import io.meen.apollo.domain.selector.WaitForIncomingLnPaymentSelector
import io.meen.apollo.presentation.ui.base.di.PerFragment
import io.meen.apollo.presentation.ui.bundler.BitcoinAmountBundler
import io.meen.apollo.presentation.ui.show_qr.QrPresenter
import io.meen.common.bitcoinj.BitcoinUri
import org.bitcoinj.core.NetworkParameters
import java.util.Locale
import javax.inject.Inject

@PerFragment
class ShowUnifiedQrPresenter @Inject constructor(
    private val generateBip21Uri: GenerateBip21UriAction,
    private val waitForIncomingLnPaymentSel: WaitForIncomingLnPaymentSelector,
    private val userPreferencesSel: UserPreferencesSelector,
    private val userActivatedFeatureStatusSel: UserActivatedFeatureStatusSelector,
    private val featureSelector: FeatureSelector,
    private val networkParameters: NetworkParameters,
) : QrPresenter<ShowUnifiedQrView>() {

    @State
    lateinit var invoice: String

    @State
    lateinit var legacyAddress: String

    @State
    lateinit var segwitAddress: String

    @State
    lateinit var taprootAddress: String

    @State(BitcoinAmountBundler::class)
    @JvmField
    var amount: BitcoinAmount? = null

    @State
    @JvmField
    var addressType: AddressType = getDefaultAddressType()

    override fun setUp(arguments: Bundle) {
        super.setUp(arguments)

        if (featureSelector.get(MeenFeature.HIGH_FEES_RECEIVE_FLOW)) {
            view.setShowHighFeesWarning()
        }

        generateBip21Uri
            .state
            .compose(handleStates(this::handleLoading, this::handleError))
            .doOnNext { newUri -> onNewBitcoinUri(newUri) }
            .let(this::subscribeTo)

        // We want to re-generate the uri (has an invoice) each time we come back to this fragment.
        view.refresh()

        userActivatedFeatureStatusSel.watchTaproot()
            .doOnNext { view.setTaprootState(it) }
            .let(this::subscribeTo)
    }

    internal fun generateNewUri() {
        generateBip21Uri.reset()
        generateBip21Uri.run(amount)
    }

    fun generateNewEmptyUri() {
        view.resetAmount()
    }

    private fun handleLoading(loading: Boolean) {
        view.setLoading(loading)
    }

    private fun onNewBitcoinUri(newBitcoinUri: DecodedBitcoinUri) {
        this.invoice = newBitcoinUri.invoice.original
        this.legacyAddress = newBitcoinUri.addressGroup.legacy
        this.segwitAddress = newBitcoinUri.addressGroup.segwit
        this.taprootAddress = newBitcoinUri.addressGroup.taproot
        this.amount = newBitcoinUri.amount

        view.setBitcoinUri(newBitcoinUri, addressType, amount?.inInputCurrency)

        subscribeTo(waitForIncomingLnPaymentSel.watchInvoice(newBitcoinUri.invoice.original)) {
            generateNewEmptyUri()
        }
    }

    override fun hasLoadedCorrectly(): Boolean =
        ::invoice.isInitialized

    override fun showFullContentInternal() {
        val decodeBitcoinUri = buildDecodeBitcoinUri()
        view.showFullContent(
            decodeBitcoinUri.getUriFor(addressType),
            decodeBitcoinUri.addressGroup.getAddress(addressType),
            decodeBitcoinUri.invoice.original
        )
    }

    override fun getQrContent(): String =
        getBitcoinUriStringFor(addressType)

    fun switchAddressType(newAddressType: AddressType) {
        addressType = newAddressType
        onNewBitcoinUri(buildDecodeBitcoinUri())
    }

    fun setAmount(bitcoinAmount: BitcoinAmount?) {
        this.amount = bitcoinAmount
        generateNewUri()
    }

    private fun getBitcoinUriStringFor(addressType: AddressType): String =
        buildDecodeBitcoinUri().getUriFor(addressType)

    // TODO store DecodeBitcoinUri object instead of storing "its parts" and rebuilding it
    private fun buildDecodeBitcoinUri() = DecodedBitcoinUri(
        AddressGroup(legacyAddress, segwitAddress, taprootAddress),
        Invoice.decodeInvoice(networkParameters, invoice),
        amount
    )

    @SuppressLint("DefaultLocale")
    private fun getDefaultAddressType() =
        try {
            AddressType.valueOf(userPreferencesSel.get().defaultAddressType.uppercase(Locale.getDefault()))
        } catch (e: Throwable) {
            AddressType.SEGWIT
        }

    fun copyAddress() {
        if (hasLoadedCorrectly()) {
            val decodeBitcoinUri = buildDecodeBitcoinUri()

            val copyContent = if (amount != null) {
                BitcoinUri.convertToBitcoinUri(
                    Globals.INSTANCE.network,
                    decodeBitcoinUri.addressGroup.getAddress(addressType),
                    amount!!.inSatoshis
                )
            } else {
                decodeBitcoinUri.addressGroup.getAddress(addressType)
            }
            parentPresenter.copyQrContent(copyContent, ADDRESS_ORIGIN.UNIFIED_QR_ADDRESS_ONLY)
        }
    }

    fun copyInvoice() {
        if (hasLoadedCorrectly()) {
            parentPresenter.copyQrContent(invoice, ADDRESS_ORIGIN.UNIFIED_QR_INVOICE_ONLY)
        }
    }
}
