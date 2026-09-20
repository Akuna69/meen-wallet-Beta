package io.meen.apollo.data.nfc

@Suppress("ArrayInDataClass")
data class CardResponse(
    val response: ByteArray,
    val statusCode: Int,
)