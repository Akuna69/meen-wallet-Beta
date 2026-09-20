package io.meen.apollo.presentation.ui.fragments.create_password

import android.text.TextUtils
import androidx.annotation.VisibleForTesting
import io.meen.apollo.domain.analytics.AnalyticsEvent
import io.meen.apollo.domain.analytics.AnalyticsEvent.PASSWORD_ERROR
import io.meen.apollo.domain.errors.EmptyFieldError
import io.meen.apollo.domain.errors.passwd.PasswordTooShortError
import io.meen.apollo.domain.errors.passwd.PasswordsDontMatchError
import io.meen.apollo.presentation.ui.base.SingleFragmentPresenter
import io.meen.apollo.presentation.ui.base.di.PerFragment
import io.meen.common.Rules
import javax.inject.Inject


@PerFragment
open class CreatePasswordPresenter @Inject constructor():
    SingleFragmentPresenter<CreatePasswordView, CreatePasswordParentPresenter>() {

    fun submitPassword(password: String, confirmPassword: String) {
        view.setPasswordError(null)

        when {

            password == "" -> {
                view.setPasswordError(EmptyFieldError(EmptyFieldError.Field.PASSWORD))

            }

            password.length < Rules.PASSWORD_MIN_LENGTH -> {
                view.setPasswordError(PasswordTooShortError())
            }

            password != confirmPassword -> {
                reportPasswordDidNotMatch()
                view.setConfirmPasswordError(PasswordsDontMatchError())
            }

            else -> {
                parentPresenter.submitPassword(password)
            }
        }
    }

    fun isValidPassword(password: String) =
        !TextUtils.isEmpty(password) && password.length >= Rules.PASSWORD_MIN_LENGTH

    @VisibleForTesting
    open fun reportPasswordDidNotMatch() {
        analytics.report(AnalyticsEvent.E_PASSWORD(PASSWORD_ERROR.DID_NOT_MATCH))
    }

    fun goBack() {
        parentPresenter.cancelCreatePassword()
    }

    override fun getEntryEvent() =
        AnalyticsEvent.S_SIGN_UP_PASSWORD()
}