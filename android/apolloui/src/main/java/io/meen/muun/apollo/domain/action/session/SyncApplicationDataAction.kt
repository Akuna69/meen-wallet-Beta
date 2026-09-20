package io.meen.apollo.domain.action.session

import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.preferences.UserPreferencesRepository
import io.meen.apollo.data.preferences.UserRepository
import io.meen.apollo.domain.ApiMigrationsManager
import io.meen.apollo.domain.LoggingContextManager
import io.meen.apollo.domain.action.ContactActions
import io.meen.apollo.domain.action.OperationActions
import io.meen.apollo.domain.action.base.BaseAsyncAction3
import io.meen.apollo.domain.action.incoming_swap.RegisterInvoicesAction
import io.meen.apollo.domain.action.integrity.GooglePlayIntegrityCheckAction
import io.meen.apollo.domain.action.keys.SyncPublicKeySetAction
import io.meen.apollo.domain.action.operation.FetchNextTransactionSizeAction
import io.meen.apollo.domain.action.realtime.FetchRealTimeDataAction
import io.meen.apollo.domain.action.realtime.PreloadFeeDataAction
import io.meen.apollo.domain.action.session.rc_only.FinishLoginWithRcAction
import io.meen.apollo.domain.errors.InitialSyncError
import io.meen.apollo.domain.errors.InitialSyncNetworkError
import io.meen.apollo.domain.errors.fcm.GooglePlayServicesNotAvailableError
import io.meen.apollo.domain.model.feebump.FeeBumpRefreshPolicy
import io.meen.apollo.domain.model.LoginWithRc
import io.meen.apollo.domain.utils.isInstanceOrIsCausedByNetworkError
import io.meen.apollo.domain.utils.toVoid
import io.meen.common.rx.RxHelper
import rx.Observable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SyncApplicationDataAction @Inject constructor(
    private val houstonClient: HoustonClient,
    private val userRepository: UserRepository,
    private val contactActions: ContactActions,
    private val operationActions: OperationActions,
    private val loggingContextManager: LoggingContextManager,
    private val syncPublicKeySet: SyncPublicKeySetAction,
    private val fetchNextTransactionSize: FetchNextTransactionSizeAction,
    private val fetchRealTimeData: FetchRealTimeDataAction,
    private val preloadFeeDataAction: PreloadFeeDataAction,
    private val createFirstSession: CreateFirstSessionAction,
    private val finishLoginWithRc: FinishLoginWithRcAction,
    private val registerInvoices: RegisterInvoicesAction,
    private val googlePlayIntegrityCheck: GooglePlayIntegrityCheckAction,
    private val apiMigrationsManager: ApiMigrationsManager,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseAsyncAction3<Boolean, Boolean, LoginWithRc?, Void>() {

    override fun action(
        isFirstSession: Boolean,
        hasContactsPermission: Boolean,
        loginWithRc: LoginWithRc?,
    ): Observable<Void> =
        Observable.defer { syncApplicationData(isFirstSession, hasContactsPermission, loginWithRc) }

    /**
     * Synchronize Apollo with Houston.
     */
    private fun syncApplicationData(
        isFirstSession: Boolean,
        hasContactsPermission: Boolean,
        loginWithRc: LoginWithRc?,
    ): Observable<Void> {

        // Before anything else, new (unrecoverable) users need a session:
        val step0 = if (isFirstSession) {
            createFirstSession.action()

        } else if (loginWithRc != null && loginWithRc.keysetFetchNeeded) {
            finishLoginWithRc.action(loginWithRc.rc)

        } else {
            Observable.just(null)
        }

        // We need this before others so that compat users can upgrade to multisig setup
        val step1 = syncPublicKeySet.action()

        // These can run in any order:
        val step2 = Observable.mergeDelayError(
            fetchUserInfo(),
            fetchNextTransactionSize.action(),
            fetchRealTimeData.action(),
            preloadFeeDataAction.action(FeeBumpRefreshPolicy.FOREGROUND),
            runOnlyIf(!isFirstSession) { syncContacts(hasContactsPermission) },
            Observable.fromCallable(apiMigrationsManager::reset),
        )

        // These must run after the ones before:
        val step3 = Observable.zip(
            runOnlyIf(!isFirstSession) { operationActions.fetchReplaceOperations() },
            registerInvoices.action(),
            googlePlayIntegrityCheck.run(),
            RxHelper::toVoid
        )

        return Observable.concat(step0, step1, step2, step3)
            .lastOrDefault(null)
            .onErrorResumeNext { throwable ->
                Timber.e(throwable)
                if (throwable is GooglePlayServicesNotAvailableError) {
                    Observable.error(throwable)
                } else if (throwable.isInstanceOrIsCausedByNetworkError()) {
                    Observable.error(InitialSyncNetworkError(throwable))
                } else {
                    Observable.error(InitialSyncError(throwable))
                }
            }
            .doOnNext { userRepository.storeInitialSyncCompleted() }
            .toVoid()
    }

    private fun fetchUserInfo(): Observable<Void> =
        houstonClient.fetchUser()
            .doOnNext {
                userRepository.store(it.fst)
                userPreferencesRepository.update(it.snd)
                loggingContextManager.setupCrashlytics()
            }
            .toVoid()

    private fun syncContacts(hasContactsPermission: Boolean): Observable<Void> {
        return if (hasContactsPermission) {
            // Sync phone contacts sending PATCH, then fetch full list:
            contactActions.syncPhoneContacts()
                .flatMap { contactActions.fetchReplaceContacts() }

        } else {
            // Just fetch previous contacts, we can't PATCH with local changes:
            contactActions.fetchReplaceContacts()
        }
    }

    private fun <T> runOnlyIf(condition: Boolean, block: () -> Observable<T>): Observable<T> {
        return if (condition) {
            block()
        } else {
            Observable.just(null)
        }
    }
}
