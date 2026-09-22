package io.meen.apollo.domain.selector

import io.meen.apollo.data.preferences.ExchangeRateWindowRepository
import io.meen.apollo.data.preferences.FeeWindowRepository
import io.meen.apollo.data.preferences.MinFeeRateRepository
import io.meen.apollo.data.preferences.TransactionSizeRepository
import io.meen.apollo.domain.model.PaymentContext
import rx.Observable
import javax.inject.Inject


class PaymentContextSelector @Inject constructor(
    private val userSel: UserSelector,
    private val feeWindowRepository: FeeWindowRepository,
    private val exchangeRateWindowRepository: ExchangeRateWindowRepository,
    private val transactionSizeRepository: TransactionSizeRepository,
    private val minFeeRateRepository: MinFeeRateRepository
) {

    fun watch(): Observable<PaymentContext> =
        Observable.combineLatest(
            userSel.watch(),
            exchangeRateWindowRepository.fetch(),
            feeWindowRepository.fetchNonNull(),
            transactionSizeRepository.watchNonNullNts(),
            minFeeRateRepository.fetch(),
            ::PaymentContext
        )
}