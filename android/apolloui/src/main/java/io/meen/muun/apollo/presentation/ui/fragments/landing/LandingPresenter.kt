package io.meen.apollo.presentation.ui.fragments.landing

import io.meen.apollo.domain.action.fcm.ForceFetchFcmAction
import io.meen.apollo.domain.analytics.AnalyticsEvent
import io.meen.apollo.domain.analytics.AnalyticsEvent.S_GET_STARTED
import io.meen.apollo.presentation.ui.base.SingleFragmentPresenter
import io.meen.apollo.presentation.ui.base.SingleFragmentView
import io.meen.apollo.presentation.ui.base.di.PerFragment
import io.meen.apollo.presentation.ui.signup.SignupPresenter
import javax.inject.Inject

@PerFragment
open class LandingPresenter @Inject constructor(private val forceFetchFcm: ForceFetchFcmAction) :
    SingleFragmentPresenter<SingleFragmentView, SignupPresenter>() {

    override fun onSetUpFinished() {
        super.onSetUpFinished()
        parentPresenter.resumeSignupIfStarted()
        assertGooglePlayServicesPresent()

        // "Fire and forget" attempt to retrieve FCM token so its already available at wallet
        // creation or recovery.
        forceFetchFcm.run()
    }

    /**
     * Start wallet creation flow.
     */
    fun startCreateWallet() {
        parentPresenter.startSignup()
    }

    /**
     * Start wallet recovery flow.
     */
    fun startRecoverWallet() {
        parentPresenter.startLogin()
    }

    override fun getEntryEvent(): AnalyticsEvent? {
        return S_GET_STARTED()
    }
}