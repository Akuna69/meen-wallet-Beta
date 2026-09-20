package io.meen.apollo.domain.action.user

import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.preferences.BiometricsRepository
import io.meen.apollo.domain.action.LogoutActions
import io.meen.apollo.domain.action.base.BaseAsyncAction0
import io.meen.apollo.domain.action.challenge_keys.SignChallengeAction
import io.meen.apollo.domain.selector.UserSelector
import io.meen.common.Optional
import io.meen.common.crypto.ChallengeType
import io.meen.common.model.challenge.Challenge
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteWalletAction @Inject constructor(
    private val userSel: UserSelector,
    private val logoutActions: LogoutActions,
    private val signChallenge: SignChallengeAction,
    private val houstonClient: HoustonClient,
    private val biometricsRepository: BiometricsRepository,
) : BaseAsyncAction0<Optional<String>>() {

    override fun action(): Observable<Optional<String>> {
        return Observable.defer {
            houstonClient.requestChallenge(ChallengeType.USER_KEY)
                .map { maybeChallenge: Optional<Challenge> ->
                    val challenge = maybeChallenge.orElseThrow() // empty only for legacy apps
                    signChallenge.signWithUserKey(challenge)
                }
                .flatMap { challengeSignature ->
                    houstonClient.deleteWallet(challengeSignature)
                        .andThen(Observable.just(null))
                }
                .map {
                    val maybeSupportId = userSel.getOptional().flatMap { it.supportId }
                    logoutActions.dangerouslyDestroyWallet()
                    biometricsRepository.deleteUserOptInBiometrics()
                    maybeSupportId
                }
        }
    }
}