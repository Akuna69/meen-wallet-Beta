package io.meen.apollo.domain.action.address

import io.meen.apollo.data.preferences.KeysRepository
import io.meen.apollo.domain.action.base.BaseAsyncAction0
import io.meen.apollo.domain.libwallet.LibwalletBridge
import io.meen.apollo.domain.model.MeenAddressGroup
import io.meen.common.Rules
import io.meen.common.crypto.hd.Schema
import io.meen.common.utils.Preconditions
import io.meen.common.utils.RandomGenerator
import org.bitcoinj.core.NetworkParameters
import rx.Observable
import javax.inject.Inject


class CreateAddressAction @Inject constructor(
    private val keysRepository: KeysRepository,
    private val networkParameters: NetworkParameters,
    private val syncExternalAddressIndexes: SyncExternalAddressIndexesAction,
) : BaseAsyncAction0<MeenAddressGroup>() {

    /**
     * Sync the external address indexes with Houston.
     */
    override fun action(): Observable<MeenAddressGroup> =
        Observable.defer {
            val addresses = createMeenAddressGroup()
            syncExternalAddressIndexes.run() // we don't wait

            Observable.just(addresses)
        }

    private fun createMeenAddressGroup(): MeenAddressGroup {
        val maxUsedIndex = keysRepository.maxUsedExternalAddressIndex
        val maxWatchingIndex = keysRepository.maxWatchingExternalAddressIndex

        Preconditions.checkState(maxUsedIndex == null || maxWatchingIndex != null)
        Preconditions.checkState(maxUsedIndex == null || maxUsedIndex <= maxWatchingIndex)

        val nextIndex =
            if (maxUsedIndex == null) {
                0
            } else if (maxUsedIndex < maxWatchingIndex) {
                maxUsedIndex + 1
            } else {
                val minUsable = maxWatchingIndex - Rules.EXTERNAL_ADDRESSES_WATCH_WINDOW_SIZE
                RandomGenerator.getInt(minUsable, maxWatchingIndex + 1)
            }

        // FIXME: if the nextIndex derived key is invalid (highly improbable),
        // childPublicKey.getLastLevelIndex() will be greater than maxWatchingIndex, which is a bug:
        // it will violate the second precondition.
        val derivedPublicKeyPair = keysRepository
            .basePublicKeyPair
            .deriveFromAbsolutePath(Schema.getExternalKeyPath())
            .deriveNextValidChild(nextIndex)

        if (maxUsedIndex == null || derivedPublicKeyPair.lastLevelIndex > maxUsedIndex) {
            keysRepository.maxUsedExternalAddressIndex = derivedPublicKeyPair.lastLevelIndex
        }

        return MeenAddressGroup(
            LibwalletBridge.createAddressV3(derivedPublicKeyPair, networkParameters),
            LibwalletBridge.createAddressV4(derivedPublicKeyPair, networkParameters),
            LibwalletBridge.createAddressV5(derivedPublicKeyPair, networkParameters)
        )
    }
}