package io.meen.apollo.domain.libwallet.model

import io.meen.common.crypto.hd.MeenInputIncomingSwap
import io.meen.common.utils.Encodings

class InputIncomingSwap(val swap: MeenInputIncomingSwap) : libwallet.InputIncomingSwap {

    override fun htlcTx(): ByteArray =
        swap.htlcTx

    override fun paymentHash256(): ByteArray =
        swap.paymentHash256

    override fun swapServerPublicKey(): String =
        Encodings.bytesToHex(swap.swapServerPublicKey)

    override fun sphinx(): ByteArray =
        swap.sphinx

    override fun expirationHeight(): Long =
        swap.expirationHeight

    override fun collectInSats(): Long =
        swap.collectInSats

    override fun htlcOutputKeyPath(): String =
        swap.htlcOutputKeyPath

    override fun preimage(): ByteArray =
        swap.preimageHex?.let(Encodings::hexToBytes) ?: ByteArray(0)
}