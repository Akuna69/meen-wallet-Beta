package io.meen.apollo.domain.action.session.rc_only

import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.domain.action.base.BaseAsyncAction1
import io.meen.apollo.domain.action.keys.DecryptAndStoreKeySetAction
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinishLoginWithRcAction @Inject constructor(
    private val houstonClient: HoustonClient,
    private val decryptAndStoreKeySet: DecryptAndStoreKeySetAction
) : BaseAsyncAction1<String, Void>() {

    override fun action(recoveryCode: String): Observable<Void> =
        Observable.defer { fetchDecryptAndStoreKeySet(recoveryCode) }

    private fun fetchDecryptAndStoreKeySet(recoveryCode: String) =
        houstonClient.fetchKeyset()
            .flatMap { keyset ->
                decryptAndStoreKeySet.action(keyset, recoveryCode)
            }
}