package io.meen.apollo.domain.libwallet.model

import io.meen.common.crypto.hd.MeenAddress

class SigningExpectations(
    private val address: String,
    private val outputAmountInSats: Long,
    private val changeAddress: MeenAddress?,
    private val feeInSatoshis: Long,
    private val isAlternativeTx: Boolean,
) {
    fun toLibwalletModel(): libwallet.SigningExpectations {
        return libwallet.SigningExpectations(
            address,
            outputAmountInSats,
            changeAddress?.let { Address(it) },
            feeInSatoshis,
            isAlternativeTx
        )
    }
}