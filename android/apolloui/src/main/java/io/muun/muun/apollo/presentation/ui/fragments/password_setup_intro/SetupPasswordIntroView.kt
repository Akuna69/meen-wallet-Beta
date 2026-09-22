package io.meen.apollo.presentation.ui.fragments.password_setup_intro

import io.meen.apollo.domain.model.SecurityLevel
import io.meen.apollo.presentation.ui.base.BaseView

interface SetupPasswordIntroView : BaseView {

    fun setSecurityLevel(securityLevel: SecurityLevel)
}
