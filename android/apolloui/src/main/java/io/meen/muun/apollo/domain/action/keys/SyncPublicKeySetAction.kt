package io.meen.apollo.domain.action.keys

import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.preferences.KeysRepository
import io.meen.apollo.domain.action.base.BaseAsyncAction0
import io.meen.apollo.domain.utils.toVoid
import io.meen.common.utils.MathUtils.maxWithNulls
import rx.Observable
import javax.inject.Inject


class SyncPublicKeySetAction @Inject constructor(
    val houstonClient: HoustonClient,
    val keysRepository: KeysRepository
): BaseAsyncAction0<Void>() {

    /**
     * Sync the external address indexes with Houston.
     */
    override fun action(): Observable<Void> =
        Observable.defer {
            houstonClient
                .updatePublicKeySet(keysRepository.basePublicKey)
                .doOnNext {
                    keysRepository.storeBaseMeenPublicKey(it.basePublicKeyTriple.meenPublicKey)
                    keysRepository.storeSwapServerPublicKey(it.basePublicKeyTriple.swapServerPublicKey)
                    storeExternalIndexes(it.externalMaxUsedIndex, it.externalMaxWatchingIndex)
                }
                .toVoid()
        }

    private fun storeExternalIndexes(maxUsedIndex: Int?, maxWatchingIndex: Int?) {
        keysRepository.maxUsedExternalAddressIndex =
            maxWithNulls(maxUsedIndex, keysRepository.maxUsedExternalAddressIndex)

        keysRepository.maxWatchingExternalAddressIndex =
            maxWithNulls(maxWatchingIndex, keysRepository.maxWatchingExternalAddressIndex)
    }
}