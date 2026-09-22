package io.meen.apollo.presentation.ui.select_amount

import io.meen.apollo.domain.model.BitcoinAmount
import io.meen.apollo.domain.model.BitcoinUnit
import io.meen.apollo.presentation.ui.base.BaseView
import io.meen.common.model.ExchangeRateProvider
import javax.money.CurrencyUnit
import javax.money.MonetaryAmount

interface SelectAmountView : BaseView {

    fun setExchangeRateProvider(exchangeRateProvider: ExchangeRateProvider)

    fun initializeAmountInput(primaryCurrency: CurrencyUnit, bitcoinUnit: BitcoinUnit)

    fun setSecondaryAmount(amount: MonetaryAmount)

    fun hideSecondaryAmount()

    fun setAmountError(showAmountError: Boolean)

    fun finishWithResult(resultCode: Int, amount: BitcoinAmount?)

}