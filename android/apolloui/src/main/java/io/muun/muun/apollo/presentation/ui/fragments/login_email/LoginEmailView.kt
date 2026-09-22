package io.meen.apollo.presentation.ui.fragments.login_email

import io.meen.apollo.domain.errors.UserFacingError
import io.meen.apollo.presentation.ui.base.BaseView


interface LoginEmailView: BaseView {
    fun setLoading(isLoading: Boolean)

    fun setEmailError(error: UserFacingError?)

    fun autoFillEmail(email: String)
}