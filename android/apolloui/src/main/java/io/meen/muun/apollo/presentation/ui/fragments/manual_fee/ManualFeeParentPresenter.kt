package io.meen.apollo.presentation.ui.fragments.manual_fee

import io.meen.apollo.presentation.ui.base.ParentPresenter
import newop.EditFeeState
import rx.Observable

interface ManualFeeParentPresenter : ParentPresenter {

    fun watchEditFeeState(): Observable<EditFeeState>

    fun confirmFee(selectedFeeRateInVBytes: Double)
}