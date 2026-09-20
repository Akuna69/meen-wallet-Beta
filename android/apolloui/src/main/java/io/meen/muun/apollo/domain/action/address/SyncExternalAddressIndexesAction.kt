package io.meen.apollo.domain.action.address

import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.preferences.KeysRepository
import io.meen.apollo.domain.action.base.BaseAsyncAction0
import io.meen.apollo.domain.utils.toVoid
import io.meen.common.api.ExternalAddressesRecord
import io.meen.common.utils.MathUtils.maxWithNulls
import rx.Observable
import javax.inject.Inject


open class SyncExternalAddressIndexesAction @Inject constructor(
    val houstonClient: HoustonClient,
    val keysRepository: KeysRepository
): BaseAsyncAction0<Void>() {

    /**
     * Sync the external address indexes with Houston.
     */
    override fun action(): Observable<Void> =
        Observable.defer {
            updateOrFetchRecord().map(this::storeRecord).toVoid()
        }

    private fun updateOrFetchRecord() =
        keysRepository.maxUsedExternalAddressIndex.let {
            if (it != null)
                houstonClient.updateExternalAddressesRecord(it)
            else
                houstonClient.fetchExternalAddressesRecord()
        }

    private fun storeRecord(record: ExternalAddressesRecord) {
        keysRepository.maxUsedExternalAddressIndex =
            maxWithNulls(record.maxUsedIndex, keysRepository.maxUsedExternalAddressIndex)

        keysRepository.maxWatchingExternalAddressIndex =
            maxWithNulls(record.maxWatchingIndex, keysRepository.maxWatchingExternalAddressIndex)
    }
}