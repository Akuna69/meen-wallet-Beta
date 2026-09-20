package io.meen.apollo.domain.action.session.rc_only

import io.meen.apollo.data.afs.MetricsProvider
import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.preferences.FirebaseInstallationIdRepository
import io.meen.apollo.data.preferences.PlayIntegrityNonceRepository
import io.meen.apollo.domain.action.base.BaseAsyncAction1
import io.meen.apollo.domain.action.challenge_keys.SignChallengeAction
import io.meen.apollo.domain.action.fcm.GetFcmTokenAction
import io.meen.apollo.domain.action.keys.DecryptAndStoreKeySetAction
import io.meen.apollo.domain.model.CreateSessionRcOk
import io.meen.common.api.KeySet
import io.meen.common.model.challenge.Challenge
import libwallet.Libwallet
import rx.Observable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogInWithRcAction @Inject constructor(
    private val houstonClient: HoustonClient,
    private val getFcmToken: GetFcmTokenAction,
    private val signChallenge: SignChallengeAction,
    private val decryptAndStoreKeySet: DecryptAndStoreKeySetAction,
    private val metricsProvider: MetricsProvider,
    private val firebaseInstallationIdRepo: FirebaseInstallationIdRepository,
    private val playIntegrityNonceRepo: PlayIntegrityNonceRepository,
) : BaseAsyncAction1<String, CreateSessionRcOk>() {

    override fun action(recoveryCode: String): Observable<CreateSessionRcOk> =
        Observable.defer { loginWithRc(recoveryCode) }

    /**
     * Login with Recovery Code.
     * Note: if user has email set, email auth will still be required.
     */
    private fun loginWithRc(recoveryCode: String): Observable<CreateSessionRcOk> {
        return createRcLoginSession(recoveryCode)
            .flatMap { challenge ->
                houstonClient.loginWithRecoveryCode(signChallenge.sign(recoveryCode, challenge))
                    .flatMap { createSessionRcOk ->
                        decryptAndStoreKeySet(createSessionRcOk.keySet, recoveryCode)
                            .map { createSessionRcOk }
                            .doOnNext {
                                playIntegrityNonceRepo.store(it.playIntegrityNonce)
                            }
                    }
            }
    }

    /**
     * Creates a new session to log into Houston, associated with a given Recovery Code.
     */
    private fun createRcLoginSession(recoveryCode: String): Observable<Challenge> {
        val pubKeyHex = Libwallet.recoveryCodeToKey(recoveryCode, null).pubKeyHex()
        val bigQueryPseudoId = firebaseInstallationIdRepo.getBigQueryPseudoId()
        Timber.i("Rc Login: $pubKeyHex")
        Timber.i("Rc Login: $bigQueryPseudoId")
        return getFcmToken.action()
            .flatMap { fcmToken ->
                houstonClient.createRcLoginSession(
                    fcmToken,
                    pubKeyHex,
                    bigQueryPseudoId,
                    metricsProvider.isRootHint
                )
            }
    }

    private fun decryptAndStoreKeySet(maybeKeySet: KeySet?, rc: String): Observable<Void> =
        if (maybeKeySet != null) {
            decryptAndStoreKeySet.action(maybeKeySet, rc)
        } else {
            Observable.just(null)
        }
}

