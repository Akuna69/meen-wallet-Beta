package io.meen.apollo.presentation.ui.recovery_code

import io.meen.apollo.domain.model.user.User
import io.meen.apollo.presentation.ui.base.SingleFragmentView

internal interface SetupRecoveryCodeView : SingleFragmentView {

    fun setUser(user: User)

    fun showAbortDialog()

    fun handleStartRecoveryCodeSetupConnectionError()

    fun handleFinishRecoveryCodeSetupConnectionError()
}