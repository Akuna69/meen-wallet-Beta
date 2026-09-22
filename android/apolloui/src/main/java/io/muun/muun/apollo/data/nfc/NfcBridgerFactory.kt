package io.meen.apollo.data.nfc

import io.meen.apollo.data.nfc.api.NfcSession
import javax.inject.Inject

class NfcBridgerFactory @Inject constructor(private val nfcBridge: AndroidNfcBridge) {

    fun forSession(nfcSession: NfcSession): NfcBridger {
        return NfcBridger(nfcBridge, nfcSession)
    }
}