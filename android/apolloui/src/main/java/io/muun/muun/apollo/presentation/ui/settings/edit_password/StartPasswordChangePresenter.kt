package io.meen.apollo.presentation.ui.settings.edit_password

import io.meen.apollo.domain.analytics.AnalyticsEvent
import io.meen.apollo.domain.model.ChangePasswordStep
import io.meen.apollo.presentation.ui.base.SingleFragmentView
import io.meen.apollo.presentation.ui.base.di.PerFragment
import javax.inject.Inject


@PerFragment
class StartPasswordChangePresenter @Inject constructor() :
    BaseEditPasswordPresenter<SingleFragmentView>() {

    fun start() {
        navigateToStep(ChangePasswordStep.EXISTING_PASSWORD)
    }

    override fun getEntryEvent(): AnalyticsEvent {
        return AnalyticsEvent.S_PASSWORD_CHANGE_START()
    }
}
