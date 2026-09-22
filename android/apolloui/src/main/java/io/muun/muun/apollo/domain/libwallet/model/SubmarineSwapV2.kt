package io.meen.apollo.domain.libwallet.model

import io.meen.common.crypto.hd.MeenInputSubmarineSwapV102

class SubmarineSwapV2(val swap: MeenInputSubmarineSwapV102) : libwallet.InputSubmarineSwapV2 {

    override fun blocksForExpiration(): Long =
        swap.numBlocksForExpiration.toLong()

    override fun meenPublicKey(): ByteArray =
        swap.meenPublicKey

    override fun paymentHash256(): ByteArray =
        swap.swapPaymentHash256

    override fun serverPublicKey(): ByteArray =
        swap.swapServerPublicKey

    override fun userPublicKey(): ByteArray =
        swap.userPublicKey

    override fun serverSignature(): ByteArray =
        swap.swapServerSignature?.bytes ?: ByteArray(0)
}