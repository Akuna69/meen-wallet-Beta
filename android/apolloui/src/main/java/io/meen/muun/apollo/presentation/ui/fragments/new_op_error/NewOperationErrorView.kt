package io.meen.apollo.presentation.ui.fragments.new_op_error

import io.meen.apollo.presentation.ui.base.BaseView
import io.meen.apollo.presentation.ui.new_operation.NewOperationErrorType
import io.meen.apollo.domain.model.BitcoinUnit
import io.meen.apollo.domain.model.PaymentContext
import io.meen.apollo.domain.model.PaymentRequest
import io.meen.common.Optional
import newop.BalanceErrorState
import newop.ErrorState

interface NewOperationErrorView : BaseView {

    companion object {
        const val ARG_ERROR_TYPE = "error_type"
    }

    fun setErrorType(errorType: NewOperationErrorType, errorState: ErrorState?)

    fun setBitcoinUnit(bitcoinUnit: BitcoinUnit)

    fun setBalanceErrorState(state: BalanceErrorState)
}