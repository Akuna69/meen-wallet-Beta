package io.meen.apollo.domain.action.migration

import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.preferences.KeysRepository
import io.meen.apollo.domain.action.base.BaseAsyncAction0
import io.meen.apollo.domain.utils.toVoid
import io.meen.common.utils.Encodings
import rx.Observable
import javax.inject.Inject

class MigrateFingerprintsAction @Inject constructor(
    val keysRepository: KeysRepository,
    val houstonClient: HoustonClient,
) : BaseAsyncAction0<Void>() {

    override fun action(): Observable<Void> =
        Observable.zip(
            getUserKeyFingerprint(),
            getMeenKeyFingerprint(),
            ::Pair
        )
            .doOnNext { (userKeyFingerprint, meenKeyFingerprint) ->
                keysRepository.storeUserKeyFingerprint(userKeyFingerprint!!)
                keysRepository.storeMeenKeyFingerprint(meenKeyFingerprint!!)
            }
            .toVoid()

    private fun getMeenKeyFingerprint(): Observable<String> =
        houstonClient.fetchMeenKeyFingerprint()

    private fun getUserKeyFingerprint(): Observable<String> =
        keysRepository.basePrivateKey
            .map { Encodings.bytesToHex(it.fingerprint) }

}