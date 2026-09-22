package io.meen.apollo.presentation.ui.setup_password

import io.meen.apollo.domain.model.user.User
import io.meen.apollo.presentation.ui.base.BaseView

interface SetupPasswordActivityView : BaseView {

    fun setUser(user: User)

    fun goToStep(step: SetupPasswordStep)

    fun showAbortDialog()

    fun showSkipDialog()

}