package io.meen.apollo.data.nfc.api

import io.meen.apollo.data.nfc.CardResponse

interface NfcSession {

    fun connect()
    fun transmit(message: ByteArray): CardResponse
    fun close()
}