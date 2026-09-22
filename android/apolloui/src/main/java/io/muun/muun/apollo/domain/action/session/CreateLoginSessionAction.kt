package io.meen.apollo.domain.action.session

import io.meen.apollo.data.afs.MetricsProvider
import io.meen.apollo.data.logging.Crashlytics
import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.preferences.FirebaseInstallationIdRepository
import io.meen.apollo.data.preferences.PlayIntegrityNonceRepository
import io.meen.apollo.domain.action.LogoutActions
import io.meen.apollo.domain.action.base.BaseAsyncAction1
import io.meen.apollo.domain.action.fcm.GetFcmTokenAction
import io.meen.apollo.domain.model.CreateSessionOk
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateLoginSessionAction @Inject constructor(
    private val houstonClient: HoustonClient,
    private val getFcmToken: GetFcmTokenAction,
    private val logoutActions: LogoutActions,
    private val metricsProvider: MetricsProvider,
    private val firebaseInstallationIdRepo: FirebaseInstallationIdRepository,
    private val playIntegrityNonceRepo: PlayIntegrityNonceRepository,
) : BaseAsyncAction1<String, CreateSessionOk>() {

    override fun action(email: String): Observable<CreateSessionOk> =
        Observable.defer { createSession(email) }

    /**
     * Creates a new session to log into Houston, associated with a given email.
     */
    private fun createSession(email: String): Observable<CreateSessionOk> {

        logoutActions.destroyWalletToStartClean()

        return getFcmToken.action()
            .flatMap { fcmToken ->
                houstonClient.createLoginSession(
                    fcmToken,
                    email,
                    firebaseInstallationIdRepo.getBigQueryPseudoId(),
                    metricsProvider.isRootHint
                )
            }
            .doOnNext {
                Crashlytics.configure("NotLoggedYet")
                playIntegrityNonceRepo.store(it.playIntegrityNonce)
            }
    }
}
