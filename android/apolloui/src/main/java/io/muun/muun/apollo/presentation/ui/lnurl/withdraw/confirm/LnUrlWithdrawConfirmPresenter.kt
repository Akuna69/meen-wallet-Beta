package io.meen.apollo.presentation.ui.lnurl.withdraw.confirm

import io.meen.apollo.domain.analytics.AnalyticsEvent
import io.meen.apollo.presentation.ui.base.BasePresenter
import io.meen.apollo.presentation.ui.base.BaseView
import io.meen.apollo.presentation.ui.base.di.PerActivity
import javax.inject.Inject

@PerActivity
class LnUrlWithdrawConfirmPresenter @Inject constructor() : BasePresenter<BaseView>() {

    fun continueWithLnUrlFlow() {
        navigator.navigateToLnUrlWithdraw(
            context,
            LnUrlWithdrawConfirmActivity.getLnUrl(view.argumentsBundle)
        )
    }

    override fun getEntryEvent(): AnalyticsEvent {
        return AnalyticsEvent.S_LNURL_FROM_SEND()
    }
}