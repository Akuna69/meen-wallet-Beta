package io.meen.apollo.presentation.ui.export_keys

import io.meen.apollo.presentation.ui.base.BaseView

interface EmergencyKitView: BaseView {

    fun refreshToolbar()

    fun goToStep(step: EmergencyKitStep)

    fun showSaveAbortDialog()
}