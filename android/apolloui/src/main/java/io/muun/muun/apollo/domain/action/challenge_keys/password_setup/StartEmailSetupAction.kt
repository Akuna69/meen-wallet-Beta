package io.meen.apollo.domain.action.challenge_keys.password_setup

import io.meen.apollo.data.logging.Crashlytics
import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.preferences.UserRepository
import io.meen.apollo.domain.action.base.BaseAsyncAction1
import io.meen.apollo.domain.action.challenge_keys.SignChallengeAction
import io.meen.common.Optional
import io.meen.common.crypto.ChallengeType
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartEmailSetupAction @Inject constructor(
    private val houstonClient: HoustonClient,
    private val userRepository: UserRepository,
    private val signChallengeAction: SignChallengeAction

): BaseAsyncAction1<String, Void>() {

    /**
     * Begin the email setup process, by signing a challenge and requesting a verification email.
     */
    override fun action(email: String): Observable<Void> =
        Observable.defer {
            houstonClient.requestChallenge(ChallengeType.USER_KEY)
                .flatMap { maybeChallenge ->
                    val challenge = maybeChallenge.orElseThrow() // empty only for legacy apps
                    val challengeSignature = signChallengeAction.signWithUserKey(challenge)

                    houstonClient.startEmailSetup(email, challengeSignature)
                }
                .doOnNext {
                    val user = userRepository.fetchOne()
                    user.email = Optional.of(email)
                    user.isEmailVerified = false

                    Crashlytics.configure(user.hid.toString())

                    userRepository.store(user)
                }
        }
}
