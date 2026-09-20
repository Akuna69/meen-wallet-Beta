package io.meen.apollo.presentation.ui.fragments.rc_only_login

import io.meen.apollo.presentation.ui.settings.RecoveryCodeView

interface RcOnlyLoginView : RecoveryCodeView {

    fun handleLegacyRecoveryCodeError()

}
