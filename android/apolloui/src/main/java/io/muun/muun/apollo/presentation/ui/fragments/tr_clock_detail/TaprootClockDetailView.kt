package io.meen.apollo.presentation.ui.fragments.tr_clock_detail

import io.meen.apollo.presentation.ui.base.BaseView

interface TaprootClockDetailView: BaseView {

    fun setTaprootCounter(blocksRemaining: Int)

}