package io.meen.apollo.domain.action.incoming_swap

import io.meen.apollo.data.preferences.ForwardingPoliciesRepository
import io.meen.apollo.data.preferences.KeysRepository
import io.meen.apollo.domain.action.base.BaseAsyncAction1
import io.meen.apollo.domain.libwallet.Invoice
import io.meen.apollo.domain.libwallet.errors.NoInvoicesLeftError
import io.meen.apollo.domain.utils.onTypedErrorResumeNext
import org.bitcoinj.core.NetworkParameters
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenerateInvoiceAction @Inject constructor(
    private val registerInvoices: RegisterInvoicesAction,
    private val forwardingPoliciesRepository: ForwardingPoliciesRepository,
    private val keysRepository: KeysRepository,
    private val networkParameters: NetworkParameters,
) : BaseAsyncAction1<Long?, String>() {

    override fun action(amountInSat: Long?): Observable<String> {
        return generateInvoice(amountInSat)
            .onTypedErrorResumeNext(NoInvoicesLeftError::class.java) {
                registerInvoices.action()
                    .flatMap { generateInvoice(amountInSat) }
            }
            .doOnCompleted(registerInvoices::run)
    }

    private fun generateInvoice(amountInSat: Long?) = keysRepository.basePrivateKey
        .flatMap { basePrivateKey ->
            Observable.just(
                Invoice.generate(
                    networkParameters,
                    basePrivateKey,
                    forwardingPoliciesRepository.fetchOne(),
                    amountInSat
                )
            )
        }

}