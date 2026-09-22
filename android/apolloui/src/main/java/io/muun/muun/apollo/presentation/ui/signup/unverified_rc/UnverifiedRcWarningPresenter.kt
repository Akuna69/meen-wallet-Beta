package io.meen.apollo.presentation.ui.signup.unverified_rc

import io.meen.apollo.domain.analytics.AnalyticsEvent
import io.meen.apollo.presentation.ui.base.BaseView
import io.meen.apollo.presentation.ui.base.ParentPresenter
import io.meen.apollo.presentation.ui.base.SingleFragmentPresenter
import javax.inject.Inject

class UnverifiedRcWarningPresenter @Inject constructor() :
    SingleFragmentPresenter<BaseView, ParentPresenter>() {

    override fun getEntryEvent(): AnalyticsEvent =
        AnalyticsEvent.S_UNVERIFIED_RC_WARNING()

    fun proceedToHome() {
        navigator.navigateToHome(context)
        view.finishActivity()
    }
}
