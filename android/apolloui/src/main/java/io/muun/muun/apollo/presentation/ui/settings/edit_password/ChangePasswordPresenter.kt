package io.meen.apollo.presentation.ui.settings.edit_password

import android.os.Bundle
import io.meen.apollo.domain.action.base.ActionState
import io.meen.apollo.domain.action.challenge_keys.password_change.FinishPasswordChangeAction
import io.meen.apollo.domain.analytics.AnalyticsEvent
import io.meen.apollo.domain.analytics.AnalyticsEvent.E_PASSWORD_CHANGED
import io.meen.apollo.domain.analytics.AnalyticsEvent.S_PASSWORD_CHANGE_END
import io.meen.apollo.domain.errors.EmptyFieldError
import io.meen.apollo.domain.errors.passwd.PasswordTooShortError
import io.meen.apollo.domain.errors.passwd.PasswordsDontMatchError
import io.meen.apollo.presentation.ui.base.di.PerFragment
import io.meen.common.Rules
import javax.inject.Inject

@PerFragment
class ChangePasswordPresenter @Inject constructor(
    private val finishPasswordChange: FinishPasswordChangeAction,
) : BaseEditPasswordPresenter<ChangePasswordView>() {

    override fun setUp(arguments: Bundle) {
        super.setUp(arguments)
        setUpFinishPasswordChangeAction()
    }

    private fun setUpFinishPasswordChangeAction() {
        val observable = finishPasswordChange
            .state
            .doOnNext { state: ActionState<Void?> ->
                when (state.kind) {
                    ActionState.Kind.VALUE -> {
                        analytics.report(E_PASSWORD_CHANGED())
                        parentPresenter.onChangeSuccessful()
                    }
                    ActionState.Kind.ERROR -> {
                        view.setLoading(false)
                        handleError(state.error)
                    }
                    else -> {
                        // Do nothing
                    }
                }
            }
        subscribeTo(observable)
    }

    /**
     * Submit the new password, checking for errors.
     */
    fun submitPassword(password: String, confirmPassword: String) {
        view.setPasswordError(null)

        if (password == "") {
            view.setPasswordError(EmptyFieldError(EmptyFieldError.Field.PASSWORD))

        } else if (password.length < Rules.PASSWORD_MIN_LENGTH) {
            view.setPasswordError(PasswordTooShortError())

        } else if (password != confirmPassword) {
            view.setConfirmPasswordError(PasswordsDontMatchError())

        } else {
            view.setPasswordError(null)
            view.setConfirmPasswordError(null)
            view.setLoading(true)
            finishPasswordChange.run(parentPresenter.currentUuid, password)
        }
    }

    override fun getEntryEvent(): AnalyticsEvent {
        return S_PASSWORD_CHANGE_END()
    }
}