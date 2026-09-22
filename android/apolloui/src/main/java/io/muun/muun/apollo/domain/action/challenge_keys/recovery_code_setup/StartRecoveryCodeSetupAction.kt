package io.meen.apollo.domain.action.challenge_keys.recovery_code_setup

import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.preferences.KeysRepository
import io.meen.apollo.domain.action.base.BaseAsyncAction1
import io.meen.apollo.domain.action.challenge_keys.CreateChallengeSetupAction
import io.meen.apollo.domain.action.challenge_keys.StoreUnverifiedRcChallengeKeyAction
import io.meen.apollo.domain.errors.rc.StartRecoveryCodeSetupError
import io.meen.apollo.domain.libwallet.RecoveryCodeV2
import io.meen.apollo.domain.utils.toVoid
import io.meen.common.api.SetupChallengeResponse
import io.meen.common.crypto.ChallengeType
import io.meen.common.model.challenge.ChallengeSetup
import io.meen.common.utils.Preconditions
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartRecoveryCodeSetupAction @Inject constructor(
    private val houstonClient: HoustonClient,
    private val createChallengeSetup: CreateChallengeSetupAction,
    private val storeUnverifiedRcChallengeKey: StoreUnverifiedRcChallengeKeyAction,
    private val keysRepository: KeysRepository,
) : BaseAsyncAction1<RecoveryCodeV2, Void>() {

    /**
     * Start the 2-step setup process of a RECOVERY CODE Challenge Key in Houston.
     */
    override fun action(recoveryCode: RecoveryCodeV2): Observable<Void> {
        return startRecoveryCodeSetup(recoveryCode.toString())
            .doOnNext { setupChallengeResponse ->
                // MeenEncryptedKey is always returned after a successful RC challenge setup
                Preconditions.checkNotNull(setupChallengeResponse.meenKey)
                Preconditions.checkNotNull(setupChallengeResponse.meenKeyFingerprint)

                keysRepository.storeEncryptedMeenPrivateKey(setupChallengeResponse.meenKey!!)
                keysRepository.storeMeenKeyFingerprint(setupChallengeResponse.meenKeyFingerprint!!)
            }
            .onErrorResumeNext { error ->
                Observable.error(StartRecoveryCodeSetupError(error))
            }
            .toVoid()
    }

    private fun startRecoveryCodeSetup(recoveryCode: String): Observable<SetupChallengeResponse> {
        return createChallengeSetup.action(ChallengeType.RECOVERY_CODE, recoveryCode)
            .flatMap { chSetup: ChallengeSetup ->
                houstonClient.startChallengeSetup(chSetup)
                    .flatMap { setupChallengeResponse ->
                        storeUnverifiedRcChallengeKey.action(chSetup.publicKey)
                            .map { setupChallengeResponse }
                    }
            }
    }
}