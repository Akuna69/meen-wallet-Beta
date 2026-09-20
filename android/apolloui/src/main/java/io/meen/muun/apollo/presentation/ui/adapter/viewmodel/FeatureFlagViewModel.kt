package io.meen.apollo.presentation.ui.adapter.viewmodel

import io.meen.apollo.domain.model.MeenFeature
import io.meen.apollo.presentation.ui.adapter.holder.ViewHolderFactory

class FeatureFlagViewModel(
    val overridableFeature: MeenFeature.OverridableFeature.Overridable,
    val state: State,
) : ItemViewModel {

    enum class State {
        ENABLED,
        DISABLED
    }

    override fun type(typeFactory: ViewHolderFactory): Int {
        return typeFactory.getLayoutRes(this)
    }
}