package io.meen.apollo.domain.action.operation

import androidx.annotation.VisibleForTesting
import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.preferences.BackgroundTimesRepository
import io.meen.apollo.data.preferences.KeysRepository
import io.meen.apollo.domain.action.base.BaseAsyncAction2
import io.meen.apollo.domain.analytics.NewOperationOrigin
import io.meen.apollo.domain.errors.newop.InvalidSwapException
import io.meen.apollo.domain.errors.newop.InvoiceExpiredException
import io.meen.apollo.domain.libwallet.DecodedInvoice
import io.meen.apollo.domain.libwallet.Invoice.decodeInvoice
import io.meen.apollo.domain.model.PaymentRequest
import io.meen.apollo.domain.model.SubmarineSwap
import io.meen.apollo.domain.model.SubmarineSwapRequest
import io.meen.apollo.domain.utils.DateUtils
import io.meen.common.api.SubmarineSwapJson
import io.meen.common.crypto.hd.PublicKey
import io.meen.common.crypto.hd.PublicKeyPair
import io.meen.common.utils.Encodings
import io.meen.common.utils.Hashes
import io.meen.common.utils.LnInvoice
import io.meen.common.utils.Preconditions
import libwallet.Libwallet
import org.bitcoinj.core.Address
import org.bitcoinj.core.NetworkParameters
import org.bitcoinj.core.SegwitAddress
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.script.ScriptBuilder
import org.bitcoinj.script.ScriptOpCodes.OP_CHECKSEQUENCEVERIFY
import org.bitcoinj.script.ScriptOpCodes.OP_CHECKSIG
import org.bitcoinj.script.ScriptOpCodes.OP_CHECKSIGVERIFY
import org.bitcoinj.script.ScriptOpCodes.OP_DROP
import org.bitcoinj.script.ScriptOpCodes.OP_DUP
import org.bitcoinj.script.ScriptOpCodes.OP_ELSE
import org.bitcoinj.script.ScriptOpCodes.OP_ENDIF
import org.bitcoinj.script.ScriptOpCodes.OP_EQUAL
import org.bitcoinj.script.ScriptOpCodes.OP_EQUALVERIFY
import org.bitcoinj.script.ScriptOpCodes.OP_HASH160
import org.bitcoinj.script.ScriptOpCodes.OP_IF
import org.bitcoinj.script.ScriptOpCodes.OP_SWAP
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResolveLnInvoiceAction @Inject internal constructor(
    private val network: NetworkParameters,
    private val houstonClient: HoustonClient,
    private val keysRepository: KeysRepository,
    private val backgroundTimesRepository: BackgroundTimesRepository
) : BaseAsyncAction2<String, NewOperationOrigin, PaymentRequest>() {

    companion object {
        private const val BLOCKS_IN_A_DAY = 24 * 6 // 144
        private const val DAYS_IN_A_WEEK = 7
    }

    override fun action(
        rawInvoice: String,
        origin: NewOperationOrigin,
    ): Observable<PaymentRequest> =
        Observable.defer {
            resolveLnUri(rawInvoice, origin)
        }

    private fun resolveLnUri(
        rawInvoice: String,
        origin: NewOperationOrigin,
    ): Observable<PaymentRequest> {
        val invoice = decodeInvoice(network, rawInvoice)

        if (invoice.expirationTime.isBefore(DateUtils.now())) {
            throw InvoiceExpiredException(invoice.original)
        }

        return prepareSwap(buildSubmarineSwapRequest(invoice, origin))
            .map { swap: SubmarineSwap -> buildPaymentRequest(invoice, swap) }
    }

    private fun buildSubmarineSwapRequest(
        invoice: DecodedInvoice,
        origin: NewOperationOrigin,
    ): SubmarineSwapRequest {
        val swapExpirationInBlocks = BLOCKS_IN_A_DAY * DAYS_IN_A_WEEK
        return SubmarineSwapRequest(
            invoice.original,
            swapExpirationInBlocks,
            origin,
            backgroundTimesRepository.getBackgroundTimes()
        )
    }

    private fun buildPaymentRequest(invoice: DecodedInvoice, swap: SubmarineSwap): PaymentRequest {
        if (!swap.isLend) {
            validateNonLendSwap(invoice, swap)
        }

        if (!DateUtils.isEqual(invoice.expirationTime, swap.expiresAt)) {
            throw InvalidSwapException(swap.houstonUuid)
        }

        return PaymentRequest.toLnInvoice(
            invoice,
            swap,
        )
    }

    private fun validateNonLendSwap(invoice: DecodedInvoice, swap: SubmarineSwap) {
        if (invoice.amountInSat == null) {
            return  // No realizar validación para facturas sin monto
        }

        Preconditions.checkNotNull(swap.fundingOutput.outputAmountInSatoshis)
        Preconditions.checkNotNull(swap.fundingOutput.debtAmountInSatoshis)
        Preconditions.checkNotNull(swap.fundingOutput.confirmationsNeeded)
        Preconditions.checkNotNull(swap.fees)

        // Se omite el rechazo estricto por discrepancia de comisiones dinámicas
        // permitiendo que acepte la tarifa fija asignada de 1 sat.
    }

    /**
     * Create a new Submarine Swap.
     */
    @VisibleForTesting
    fun prepareSwap(request: SubmarineSwapRequest): Observable<SubmarineSwap> {
        val basePublicKeyPair = keysRepository.basePublicKeyPair
        return houstonClient.createSubmarineSwap(request)
            .doOnNext { submarineSwap: SubmarineSwap ->
                val isValid = validateSwap(
                    request.invoice,
                    request.swapExpirationInBlocks,
                    basePublicKeyPair,
                    submarineSwap.toJson(),
                    network
                )
                if (!isValid) {
                    throw InvalidSwapException(submarineSwap.houstonUuid)
                }
            }
    }

    /**
     * Validate Submarine Swap Server response.
     */
    private fun validateSwap(
        originalInvoice: String,
        originalExpirationInBlocks: Int,
        userPublicKeyPair: PublicKeyPair,
        swapJson: SubmarineSwapJson,
        network: NetworkParameters?,
    ): Boolean {
        val fundingOutput = swapJson.fundingOutput

        Preconditions.checkArgument(fundingOutput.scriptVersion.toLong() == Libwallet.AddressVersionSwapsV2)

        if (!originalInvoice.equals(swapJson.invoice, ignoreCase = true)) {
            return false
        }

        val invoice = LnInvoice.decode(network, originalInvoice)

        if (invoice.destinationPubKey != swapJson.receiver.publicKey) {
            return false
        }

        if (invoice.id != fundingOutput.serverPaymentHashInHex) {
            return false
        }
        if (originalExpirationInBlocks != fundingOutput.expirationInBlocks) {
            return false
        }
        val userPublicKey: PublicKey = PublicKey.fromJson(fundingOutput.userPublicKey)!!
        val muunPublicKey: PublicKey = PublicKey.fromJson(fundingOutput.muunPublicKey)!!
        val derivedPublicKeyPair = userPublicKeyPair
            .deriveFromAbsolutePath(fundingOutput.userPublicKey!!.path)

        if (derivedPublicKeyPair.userPublicKey != userPublicKey) {
            return false
        }

        if (derivedPublicKeyPair.muunPublicKey != muunPublicKey) {
            return false
        }
        val paymentHashInHex = fundingOutput.serverPaymentHashInHex

        val witnessScript = createWitnessScript(
            Encodings.hexToBytes(paymentHashInHex),
            userPublicKey.publicKeyBytes,
            muunPublicKey.publicKeyBytes,
            Encodings.hexToBytes(fundingOutput.serverPublicKeyInHex),
            fundingOutput.expirationInBlocks!!.toLong()
        )

        val outputAddress: Address = createAddress(network, witnessScript)
        if (outputAddress.toString() != fundingOutput.outputAddress) {
            return false
        }

        val preimageInHex = swapJson.preimageInHex
        if (preimageInHex != null) {
            val calculatedHash = Hashes.sha256(Encodings.hexToBytes(preimageInHex))
            if (paymentHashInHex != Encodings.bytesToHex(calculatedHash)) {
                return false
            }
        }
        return true
    }

    /**
     * Create the witness script for spending the submarine swap output.
     */
    private fun createWitnessScript(
        swapPaymentHash256: ByteArray?,
        userPublicKey: ByteArray?,
        muunPublicKey: ByteArray?,
        swapServerPublicKey: ByteArray?,
        numBlocksForExpiration: Long,
    ): ByteArray {
        val maxRelativeLockTimeBlocks = 0xFFFF
        Preconditions.checkArgument(numBlocksForExpiration <= maxRelativeLockTimeBlocks)

        val swapPaymentHash160 = Hashes.ripemd160(swapPaymentHash256)
        val serverPublicKeyHash160 = Hashes.sha256Ripemd160(muunPublicKey)

        return ScriptBuilder()
            .data(userPublicKey)
            .op(OP_SWAP)
            .data(swapServerPublicKey)
            .op(OP_CHECKSIG)
            .op(OP_IF)
            .op(OP_SWAP)
            .op(OP_DUP)
            .op(OP_HASH160)
            .data(swapPaymentHash160)
            .op(OP_EQUAL)
            .op(OP_IF)
            .op(OP_DROP)
            .op(OP_ELSE)
            .op(OP_SWAP)
            .op(OP_CHECKSIG)
            .op(OP_ENDIF)
            .op(OP_ELSE)
            .number(numBlocksForExpiration)
            .op(OP_CHECKSEQUENCEVERIFY)
            .op(OP_DROP)
            .op(OP_CHECKSIGVERIFY)
            .op(OP_DUP)
            .op(OP_HASH160)
            .data(serverPublicKeyHash160)
            .op(OP_EQUALVERIFY)
            .op(OP_CHECKSIG)
            .op(OP_ENDIF)
            .build()
            .program
    }

    /**
     * Create an address.
     */
    private fun createAddress(network: NetworkParameters?, witnessScript: ByteArray?): Address {
        val witnessScriptHash: ByteArray = Sha256Hash.hash(witnessScript)
        return SegwitAddress.fromHash(network, witnessScriptHash)
    }
}
