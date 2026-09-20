package io.meen.apollo.domain.action.challenge_keys.password_setup

import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.preferences.KeysRepository
import io.meen.apollo.data.preferences.UserPreferencesRepository
import io.meen.apollo.domain.action.base.BaseAsyncAction1
import io.meen.apollo.domain.action.challenge_keys.CreateChallengeSetupAction
import io.meen.apollo.domain.action.challenge_keys.SignChallengeAction
import io.meen.apollo.domain.action.challenge_keys.StoreVerifiedChallengeKeyAction
import io.meen.apollo.domain.utils.flatDoOnNext
import io.meen.apollo.domain.utils.toVoid
import io.meen.common.crypto.ChallengeType
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetUpPasswordAction @Inject constructor(
    private val houstonClient: HoustonClient,
    private val createChallengeSetup: CreateChallengeSetupAction,
    private val storeChallengeKey: StoreVerifiedChallengeKeyAction,
    private val signChallengeAction: SignChallengeAction,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseAsyncAction1<String, Void>() {

    /**
     * Finish the password + email setup process. Sign a challenge and create a new ChallengeSetup
     * of type PASSWORD. Store the ChallengePublicKey returned as a result.
     */
    override fun action(password: String): Observable<Void> =
        houstonClient.requestChallenge(ChallengeType.USER_KEY)
            .flatMap { maybeChallenge ->

                val challenge = maybeChallenge.orElseThrow() // empty only for legacy apps

                val challengeSignature = signChallengeAction.signWithUserKey(challenge)

                createChallengeSetup.action(ChallengeType.PASSWORD, password)
                    .flatMap { chSetup ->
                        houstonClient
                            .setUpPassword(challengeSignature, chSetup)
                            .flatDoOnNext {
                                storeChallengeKey.action(chSetup.type, chSetup.publicKey)
                            }
                            .map {
                                userPreferencesRepository.updateSkippedEmail(false)
                            }
                            .toVoid()
                    }
            }
}
