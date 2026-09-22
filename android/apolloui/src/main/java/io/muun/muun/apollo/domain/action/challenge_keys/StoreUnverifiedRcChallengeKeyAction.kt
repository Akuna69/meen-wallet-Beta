package io.meen.apollo.domain.action.challenge_keys

import io.meen.apollo.data.preferences.KeysRepository
import io.meen.apollo.domain.action.base.BaseAsyncAction1
import io.meen.apollo.domain.utils.toVoid
import io.meen.common.crypto.ChallengePublicKey
import io.meen.common.crypto.ChallengeType
import rx.Observable
import javax.inject.Inject

class StoreUnverifiedRcChallengeKeyAction @Inject constructor(
    private val keysRepository: KeysRepository,
) : BaseAsyncAction1<ChallengePublicKey, Void>() {

    /**
     * Store an UNVERIFIED Recovery Code ChallengeKey.
     */
    override fun action(publicKey: ChallengePublicKey) =
        Observable.fromCallable { storeKey(publicKey) }.toVoid()

    private fun storeKey(publicKey: ChallengePublicKey) {
        keysRepository.storePublicChallengeKey(publicKey, ChallengeType.RECOVERY_CODE)
    }
}