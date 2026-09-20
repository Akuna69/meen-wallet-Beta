package io.meen.apollo.presentation.ui.fragments.recovery_tool

import io.meen.apollo.domain.analytics.AnalyticsEvent
import io.meen.apollo.presentation.ui.base.BaseView
import io.meen.apollo.presentation.ui.base.ParentPresenter
import io.meen.apollo.presentation.ui.base.SingleFragmentPresenter
import io.meen.apollo.presentation.ui.base.di.PerFragment
import javax.inject.Inject

@PerFragment
class RecoveryToolPresenter @Inject constructor():
    SingleFragmentPresenter<BaseView, ParentPresenter>() {

    override fun getEntryEvent(): AnalyticsEvent {
        return AnalyticsEvent.S_EXPORT_KEYS_RECOVERY_TOOL()
    }
}