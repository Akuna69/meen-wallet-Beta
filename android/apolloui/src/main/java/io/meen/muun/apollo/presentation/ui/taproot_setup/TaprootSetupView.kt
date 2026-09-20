package io.meen.apollo.presentation.ui.taproot_setup

import android.os.Bundle
import io.meen.apollo.presentation.ui.base.BaseView

interface TaprootSetupView: BaseView {

    fun goToStep(step: TaprootSetupStep, args: Bundle)

}