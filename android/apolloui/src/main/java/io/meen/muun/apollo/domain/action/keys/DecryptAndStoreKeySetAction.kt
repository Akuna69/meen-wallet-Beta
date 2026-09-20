package io.meen.apollo.domain.action.keys

import io.meen.apollo.data.preferences.KeysRepository
import io.meen.apollo.domain.action.base.BaseAsyncAction2
import io.meen.apollo.domain.action.challenge_keys.StoreVerifiedChallengeKeyAction
import io.meen.apollo.domain.errors.passwd.PasswordIntegrityError
import io.meen.apollo.domain.libwallet.Extensions
import io.meen.apollo.domain.libwallet.toLibwallet
import io.meen.common.api.KeySet
import io.meen.common.crypto.ChallengePublicKey
import io.meen.common.utils.Encodings
import io.meen.common.utils.Preconditions
import libwallet.Libwallet
import org.bitcoinj.core.NetworkParameters
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DecryptAndStoreKeySetAction @Inject constructor(
    private val storeChallengeKey: StoreVerifiedChallengeKeyAction,
    private val keysRepository: KeysRepository,
    private val network: NetworkParameters,
) : BaseAsyncAction2<KeySet, String, Void>() {

    override fun action(keyset: KeySet, userInput: String): Observable<Void> =
        Observable.defer { decryptAndStore(keyset, userInput) }

    private fun decryptAndStore(keySet: KeySet, userInput: String): Observable<Void> {

        if (keySet.challengeKeys != null) {
            for (challengeKey in keySet.challengeKeys!!) {

                val publicKey = ChallengePublicKey(
                    Encodings.hexToBytes(challengeKey.publicKey),
                    Encodings.hexToBytes(challengeKey.salt!!), // Never null at this point
                    challengeKey.challengeVersion
                )

                storeChallengeKey.actionNow(challengeKey.type, publicKey)
            }
        }

        if (keySet.meenKey != null) {
            Preconditions.checkNotNull(keySet.meenKeyFingerprint)

            keysRepository.storeEncryptedMeenPrivateKey(keySet.meenKey!!)
            keysRepository.storeMeenKeyFingerprint(keySet.meenKeyFingerprint!!)
        }

        try {
            val decryptedKey = Libwallet.keyDecrypt(
                keySet.encryptedPrivateKey,
                userInput,
                network.toLibwallet()
            )
            val userPrivateKey = Extensions.fromLibwallet(decryptedKey.key)

            return keysRepository.storeBasePrivateKey(userPrivateKey)
        } catch (e: Exception) {
            throw PasswordIntegrityError()
        }
    }
}