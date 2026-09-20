package io.meen.apollo.presentation.ui.settings.flags

import io.meen.apollo.domain.model.MuunFeature
import io.meen.apollo.presentation.ui.base.BaseView

interface DisableFeatureFlagsView : BaseView {

    fun setState(
        features: List<MuunFeature.OverridableFeature.Overridable>,
        featureOverrides: List<MuunFeature.OverridableFeature.Overridable>,
    )

}