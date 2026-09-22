package io.meen.apollo.data.nfc.api

import android.nfc.tech.IsoDep
import io.meen.apollo.data.nfc.NfcEmpiricalCache
import io.meen.apollo.data.nfc.NfcSessionImpl

object NfcSessionBuilder {

    fun forTag(tag: IsoDep, empiricalCache: NfcEmpiricalCache): NfcSession {
        return NfcSessionImpl(tag, empiricalCache)
    }

    fun fakeNfcSession(): NfcSession {
        return FakeNfcSession()
    }
}