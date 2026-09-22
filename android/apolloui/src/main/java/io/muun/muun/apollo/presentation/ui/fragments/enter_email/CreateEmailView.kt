package io.meen.apollo.presentation.ui.fragments.enter_email

import io.meen.apollo.domain.errors.UserFacingError
import io.meen.apollo.presentation.ui.base.BaseView

interface CreateEmailView: BaseView {

    fun setLoading(isLoading: Boolean)

    fun setEmail(email: String?)

    fun setEmailError(error: UserFacingError?)

}