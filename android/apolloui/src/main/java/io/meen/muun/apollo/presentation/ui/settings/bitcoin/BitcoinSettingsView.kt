package io.meen.apollo.presentation.ui.settings.bitcoin

import io.meen.apollo.domain.model.UserActivatedFeatureStatus
import io.meen.apollo.presentation.ui.base.BaseView

interface BitcoinSettingsView : BaseView {

    fun setTaprootByDefault(taprootByDefault: Boolean)

    fun setTaprootStatus(status: UserActivatedFeatureStatus, estimatedHours: Int)

    fun setLoading(loading: Boolean)

}