package io.meen.apollo.presentation.ui.settings.flags

import android.os.Bundle
import io.meen.apollo.domain.FeatureOverrideStore
import io.meen.apollo.domain.model.MeenFeature
import io.meen.apollo.domain.selector.FeatureSelector
import io.meen.apollo.presentation.ui.adapter.viewmodel.FeatureFlagViewModel
import io.meen.apollo.presentation.ui.base.ParentPresenter
import io.meen.apollo.presentation.ui.base.SingleFragmentPresenter
import javax.inject.Inject

class DisableFeatureFlagsPresenter @Inject constructor(
    private val featureSelector: FeatureSelector,
    private val featureOverrideStore: FeatureOverrideStore,
) : SingleFragmentPresenter<DisableFeatureFlagsView, ParentPresenter>() {

    override fun setUp(arguments: Bundle) {
        super.setUp(arguments)

        view.setState(
            featureSelector.fetchOverridableFlags().toBlocking().first(),
            featureOverrideStore.getFeatureOverrides()
        )
    }

    fun toggleFeatureFlag(
        overridableFeature: MeenFeature.OverridableFeature.Overridable,
        state: FeatureFlagViewModel.State,
    ) {
        // If state was ENABLED -> disable, if state was DISABLED -> enable
        if (state == FeatureFlagViewModel.State.ENABLED) {
            featureOverrideStore.disableFeatureFlag(overridableFeature)
        } else {
            featureOverrideStore.enableFeatureFlag(overridableFeature)
        }
    }
}