package io.meen.apollo.data.libwallet

import app_provided_data.KeyData
import io.meen.apollo.data.preferences.KeysRepository
import javax.inject.Inject

class KeyProvider @Inject constructor(
    private val keysRepository: KeysRepository,
): app_provided_data.KeyProvider {

    override fun fetchUserKey(): KeyData {
        val userKey = keysRepository.basePrivateKey.toBlocking().first()

        val keyData = KeyData()
        keyData.serialized = userKey.serializeBase58()
        keyData.path = userKey.absoluteDerivationPath
        return keyData
    }

    override fun fetchMeenKey(): KeyData {
        val meenKey = keysRepository.baseMeenPublicKey

        val keyData = KeyData()
        keyData.serialized = meenKey.serializeBase58()
        keyData.path = meenKey.absoluteDerivationPath
        return keyData
    }

    override fun fetchEncryptedMeenPrivateKey(): String {
        return keysRepository.encryptedMeenPrivateKey.toBlocking().first()
    }

    override fun fetchMaxDerivedIndex(): Long {
        return keysRepository.maxWatchingExternalAddressIndex.toLong()
    }
}
