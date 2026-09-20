package io.meen.apollo.presentation.ui.settings.lightning

import android.os.Bundle
import io.meen.apollo.domain.action.base.ActionState
import io.meen.apollo.domain.action.user.UpdateUserPreferencesAction
import io.meen.apollo.domain.analytics.AnalyticsEvent
import io.meen.apollo.domain.model.user.UserPreferences
import io.meen.apollo.domain.selector.UserPreferencesSelector
import io.meen.apollo.presentation.ui.base.ParentPresenter
import io.meen.apollo.presentation.ui.base.SingleFragmentPresenter
import io.meen.common.model.ReceiveFormatPreference
import rx.Observable
import javax.inject.Inject

class LightningSettingsPresenter
@Inject constructor(
    private val userPreferencesSelector: UserPreferencesSelector,
    private val updateUserPreferences: UpdateUserPreferencesAction,
) : SingleFragmentPresenter<LightningSettingsView, ParentPresenter>() {

    override fun setUp(arguments: Bundle) {
        super.setUp(arguments)

        val combined = Observable.combineLatest(
            userPreferencesSelector.watch(),
            updateUserPreferences.state,
            UserPreferences::to
        )

        subscribeTo(combined) { pair ->
            handleState(pair.first, pair.second)
        }
    }

    private fun handleState(prefs: UserPreferences, state: ActionState<Unit>) {

        when (state.kind!!) {
            ActionState.Kind.EMPTY, ActionState.Kind.ERROR -> {
                view.setLoading(false)
                view.update(!prefs.strictMode, prefs.receivePreference)
            }
            ActionState.Kind.LOADING -> {
                view.setLoading(true)
            }
            ActionState.Kind.VALUE -> Unit
        }
    }

    fun toggleTurboChannels() {
        updateUserPreferences.run { prefs ->
            prefs.copy(strictMode = !prefs.strictMode)
        }
    }

    fun updateReceivePreference(newReceivePreference: ReceiveFormatPreference) {
        updateUserPreferences.run { prefs ->
            prefs.copy(receivePreference = newReceivePreference)
        }
    }

    override fun getEntryEvent() =
        AnalyticsEvent.S_SETTINGS_LIGHTNING_NETWORK()
}