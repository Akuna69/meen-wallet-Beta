package io.meen.apollo.domain.action.incoming_swap

import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.preferences.KeysRepository
import io.meen.apollo.domain.libwallet.errors.UnfulfillableIncomingSwapError
import io.meen.apollo.domain.model.IncomingSwap
import org.bitcoinj.core.NetworkParameters
import rx.Completable
import timber.log.Timber
import javax.inject.Inject

class VerifyFulfillableAction @Inject constructor(
    private val keysRepository: KeysRepository,
    private val houstonClient: HoustonClient,
    private val networkParameters: NetworkParameters,
) {
    fun action(swap: IncomingSwap): Completable {

        return keysRepository.basePrivateKey
            .flatMapCompletable { userKey ->

                try {
                    swap.verifyFulfillable(userKey, networkParameters)
                } catch (e: UnfulfillableIncomingSwapError) {
                    Timber.e(
                        "Will expire invoice ${swap.getPaymentHash()} due to unfulfillable swap",
                        e
                    )

                    return@flatMapCompletable houstonClient.expireInvoice(swap.getPaymentHash())
                }

                Completable.complete()

            }.toCompletable()
    }
}