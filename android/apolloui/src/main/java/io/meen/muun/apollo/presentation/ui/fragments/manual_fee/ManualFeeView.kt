package io.meen.apollo.presentation.ui.fragments.manual_fee

import io.meen.apollo.presentation.ui.base.BaseView
import newop.EditFeeState

interface ManualFeeView : BaseView {

    fun setState(state: EditFeeState)
}