package io.meen.apollo.presentation.ui.fragments.recommended_fee

import io.meen.apollo.domain.model.BitcoinUnit
import io.meen.apollo.presentation.ui.base.BaseView
import newop.EditFeeState

interface RecommendedFeeView : BaseView {

    fun setBitcoinUnit(bitcoinUnit: BitcoinUnit)

    fun setState(state: EditFeeState)
}