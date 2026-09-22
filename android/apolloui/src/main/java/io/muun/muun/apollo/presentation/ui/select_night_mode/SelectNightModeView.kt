package io.meen.apollo.presentation.ui.select_night_mode

import io.meen.apollo.domain.model.NightMode
import io.meen.apollo.presentation.ui.base.BaseView

interface SelectNightModeView : BaseView {

    fun setNightMode(nightMode: NightMode)

}