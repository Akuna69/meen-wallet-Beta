package io.meen.apollo.presentation.ui.recovery_code.show

import io.meen.apollo.domain.libwallet.RecoveryCodeV2
import io.meen.apollo.presentation.ui.base.SingleFragmentView

interface ShowRecoveryCodeView : SingleFragmentView {

    fun setRecoveryCode(recoveryCode: RecoveryCodeV2)
}