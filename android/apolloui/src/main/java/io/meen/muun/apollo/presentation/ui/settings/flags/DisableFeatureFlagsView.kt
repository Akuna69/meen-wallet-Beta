package io.meen.apollo.presentation.ui.settings.flags

import io.meen.apollo.domain.model.MeenFeature
import io.meen.apollo.presentation.ui.base.BaseView

interface DisableFeatureFlagsView : BaseView {

    fun setState(
        features: List<MeenFeature.OverridableFeature.Overridable>,
        featureOverrides: List<MeenFeature.OverridableFeature.Overridable>,
    )

}