package io.meen.apollo.presentation.ui.fragments.ek_verify_help

import io.meen.apollo.domain.analytics.AnalyticsEvent
import io.meen.apollo.presentation.ui.base.BaseView
import io.meen.apollo.presentation.ui.base.SingleFragmentPresenter
import io.meen.apollo.presentation.ui.base.di.PerFragment
import javax.inject.Inject

@PerFragment
class EmergencyKitVerifyHelpPresenter @Inject constructor():
    SingleFragmentPresenter<BaseView, EmergencyKitVerifyHelpParentPresenter>() {

    override fun getEntryEvent(): AnalyticsEvent {
        return AnalyticsEvent.S_EMERGENCY_KIT_HELP()
    }

    fun goBack() {
        parentPresenter.cancelEmergencyKitVerifyHelp()
    }
}