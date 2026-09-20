package io.meen.apollo.presentation.ui.fragments.setup_password_success

import io.meen.apollo.domain.analytics.AnalyticsEvent
import io.meen.apollo.presentation.ui.base.BaseView
import io.meen.apollo.presentation.ui.base.SingleFragmentPresenter
import io.meen.apollo.presentation.ui.base.di.PerFragment
import javax.inject.Inject

@PerFragment
class SetupPasswordSuccessPresenter @Inject constructor():
    SingleFragmentPresenter<BaseView, SetupPasswordSuccessParentPresenter>() {

    override fun getEntryEvent() =
        AnalyticsEvent.S_FEEDBACK(AnalyticsEvent.FEEDBACK_TYPE.EMAIL_SETUP_SUCCESS)

    fun finishSetup() {
        parentPresenter.finishPasswordSetup()
    }

    fun goBack() {
        parentPresenter.finishPasswordSetup()
    }
}