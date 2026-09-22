package io.meen.apollo.presentation.ui.fragments.password_setup_intro

import android.os.Bundle
import io.meen.apollo.domain.analytics.AnalyticsEvent
import io.meen.apollo.domain.model.SecurityCenter
import io.meen.apollo.domain.selector.UserPreferencesSelector
import io.meen.apollo.presentation.ui.base.SingleFragmentPresenter
import io.meen.apollo.presentation.ui.base.di.PerFragment
import javax.inject.Inject

@PerFragment
class SetupPasswordIntroPresenter @Inject constructor(
    private val userPreferencesSel: UserPreferencesSelector,
) : SingleFragmentPresenter<SetupPasswordIntroView, SetupPasswordIntroParentPresenter>() {

    override fun setUp(arguments: Bundle) {
        super.setUp(arguments)

        view.setSecurityLevel(
            SecurityCenter(userSel.get(), userPreferencesSel.emailSetupSkipped()).getLevel()
        )
    }

    fun startSetup() {
        parentPresenter.startPasswordSetup()
    }

    fun goBack() {
        parentPresenter.cancelIntro()
    }

    fun skipEmailSetup() {
        parentPresenter.skipCreateEmail()
    }

    override fun getEntryEvent() =
        AnalyticsEvent.S_EMAIL_PRIMING()
}