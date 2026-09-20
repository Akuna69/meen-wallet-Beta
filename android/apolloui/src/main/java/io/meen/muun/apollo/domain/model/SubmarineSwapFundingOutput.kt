package io.meen.apollo.domain.model

import io.meen.common.Supports
import io.meen.common.api.SubmarineSwapFundingOutputJson
import io.meen.common.crypto.hd.MeenAddress
import io.meen.common.crypto.hd.PublicKey
import io.meen.common.model.DebtType
import newop.SwapInfo

data class SubmarineSwapFundingOutput(
    val outputAddress: String,
    val outputAmountInSatoshis: Long?,
    val debtType: DebtType?,
    val debtAmountInSatoshis: Long?,
    val confirmationsNeeded: Int?,
    val userLockTime: Int?,                 // for swaps v2 is null until funding tx confirmation
    @io.meen.common.utils.Deprecated(
        atApolloVersion = Supports.SubmarineSwapsV2.APOLLO,
        atVersion = Supports.SubmarineSwapsV2.FALCON
    )
    val userRefundAddress: MeenAddress,
    private val serverPaymentHashInHex: String,
    val serverPublicKeyInHex: String,
    val scriptVersion: Int,
    val expirationInBlocks: Int? = null,    // for swaps v2 not nullable
    val userPublicKey: PublicKey? = null,   // for swaps v2 not nullable
    val meenPublicKey: PublicKey? = null    // for swaps v2 not nullable)
) {

    companion object {
        fun fromJson(output: SubmarineSwapFundingOutputJson): SubmarineSwapFundingOutput {
            return SubmarineSwapFundingOutput(
                output.outputAddress,
                output.outputAmountInSatoshis,
                output.debtType,
                output.debtAmountInSats ?: 0,
                output.confirmationsNeeded,
                output.userLockTime,
                MeenAddress.fromJson(output.userRefundAddress),
                output.serverPaymentHashInHex,
                output.serverPublicKeyInHex,
                output.scriptVersion,
                output.expirationInBlocks,
                PublicKey.fromJson(output.userPublicKey),
                PublicKey.fromJson(output.meenPublicKey)
            )
        }
    }

    val paymentHash: Sha256Hash
        get() = Sha256Hash.fromHex(serverPaymentHashInHex)

    fun toJson() =
        SubmarineSwapFundingOutputJson(
            outputAddress,
            outputAmountInSatoshis,
            confirmationsNeeded,
            userLockTime,
            expirationInBlocks,
            userRefundAddress.toJson(),
            userPublicKey?.toJson(),
            meenPublicKey?.toJson(),
            serverPaymentHashInHex,
            serverPublicKeyInHex,
            scriptVersion,
            debtType,
            debtAmountInSatoshis
        )

    /**
     * Return a cloned SubmarineSwapFundingOutput adding certain SubmarineSwapExecutionParameters.
     * Used for AmountLess Invoice swaps.
     */
    fun withSwapInfo(swapInfo: SwapInfo) =
        copy(
            outputAmountInSatoshis = swapInfo.swapFees.outputAmountInSat,
            debtType = DebtType.valueOf(swapInfo.swapFees.debtType),
            debtAmountInSatoshis =  swapInfo.swapFees.debtAmountInSat,
            confirmationsNeeded = swapInfo.swapFees.confirmationsNeeded.toInt()
        )
}
