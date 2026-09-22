package io.meen.apollo.presentation.ui.select_bitcoin_unit

import io.meen.apollo.domain.model.BitcoinUnit
import io.meen.apollo.presentation.ui.base.BaseView

interface SelectBitcoinUnitView: BaseView {

    fun setBitcoinUnit(bitcoinUnit: BitcoinUnit)

}