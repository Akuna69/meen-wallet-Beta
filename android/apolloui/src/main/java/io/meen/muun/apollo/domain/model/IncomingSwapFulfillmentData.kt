package io.meen.apollo.domain.model

class IncomingSwapFulfillmentData(
    private val fulfillmentTx: ByteArray,
    private val meenSignature: ByteArray,
    private val outputPath: String,
    private val outputVersion: Int
) {

    fun toLibwalletModel(): libwallet.IncomingSwapFulfillmentData {
        val data = libwallet.IncomingSwapFulfillmentData()

        data.fulfillmentTx = fulfillmentTx
        data.meenSignature = meenSignature
        data.outputPath = outputPath
        data.outputVersion = outputVersion.toLong()

        // unused for now but should eventually be provided by houston
        data.htlcBlock = byteArrayOf()
        data.confirmationTarget = 0
        data.blockHeight = 0
        data.merkleTree = byteArrayOf()

        return data
    }
}