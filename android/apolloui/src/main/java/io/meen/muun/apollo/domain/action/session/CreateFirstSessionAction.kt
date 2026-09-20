package io.meen.apollo.domain.action.session

import io.meen.apollo.data.afs.MetricsProvider
import io.meen.apollo.data.logging.Crashlytics
import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.preferences.FirebaseInstallationIdRepository
import io.meen.apollo.data.preferences.KeysRepository
import io.meen.apollo.data.preferences.PlayIntegrityNonceRepository
import io.meen.apollo.data.preferences.UserRepository
import io.meen.apollo.domain.action.CurrencyActions
import io.meen.apollo.domain.action.LogoutActions
import io.meen.apollo.domain.action.base.BaseAsyncAction0
import io.meen.apollo.domain.action.fcm.GetFcmTokenAction
import io.meen.apollo.domain.action.keys.CreateBasePrivateKeyAction
import io.meen.apollo.domain.model.CreateFirstSessionOk
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateFirstSessionAction @Inject constructor(
    private val currencyActions: CurrencyActions,
    private val logoutActions: LogoutActions,
    private val getFcmToken: GetFcmTokenAction,
    private val createBasePrivateKey: CreateBasePrivateKeyAction,
    private val metricsProvider: MetricsProvider,
    private val userRepo: UserRepository,
    private val keysRepo: KeysRepository,
    private val firebaseInstallationIdRepo: FirebaseInstallationIdRepository,
    private val playIntegrityNonceRepo: PlayIntegrityNonceRepository,
    private val houstonClient: HoustonClient,
) : BaseAsyncAction0<CreateFirstSessionOk>() {

    /**
     * Creates a new user with their first session in Houston.
     */
    override fun action(): Observable<CreateFirstSessionOk> =
        Observable.defer {
            logoutActions.destroyWalletToStartClean()
            getFcmToken.action().flatMap { setUpUser(it) }
        }

    /**
     * Signs up a user.
     */
    private fun setUpUser(gcmToken: String): Observable<CreateFirstSessionOk> {

        return createBasePrivateKey.action()
            .flatMap { basePrivateKey ->
                houstonClient
                    .createFirstSession(
                        gcmToken,
                        basePrivateKey.publicKey,
                        currencyActions.localCurrencies.iterator().next(),
                        firebaseInstallationIdRepo.getBigQueryPseudoId(),
                        metricsProvider.isRootHint
                    )
            }
            .doOnNext {
                userRepo.store(it.user)
                keysRepo.storeBaseMuunPublicKey(it.cosigningPublicKey)
                keysRepo.storeSwapServerPublicKey(it.swapServerPublicKey)

                Crashlytics.configure(it.user.hid.toString())

                playIntegrityNonceRepo.store(it.playIntegrityNonce)
            }
    }
}
