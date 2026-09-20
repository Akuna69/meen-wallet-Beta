package io.meen.apollo.presentation.ui.fragments.enter_email

import android.os.Bundle
import io.meen.apollo.domain.analytics.AnalyticsEvent
import io.meen.apollo.domain.errors.passwd.EmailAlreadyUsedError
import io.meen.apollo.presentation.ui.base.SingleFragmentPresenter
import io.meen.apollo.presentation.ui.base.di.PerFragment
import io.meen.common.bitcoinj.ValidationHelpers
import javax.inject.Inject

@PerFragment
open class CreateEmailPresenter @Inject constructor() :
    SingleFragmentPresenter<CreateEmailView, CreateEmailParentPresenter>() {

    override fun setUp(arguments: Bundle) {
        super.setUp(arguments)

        parentPresenter.refreshToolbarTitle()

        setUpSubmitEmail()
        view.setEmail(parentPresenter.getEmail()) // restore previous value if available
    }

    private fun setUpSubmitEmail() {
        parentPresenter
            .watchSubmitEmail()
            .compose(handleStates(view::setLoading, this::handleError))
            .let(this::subscribeTo)
    }

    fun isValidEmail(email: String) =
        ValidationHelpers.isValidEmail(email)

    fun submitEmail(email: String) {
        if (!isValidEmail(email)) {
            return // View shouldn't let this happen.
        }

        parentPresenter.submitEmail(email)
    }

    override fun handleError(error: Throwable) {
        if (error is EmailAlreadyUsedError) {
            analytics.report(AnalyticsEvent.E_EMAIL(AnalyticsEvent.EMAIL_TYPE.ALREADY_USED))
            view.setEmailError(error)

        } else {
            super.handleError(error)
        }
    }

    override fun getEntryEvent() =
        AnalyticsEvent.S_SIGN_UP_EMAIL()

    fun goBack() {
        parentPresenter.cancelCreateEmail()
    }
}