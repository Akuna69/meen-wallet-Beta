package io.meen.apollo.domain.action.ek

import io.meen.apollo.data.preferences.KeysRepository
import io.meen.apollo.domain.action.base.BaseAsyncAction0
import io.meen.common.crypto.ChallengePublicKey
import io.meen.common.crypto.ChallengeType
import io.meen.common.utils.Encodings
import io.meen.common.utils.Preconditions
import rx.Observable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetOrCreateEncryptedBasePrivateKeyAction @Inject constructor(
    private val keysRepository: KeysRepository,
) : BaseAsyncAction0<String>() {
    /**
     * Prepare the emergency kit for export, and render the HTML.
     */
    override fun action(): Observable<String> {
        if (keysRepository.hasEncryptedBasePrivateKey) {
            return keysRepository.encryptedBasePrivateKey
        }

        Preconditions.checkState(keysRepository.hasChallengePublicKey(ChallengeType.RECOVERY_CODE))
        Preconditions.checkState(keysRepository.hasEncryptedMeenPrivateKey)
        Preconditions.checkState(keysRepository.hasBasePrivateKey)

        val encryptedMeenPrivateKey = keysRepository.encryptedMeenPrivateKey.toBlocking().first()
        val basePrivateKey = keysRepository.basePrivateKey.toBlocking().first()

        return keysRepository.getChallengePublicKey(ChallengeType.RECOVERY_CODE)
            .map { challengePublicKey: ChallengePublicKey ->
                val birthday: Long = 0

                challengePublicKey.encryptPrivateKey(
                    encryptedMeenPrivateKey,
                    basePrivateKey,
                    birthday
                )
            }
            .flatMap { encryptedKey: String ->
                Preconditions.checkNotNull(encryptedKey)
                Timber.d("Storing encrypted Apollo private key in secure storage.")
                //TODO: why is this set coupled to the encryptedBasePrivateKey?
                keysRepository.storeUserKeyFingerprint(
                    Encodings.bytesToHex(basePrivateKey.fingerprint)
                )

                keysRepository.storeEncryptedBasePrivateKey(encryptedKey)

                Observable.just(encryptedKey)
            }
    }
}