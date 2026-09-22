package io.meen.apollo.presentation.ui.fragments.home

import io.meen.apollo.domain.model.BitcoinUnit
import io.meen.apollo.domain.model.Operation
import io.meen.apollo.presentation.ui.base.BaseView

interface HomeFragmentView : BaseView {

    fun setState(homeState: HomeFragmentPresenter.HomeState)

    fun setNewOp(newOp: Operation, bitcoinUnit: BitcoinUnit)

    fun showTooltip()
}