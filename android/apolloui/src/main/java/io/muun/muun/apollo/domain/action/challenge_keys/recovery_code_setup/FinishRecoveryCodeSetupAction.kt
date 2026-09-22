package io.meen.apollo.domain.action.challenge_keys.recovery_code_setup

import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.preferences.KeysRepository
import io.meen.apollo.data.preferences.UserRepository
import io.meen.apollo.domain.action.base.BaseAsyncAction0
import io.meen.apollo.domain.errors.rc.FinishRecoveryCodeSetupError
import io.meen.common.crypto.ChallengePublicKey
import io.meen.common.crypto.ChallengeType
import rx.Observable
import javax.inject.Inject

class FinishRecoveryCodeSetupAction @Inject constructor(
    private val houstonClient: HoustonClient,
    private val keysRepository: KeysRepository,
    private val userRepository: UserRepository,
) : BaseAsyncAction0<ChallengePublicKey>() {

    /**
     * Finish/Verify the setup of a RECOVERY CODE Challenge Key in Houston.
     */
    override fun action(): Observable<ChallengePublicKey> {
        return keysRepository.getChallengePublicKey(ChallengeType.RECOVERY_CODE)
            .flatMap { challengePublicKey ->
                houstonClient.finishChallengeSetup(ChallengeType.RECOVERY_CODE, challengePublicKey)
                    .andThen(Observable.fromCallable {
                        userRepository.setRecoveryCodeSetupInProcess(false)
                        userRepository.setHasRecoveryCode()
                        return@fromCallable challengePublicKey
                    })
            }
            .onErrorResumeNext { error ->
                Observable.error(FinishRecoveryCodeSetupError(error))
            }
    }
}