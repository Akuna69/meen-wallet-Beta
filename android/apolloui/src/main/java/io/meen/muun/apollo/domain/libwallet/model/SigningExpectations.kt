package io.meen.apollo.domain.libwallet.model

import io.meen.common.crypto.hd.MuunAddress

class SigningExpectations(
    private val address: String,
    private val outputAmountInSats: Long,
    private val changeAddress: MuunAddress?,
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