package io.meen.apollo.data.secure_key_value_storage

import io.meen.apollo.domain.libwallet.LibwalletClient
import io.meen.apollo.domain.secure_key_value_storage.Secret
import javax.inject.Inject

class SecureKeyValueStorageRepository @Inject constructor(
    private val libwalletClient: LibwalletClient,
) {

    fun put(key: String, value: ByteArray) {
        libwalletClient.secureKeyValueStoragePut(key, value)
    }

    fun get(key: String): Secret {
        return libwalletClient.secureKeyValueStorageGet(key)
    }

    fun delete(key: String) {
        libwalletClient.secureKeyValueStorageDelete(key)
    }

    fun wipe() {
        libwalletClient.secureKeyValueStorageWipe()
    }
}
