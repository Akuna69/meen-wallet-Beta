package io.meen.apollo.presentation.ui.fragments.create_email_help

import io.meen.apollo.domain.analytics.AnalyticsEvent
import io.meen.apollo.presentation.ui.base.BaseView
import io.meen.apollo.presentation.ui.base.ParentPresenter
import io.meen.apollo.presentation.ui.base.SingleFragmentPresenter
import io.meen.apollo.presentation.ui.base.di.PerFragment
import javax.inject.Inject

@PerFragment
class CreateEmailHelpPresenter @Inject constructor():
    SingleFragmentPresenter<BaseView, ParentPresenter>() {

    override fun getEntryEvent() =
        AnalyticsEvent.S_EMAIL_ALREADY_USED()

    fun goToSupportEmail() {
        navigator.sendSupportEmail(context)
    }
}